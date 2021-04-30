package com.example.bjit_asr.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Conversation {
    @PrimaryKey(autoGenerate = true)
    public long conversationId;

    @ColumnInfo(name = "saved-time")
    public String saveAt ;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "details")
    public String details;

    @ColumnInfo(name = "is_remote_conversation")
    public boolean isConversationRemote;

    @ColumnInfo(name = "remote_conversation_id")
    public String remoteConversationId;

    public Conversation(){}
}
