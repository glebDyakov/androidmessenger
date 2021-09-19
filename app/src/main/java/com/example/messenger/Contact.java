package com.example.messenger;

public class Contact {

    private String contactname; // имя игрока
    private String message; // последнее сообщение
    private int contactID; // идентификатор игрока (в данном случае это порт сокета)

    public Contact() {
    }

    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactID) {
        this.contactID = contactID;
    }

    public String getContactname() {
        return contactname;
    }

    public void setContactname(String contactname) {
        this.contactname = contactname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
