package com.example.bjit_asr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Toast;

import com.example.bjit_asr.Models.RemoteMessage;
import com.example.bjit_asr.Models.RemoteUser;
import com.example.bjit_asr.ui.RemoteConversation.RemoteMessageAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.bjit_asr.utils.FirebaseUtils.getDbRef;
import static com.example.bjit_asr.utils.Utils.REQUEST_RECORD_AUDIO_PERMISSION_CODE;
import static com.example.bjit_asr.utils.Utils.getDeviceUniqueId;
import static com.example.bjit_asr.utils.Utils.getRecognitionProgressViewColor;
import static com.example.bjit_asr.utils.Utils.muteDevice;
import static com.example.bjit_asr.utils.Utils.showSnackMessage;

public class RemoteConversation extends AppCompatActivity implements RecognitionListener {

    SpeechRecognizer speechRecognizer;
    RecognitionProgressView recognitionProgressView;
    AudioManager audioManager;
    int deviceSystemVolume;
    boolean isRecognizeListening;
    MaterialButton listen;
    RecyclerView remoteConverRecyclerView;
    RemoteMessageAdapter remoteMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_conversation);


        audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        isRecognizeListening = false;
        recognitionProgressView = findViewById(R.id.recognition_view);
        listen = findViewById(R.id.listen);
        remoteConverRecyclerView = findViewById(R.id.remote_conversations);

        recognitionProgressView.setColors(getRecognitionProgressViewColor(this));
        recognitionProgressView.play();


        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(RemoteConversation.this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestAudioPermission();
                } else {
                    callStartRecognition();
                }
            }
        });

        FirebaseRecyclerOptions<RemoteMessage> options =
                new FirebaseRecyclerOptions.Builder<RemoteMessage>()
                        .setQuery(getDbRef().child(getDeviceUniqueId(this)), RemoteMessage.class)
                        .build();

        remoteMessageAdapter = new RemoteMessageAdapter(this,options);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        remoteConverRecyclerView.setLayoutManager(mLinearLayoutManager);
        remoteConverRecyclerView.setAdapter(remoteMessageAdapter);


    }

    @Override
    public void onPause() {
        remoteMessageAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        remoteMessageAdapter.startListening();
    }

    private void callStartRecognition(){
        if(isRecognizeListening){
            speechRecognizer.destroy();
            listen.setText("Start");
            isRecognizeListening = false;
            recognitionProgressView.stop();
            recognitionProgressView.setVisibility(View.GONE);
            return;
        }
        startRecognition();
        recognitionProgressView.postDelayed(new Runnable() {
            @Override
            public void run() {
                startRecognition();
            }
        }, 50);
        listen.setText("Stop Listening");
    }

    private void startRecognition() {

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognitionProgressView.setVisibility(View.VISIBLE);
        recognitionProgressView.setSpeechRecognizer(speechRecognizer);
        recognitionProgressView.setRecognitionListener(this);
        recognitionProgressView.play();

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");

        deviceSystemVolume = muteDevice(audioManager);
        speechRecognizer.startListening(intent);
        isRecognizeListening = true;
    }

    private void requestAudioPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this, "Requires RECORD_AUDIO permission", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.RECORD_AUDIO },
                    REQUEST_RECORD_AUDIO_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callStartRecognition();
                } else {
                    Toast.makeText(this, "Permissions Denied to record audio", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {

    }

    @Override
    public void onError(int error) {

    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        showToast(matches.get(0));
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        RemoteUser remoteUser = new RemoteUser();
        remoteUser.setUserId(getDeviceUniqueId(this));
        remoteUser.setUserName("Test bb");

        RemoteMessage remoteMessage = new RemoteMessage(msg,remoteUser,String.valueOf(Calendar.getInstance().getTime()));

        getDbRef().child(remoteUser.getUserId()).push().setValue(remoteMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RemoteConversation.this, "added", Toast.LENGTH_SHORT).show();
            }
        });

        recognitionProgressView.stop();
        startRecognition();
    }

}