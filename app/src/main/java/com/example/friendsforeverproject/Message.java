package com.example.friendsforeverproject;

import com.google.firebase.Timestamp;

public class Message {
    private String senderId;
    private String receiverId;
    private String text;
    private Timestamp timestamp;

    public Message() {} // Firestore needs no-arg constructor

    public Message(String senderId, String receiverId, String text, Timestamp timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getText() { return text; }
    public Timestamp getTimestamp() { return timestamp; }

    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public void setText(String text) { this.text = text; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
