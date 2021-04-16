package com.example.bjit_asr.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

@Entity
public class RecognizeText {
    @PrimaryKey(autoGenerate = true)
    private long recognizeTextId;

    private String text;
    private String time;
    private long conversationId;

    public long getRecognizeTextId() {
        return recognizeTextId;
    }

    public void setRecognizeTextId(long recognizeTextId) {
        this.recognizeTextId = recognizeTextId;
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public RecognizeText(){}

    public RecognizeText(String _text,String _time,long _id){
        this.text = _text;
        this.time = _time;
        this.recognizeTextId = _id;
    }

    public RecognizeText(String _text,String _time){
        this.text = _text;
        this.time = _time;
    }

    public RecognizeText(String _text){
        this.text = _text;
        this.time = getCurrentDateTime();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static ArrayList<RecognizeText> mockDatas(int numContacts) {
        ArrayList<RecognizeText> contacts = new ArrayList<RecognizeText>();

        for (int i = 1; i <= numContacts; i++) {
            contacts.add(new RecognizeText("Hello ! I am Tick Talk . I can recognize your speech",getCurrentDateTime()));
        }

        return contacts;
    }

    private static String getCurrentDateTime(){
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        return df.format(Calendar.getInstance().getTime());
    }
}
