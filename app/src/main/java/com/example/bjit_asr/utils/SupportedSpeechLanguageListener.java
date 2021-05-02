package com.example.bjit_asr.utils;

import java.util.List;

public interface SupportedSpeechLanguageListener {
    public void onLanguageBroadcastReceiveListener(List<String> supportedLanguages);
    public void onNoLanguageDetected(String msg);
}
