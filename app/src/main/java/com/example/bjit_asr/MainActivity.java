package com.example.bjit_asr;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.bjit_asr.Models.Conversation;
import com.example.bjit_asr.Models.ConversationWithTexts;
import com.example.bjit_asr.Models.RecognizeText;
import com.example.bjit_asr.database.AppDatabase;
import com.example.bjit_asr.ui.Home.ConversationAdapter;
import com.example.bjit_asr.ui.Home.RecognizeTextAdapter;
import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.functions.Consumer;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity implements RecognitionListener,Function1<MeowBottomNavigation.Model, Unit> {

    private TextView speechText;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1;
    private SpeechRecognizer speechRecognizer;
    private RecognitionProgressView recognitionProgressView;
    private AudioManager audioManager;
    private int deviceSystemVolume ;
    private View speechContainer;
    private RecyclerView recognizeTextRecyclerView;
    private RecyclerView conversationRecyclerView;
    private ArrayList<RecognizeText> recognizeTexts;
    private RecognizeTextAdapter recognizeTextAdapter;
    private ConversationAdapter conversationAdapter;
    private MaterialButton listen;
    private MaterialButton saveConversation;
    private boolean isRecognizeListening;

    AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isRecognizeListening = false;
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "bsbc-asr-ticktalk.db")
                .allowMainThreadQueries()
                .build();
        audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);

        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottom);
        speechText = findViewById(R.id.speech_text);
        speechContainer = findViewById(R.id.speech_container);
        listen = findViewById(R.id.listen);
        saveConversation = findViewById(R.id.save_conversation);
        recognitionProgressView = (RecognitionProgressView) findViewById(R.id.recognition_view);
        recognizeTextRecyclerView = (RecyclerView) findViewById(R.id.recognize_texts_recyclerview);
        conversationRecyclerView = findViewById(R.id.all_conversations);

        bottomNavigation.add(new MeowBottomNavigation.Model(R.drawable.ic_baseline_bookmark_border_24, R.drawable.ic_baseline_bookmark_border_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(R.drawable.ic_baseline_mic_none_24, R.drawable.ic_baseline_mic_none_24));
        bottomNavigation.show(R.drawable.ic_baseline_bookmark_border_24, true);

        bottomNavigation.setOnClickMenuListener(this);
        bottomNavigation.setOnShowListener(this);

        int[] colors = {
                ContextCompat.getColor(this, R.color.yellow),
                ContextCompat.getColor(this, R.color.blue),
                ContextCompat.getColor(this, R.color.purple_200),
                ContextCompat.getColor(this, R.color.black),
                ContextCompat.getColor(this, R.color.red)
        };

        recognitionProgressView.setColors(colors);
        recognitionProgressView.play();

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermission();
                } else {
                    callStartRecognition();
                }
            }
        });

        saveConversation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Conversation conversation = new Conversation();
                conversation.title = "titles";
                conversation.details = "blah blah";
                long conversationId = db.conversationDao().insertOne(conversation);

                List<RecognizeText> recognizeTextsForDb = new ArrayList<>();
                for (RecognizeText recText: recognizeTexts) {
                    recText.setConversationId(conversationId);
                    recognizeTextsForDb.add(recText);
                }

                db.recognizeTextDao().insertAll(recognizeTextsForDb);
            }
        });

        db.conversationDao().getConversationWithTexts().subscribe(new Consumer<List<ConversationWithTexts>>() {
            @Override
            public void accept(@NonNull List<ConversationWithTexts> conversationWithTexts) throws Exception {
                handleResponse(conversationWithTexts);
            }
        });

    }

    private void handleResponse(List<ConversationWithTexts> conversationWithTexts){
        Log.e("student size :",conversationWithTexts.size()+"");
        List<Conversation> conversationsList = new ArrayList<>();
        for (int i=0;i<conversationWithTexts.size();i++){
            conversationsList.add(conversationWithTexts.get(i).conversation);
        }

        conversationRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        conversationAdapter = new ConversationAdapter(conversationsList);
        conversationRecyclerView.setAdapter(conversationAdapter);
    }

    private void updateRecyclerView(String data){
        recognizeTexts.add(new RecognizeText(data));
        recognizeTextAdapter.notifyDataSetChanged();
        recognizeTextRecyclerView.scrollToPosition(recognizeTexts.size()-1);
    }


    private void initializeRecognizeTextRecyclerView(){
        recognizeTexts = new ArrayList<>() ;
        recognizeTextAdapter = new RecognizeTextAdapter(recognizeTexts);
        recognizeTextRecyclerView.setAdapter(recognizeTextAdapter);
        recognizeTextRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        unMuteDevice(deviceSystemVolume);
        super.onDestroy();
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

    private void callStartRecognition(){
        if(isRecognizeListening){
            speechRecognizer.destroy();
            listen.setText("Listen");
            listen.setBackgroundColor(getColor(R.color.blue));
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
        listen.setBackgroundColor(getColor(R.color.red));
    }
    private int muteDevice(){
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        return audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
    }

    private void unMuteDevice(int volume){
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, volume,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
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

        deviceSystemVolume = muteDevice();
        speechRecognizer.startListening(intent);
        isRecognizeListening = true;
    }

    private void showResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        updateRecyclerView(matches.get(0));
        recognitionProgressView.stop();
        startRecognition();
    }

    private void requestPermission() {
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
    public Unit invoke(MeowBottomNavigation.Model model) {
        int id = model.getId();
        switch (id) {
            case R.drawable.ic_baseline_bookmark_border_24:
                speechContainer.setVisibility(View.GONE);
                if(speechRecognizer!=null){
                    recognitionProgressView.stop();
                    recognitionProgressView.setVisibility(View.GONE);
                    speechRecognizer.cancel();
                }
                listen.setText("Listen");
                listen.setBackgroundColor(getColor(R.color.blue));
                break;
            case R.drawable.ic_baseline_mic_none_24:
                speechContainer.setVisibility(View.VISIBLE);
                initializeRecognizeTextRecyclerView();
                break;
        }
        return null;
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        unMuteDevice(deviceSystemVolume);
    }

    @Override
    public void onError(int i) {
        switch (i){
            case SpeechRecognizer.ERROR_NO_MATCH:
                showToast("No Match");
                startRecognition();
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                showToast("Time out");
                startRecognition();
                break;
        }
    }

    @Override
    public void onResults(Bundle bundle) {
        showResults(bundle);
        unMuteDevice(deviceSystemVolume);
    }

    @Override
    public void onPartialResults(Bundle bundle) {

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}