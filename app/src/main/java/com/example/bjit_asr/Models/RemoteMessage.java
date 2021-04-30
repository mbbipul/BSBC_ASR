package com.example.bjit_asr.Models;

public class RemoteMessage {
    String message;
    RemoteUser sender;
    String createdAt;
    boolean isSender;

    public String getMessage() {
        return message;
    }

    public RemoteMessage(String message, RemoteUser sender, String createdAt,boolean isSender) {
        this.message = message;
        this.sender = sender;
        this.createdAt = createdAt;
        this.isSender = isSender;
    }

    public RemoteMessage(){}

    public void setMessage(String message) {
        this.message = message;
    }

    public RemoteUser getSender() {
        return sender;
    }

    public void setSender(RemoteUser sender) {
        this.sender = sender;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean getIsSender() {
        return isSender;
    }

    public void setIsSender(boolean sender) {
        isSender = sender;
    }

}
