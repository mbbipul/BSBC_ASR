package com.example.bjit_asr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.bjit_asr.Models.Conversation;
import com.example.bjit_asr.Models.RecognizeText;
import com.example.bjit_asr.database.AppDatabase;
import com.example.bjit_asr.database.AppDb;
import com.example.bjit_asr.ui.Home.RecognizeTextAdapter;
import com.example.bjit_asr.utils.Utils;

import java.util.List;

import io.reactivex.functions.Consumer;

public class ConversationsActivity extends AppCompatActivity {

    AppDatabase db;
    RecyclerView  recognizeTextRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        recognizeTextRecyclerView = findViewById(R.id.recognize_texts_recyclerview);
        db = AppDb.getInstance(this);

        Intent intent = getIntent();
        long conversationId = intent.getLongExtra("conversationId",-23);
        String conversatonTitle = intent.getStringExtra("conversationTitle");

        setTitle(conversatonTitle);

        if (conversationId > 0){
            db.recognizeTextDao().getRecognizeTextsByConversation(conversationId)
                    .subscribe(new Consumer<List<RecognizeText>>() {
                @Override
                public void accept(@NonNull List<RecognizeText> recognizeTexts) throws Exception {
                    onTextRecognitionFetch(recognizeTexts);
                }
            });
        }else {
            Utils.showSnackMessage(recognizeTextRecyclerView,"Invalid conversation ID");
        }

    }

    private void onTextRecognitionFetch(List<RecognizeText> recognizeTexts){
        RecognizeTextAdapter recognizeTextAdapter = new RecognizeTextAdapter(recognizeTexts);

        recognizeTextRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recognizeTextRecyclerView.setAdapter(recognizeTextAdapter);
    }
}