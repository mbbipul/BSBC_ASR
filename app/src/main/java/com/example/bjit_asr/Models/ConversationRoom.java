package com.example.bjit_asr.Models;

import java.util.Date;

public class ConversationRoom {
    private RemoteUser createdBy;
    private String createdAt;
    private boolean status;

    public ConversationRoom(RemoteUser createdBy, String createdAt, boolean status) {
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.status = status;
    }

    public ConversationRoom(){}

    public RemoteUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(RemoteUser createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
