package com.example.bjit_asr.Models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RecognizeText {
    private String text;
    private String time;

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
