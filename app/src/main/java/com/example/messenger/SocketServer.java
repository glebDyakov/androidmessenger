package com.example.messenger;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class SocketServer extends Thread  implements ContactManager.ContactManagerDelegate {

    public interface OnMessageReceived {
        void messageReceived(String message, Contact from); // отправка сообщения в UI-поток

        void updatePlayerList(ArrayList<ContactManager> connectedUsers); // обновление списка при подключении\отключении
    }

    private OnMessageReceived messageListener;
    private boolean running = false; // флаг для проверки, запущен ли сервер
    private ServerSocket serverSocket; // экземпляр класса ServerSocket
    private ArrayList<ContactManager> connectedContacts; // список подключенных игроков

    public SocketServer(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
        connectedContacts = new ArrayList<>();
    }

    private void runServer() {
        running = true;

        try {
            // создаём серверный сокет, он будет прослушивать порт на наличие запросов
            serverSocket = new ServerSocket(8080);

            while (running) {
                // запускаем бесконечный цикл, внутри которого сокет будет слушать соединения и обрабатывать их
                // создаем клиентский сокет, метод accept() создаёт экземпляр Socket при новом подключении
                Socket client = serverSocket.accept();

                ContactManager contactManager = new ContactManager(client, this);
                connectedContacts.add(contactManager);
                contactManager.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (connectedContacts != null) { // закрытие всех соединений с клиентами
            for (ContactManager contactManager : connectedContacts) {
                contactManager.close();
            }
        }

        running = false;
        // закрытие сервера
        try {
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        serverSocket = null;
    }

    @Override public void run() {
        super.run();
        runServer();
    }

    public void sendMessage(Contact contact) {
        if (connectedContacts != null) {
            for (ContactManager contactManager : connectedContacts) {
                if (contactManager.getContact().getContactID() != contact.getContactID()) {
                    contactManager.sendMessage(contact.getMessage()); // если идентификатор пользователя не равен идентификатору отправившего сообщение - отправляем ответ
                }
            }
        }
    }

    public void sendMessageTo(int id, String msg) {
        if (connectedContacts != null) {
            for (ContactManager contactManager : connectedContacts) {
                if (contactManager.getContact().getContactID() == id) {
                    contactManager.sendMessage(msg); // если идентификатор пользователя равен заданному - отправляем ответ
                }
            }
        }
    }

    public void sendToAll(String msg) {
        if (connectedContacts != null) {
            for (ContactManager contactManager : connectedContacts) {
                contactManager.sendMessage(msg); // ищем всех пользователей в списке и отправляем ответ
            }
        }
    }

    @Override
    public void contactConnected(Contact connectedContact) {
        messageListener.updatePlayerList(connectedContacts);
    }

    @Override
    public void contactDisconnected(ContactManager contactManager, String contactname) {
        connectedContacts.remove(contactManager);
        messageListener.updatePlayerList(connectedContacts);
    }

    @Override
    public void messageReceived(Contact fromContact, Contact toContact) {
        messageListener.messageReceived(fromContact.getMessage(), fromContact);
        sendMessage(fromContact);
    }

}
