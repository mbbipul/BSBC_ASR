package com.example.bjit_asr.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import java.util.List;

public class SpeechRecLangDetailsChecker extends BroadcastReceiver
{
    private List<String> supportedLanguages;

    private String languagePreference;

    SupportedSpeechLanguageListener supportedSpeechLanguageListener;

    public SpeechRecLangDetailsChecker(SupportedSpeechLanguageListener _listener){
        this.supportedSpeechLanguageListener = _listener;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle results = getResultExtras(true);
        Bundle extras = getResultExtras(true);
        supportedLanguages = extras.getStringArrayList
                (RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES);
        if (supportedLanguages == null) {
            supportedSpeechLanguageListener.onNoLanguageDetected("No voice data found.");
        } else {
            supportedSpeechLanguageListener.onLanguageBroadcastReceiveListener(supportedLanguages);
        }

    }
}