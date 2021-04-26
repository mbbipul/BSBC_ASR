package com.example.bjit_asr.Models;

public class RemoteMessage {
    String message;
    RemoteUser sender;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RemoteUser getSender() {
        return sender;
    }

    public void setSender(RemoteUser sender) {
        this.sender = sender;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    long createdAt;
}
