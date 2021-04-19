package com.example.bjit_asr.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.bjit_asr.Models.Conversation;
import com.example.bjit_asr.Models.ConversationWithTexts;
import com.example.bjit_asr.Models.RecognizeText;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface ConversationDao {
    @Query("SELECT * FROM conversation")
    List<Conversation> getAll();

    @Query("SELECT * FROM conversation WHERE conversationId IN (:textIds)")
    Flowable<List<Conversation>> loadAllByIds(int[] textIds);

    @Query("SELECT * FROM conversation WHERE conversationId = :textId")
    Single<Conversation> loadById(long textId);

    @Transaction
    @Query("SELECT * FROM conversation")
    Flowable<List<ConversationWithTexts>> getConversationWithTexts();

    @Insert
    long insertOne(Conversation conversation);

    @Insert
    void insertAll(Conversation... conversations);

    @Delete
    void delete(Conversation conversation);
}
