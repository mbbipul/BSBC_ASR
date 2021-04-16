package com.example.bjit_asr.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.bjit_asr.Models.RecognizeText;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface RecognizeTextDao {
    @Query("SELECT * FROM recognizetext")
    Flowable<List<RecognizeText>> getAll();

    @Query("SELECT * FROM recognizetext WHERE recognizeTextId IN (:textIds)")
    Flowable<List<RecognizeText>> loadAllByIds(int[] textIds);

    @Insert
    void insertAll(List<RecognizeText> recognizeTexts);

    @Delete
    void delete(RecognizeText recognizeText);
}
