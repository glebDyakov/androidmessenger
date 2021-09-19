package com.example.messenger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ContactManager extends Thread {

    private Contact contact; // экземпляр класса User, хранящий информацию о пользователе
    private Socket socket; // сокет, созданный при подключении пользователя
    private PrintWriter bufferSender;
    private boolean running; // флаг для проверки, запущен ли сокет
    private ContactManagerDelegate managerDelegate; // экземпляр интерфейса UserManagerDelegate

    public ContactManager(Socket socket, ContactManagerDelegate managerDelegate) {
        this.contact = new Contact();
        this.socket = socket;
        this.managerDelegate = managerDelegate;
        running = true;
    }

    public Contact getContact() {
        return contact;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override public void run() {
        super.run();
        try {
            // отправляем сообщение клиенту
            bufferSender =
                    new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                            true);

            // читаем сообщение от клиента
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // в бесконечном цикле ждём сообщения от клиента и смотрим, что там
            while (running) {
                String message = null;
                try {
                    message = in.readLine();
                } catch (IOException e) {
                }

                // проверка на команды
                if (hasCommand(message)) {
                    continue;
                }

                if (message != null && managerDelegate != null) {
                    contact.setMessage(message); // сохраняем сообщение
                    managerDelegate.messageReceived(contact, null); // уведомляем сервер о сообщении
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void close() {
        running = false;

        if (bufferSender != null) {
            bufferSender.flush();
            bufferSender.close();
            bufferSender = null;
        }

        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        socket = null;
    }

    public void sendMessage(String message) {
        if (bufferSender != null && !bufferSender.checkError()) {
            bufferSender.println(message);
            bufferSender.flush();
        }
    }

    public boolean hasCommand(String message) {
        if (message != null) {
            if (message.contains("Constants.CLOSED_CONNECTION")) {
                close();
                managerDelegate.contactDisconnected(this, contact.getContactname());
                return true;
            } else if (message.contains("Constants.LOGIN_NAME")) {
                contact.setContactname(message.replaceAll("Constants.LOGIN_NAME", ""));
                contact.setContactID(socket.getPort());
                managerDelegate.contactConnected(contact);
                return true;
            } else if (message.contains("Constants.PING")) {
                return true;
            }
        }

        return false;
    }

    // интерфейс, который передает результаты операций в SocketServer
    public interface ContactManagerDelegate {
        void contactConnected(Contact connectedContact);

        void contactDisconnected(ContactManager contactManager, String contactname);

        void messageReceived(Contact fromContact, Contact toContact);
    }

}
