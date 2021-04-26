package com.example.bjit_asr.Models;

public class RemoteMessage {
    String message;
    RemoteUser sender;

    public String getMessage() {
        return message;
    }

    public RemoteMessage(String message, RemoteUser sender, String createdAt) {
        this.message = message;
        this.sender = sender;
        this.createdAt = createdAt;
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
