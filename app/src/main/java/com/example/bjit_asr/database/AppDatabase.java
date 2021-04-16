package com.example.bjit_asr.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.bjit_asr.Models.Conversation;
import com.example.bjit_asr.Models.RecognizeText;

@Database(entities = {Conversation.class,RecognizeText.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecognizeTextDao recognizeTextDao();
    public abstract ConversationDao conversationDao();
}