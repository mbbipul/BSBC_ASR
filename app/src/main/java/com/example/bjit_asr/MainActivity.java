package com.example.bjit_asr;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity implements Function1<MeowBottomNavigation.Model, Unit> {

    private TextView speechText;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 1;
    private SpeechRecognizer speechRecognizer;
    private RecognitionProgressView recognitionProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottom);
        speechText = findViewById(R.id.speech_text);

        bottomNavigation.add(new MeowBottomNavigation.Model(R.drawable.ic_baseline_bookmark_border_24, R.drawable.ic_baseline_bookmark_border_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(R.drawable.ic_baseline_mic_none_24, R.drawable.ic_baseline_mic_none_24));
        bottomNavigation.show(R.drawable.ic_baseline_bookmark_border_24, true);

        bottomNavigation.setOnClickMenuListener(this);

        bottomNavigation.setOnShowListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                int id = model.getId();
                switch (id) {
                    case R.drawable.ic_baseline_bookmark_border_24:
                        Toast.makeText(MainActivity.this, "bookmark clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.drawable.ic_baseline_mic_none_24:
                        Toast.makeText(MainActivity.this, "speak clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
                return null;
            }
        });



        int[] colors = {
                ContextCompat.getColor(this, R.color.yellow),
                ContextCompat.getColor(this, R.color.blue),
                ContextCompat.getColor(this, R.color.purple_200),
                ContextCompat.getColor(this, R.color.black),
                ContextCompat.getColor(this, R.color.red)
        };


        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        recognitionProgressView = (RecognitionProgressView) findViewById(R.id.recognition_view);
        recognitionProgressView.setSpeechRecognizer(speechRecognizer);
        recognitionProgressView.setRecognitionListener(new RecognitionListenerAdapter() {
            @Override
            public void onResults(Bundle results) {
                showResults(results);
            }
        });
        recognitionProgressView.setColors(colors);
        recognitionProgressView.play();

        Button listen = (Button) findViewById(R.id.listen);

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermission();
                } else {
                    startRecognition();
                    recognitionProgressView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startRecognition();
                        }
                    }, 50);
                }
            }
        });

    }


    @Override
    protected void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }

    private void startRecognition() {
        recognitionProgressView.play();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");
        speechRecognizer.startListening(intent);
    }

    private void showResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        speechText.setText(matches.get(0));
        recognitionProgressView.stop();
        recognitionProgressView.play();    }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 345: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    speechText.setText(result.get(0));
                }
                break;
            }
        }
    }

    @Override
    public Unit invoke(MeowBottomNavigation.Model model) {
        int id = model.getId();
        switch (id) {
            case R.drawable.ic_baseline_bookmark_border_24:
                Toast.makeText(MainActivity.this, "bookmark clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.drawable.ic_baseline_mic_none_24:
//                promptSpeechInput();

                break;

        }
        return null;
    }
}