package com.example.bjit_asr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bjit_asr.Models.Conversation;
import com.example.bjit_asr.Models.RecognizeText;
import com.example.bjit_asr.Models.RemoteMessage;
import com.example.bjit_asr.database.AppDatabase;
import com.example.bjit_asr.database.AppDb;
import com.example.bjit_asr.ui.Home.RecognizeTextAdapter;
import com.example.bjit_asr.ui.RemoteConversation.RemoteMessageAdapter;
import com.example.bjit_asr.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

import io.reactivex.functions.Consumer;

import static com.example.bjit_asr.utils.FirebaseUtils.getDbRef;

public class ConversationsActivity extends AppCompatActivity {

    AppDatabase db;
    RecyclerView  recognizeTextRecyclerView;
    RemoteMessageAdapter remoteMessageAdapter;
    boolean isConversationRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        recognizeTextRecyclerView = findViewById(R.id.recognize_texts_recyclerview);
        db = AppDb.getInstance(this);

        Intent intent = getIntent();
        long conversationId = intent.getLongExtra("conversationId",-23);
        isConversationRemote = intent.getBooleanExtra("isConversationRemote",false);
        String conversationTitle = intent.getStringExtra("conversationTitle");
        String remoteConversationId = intent.getStringExtra("remoteConversationId");
        setTitle(conversationTitle);

        if (isConversationRemote){
            setTitle(conversationTitle+" - "+"Remote Conversation");
            Toast.makeText(this, String.valueOf(remoteConversationId), Toast.LENGTH_SHORT).show();

            recognizeTextRecyclerView.setVisibility(View.GONE);
            RecyclerView remoteRecyclerView = findViewById(R.id.remote_conversations);
            remoteRecyclerView.setVisibility(View.VISIBLE);

            FirebaseRecyclerOptions<RemoteMessage> options =
                    new FirebaseRecyclerOptions.Builder<RemoteMessage>()
                            .setQuery(getDbRef().child(remoteConversationId), RemoteMessage.class)
                            .build();

            remoteMessageAdapter = new RemoteMessageAdapter(this,options);

            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
            mLinearLayoutManager.setStackFromEnd(true);
            remoteRecyclerView.setLayoutManager(mLinearLayoutManager);
            remoteRecyclerView.setAdapter(remoteMessageAdapter);

        }
        else{
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

    }

    private void onTextRecognitionFetch(List<RecognizeText> recognizeTexts){
        RecognizeTextAdapter recognizeTextAdapter = new RecognizeTextAdapter(recognizeTexts);

        recognizeTextRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recognizeTextRecyclerView.setAdapter(recognizeTextAdapter);
    }

    @Override
    public void onPause() {
        if (isConversationRemote)
            remoteMessageAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isConversationRemote)
            remoteMessageAdapter.startListening();
    }
}