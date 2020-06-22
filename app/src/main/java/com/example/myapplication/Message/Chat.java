package com.example.myapplication.Message;


public class Chat {

    private String chat_ID;
    private String chat_senderID;
    private String chat_receiverID;
    private String message;

    public Chat(String chat_ID, String chat_senderID, String chat_receiverID, String message) {
        this.chat_ID = chat_ID;
        this.chat_senderID = chat_senderID;
        this.chat_receiverID = chat_receiverID;
        this.message = message;
    }

    public Chat() {
    }

    public String getChat_senderID() { return chat_senderID; }

    public void setChat_senderID(String chat_senderID) { this.chat_senderID = chat_senderID; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getChat_receiverID() { return chat_receiverID; }

    public void setChat_receiverID(String chat_receiverID) { this.chat_receiverID = chat_receiverID; }

    public String getChat_ID() { return chat_ID; }

    public void setChat_ID(String chat_ID) { this.chat_ID = chat_ID; }

}
