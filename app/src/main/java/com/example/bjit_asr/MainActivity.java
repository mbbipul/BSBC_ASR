package com.example.bjit_asr;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.bjit_asr.Models.Conversation;
import com.example.bjit_asr.Models.ConversationWithTexts;
import com.example.bjit_asr.Models.RecognizeText;
import com.example.bjit_asr.database.AppDatabase;
import com.example.bjit_asr.database.AppDb;
import com.example.bjit_asr.ui.Home.ConversationAdapter;
import com.example.bjit_asr.ui.Home.RecognizeTextAdapter;
import com.example.bjit_asr.utils.Keystores;
import com.example.bjit_asr.utils.SpeechRecLangDetailsChecker;
import com.example.bjit_asr.utils.SupportedSpeechLanguageListener;
import com.example.bjit_asr.utils.Utils;
import com.github.zagum.speechrecognitionview.RecognitionProgressView;
import com.github.zagum.speechrecognitionview.adapters.RecognitionListenerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static com.example.bjit_asr.utils.FirebaseUtils.ROOM_STATUS_PATH;
import static com.example.bjit_asr.utils.FirebaseUtils.getDbRef;
import static com.example.bjit_asr.utils.Utils.RECOGNIZER_LANGUAGE_KEY;
import static com.example.bjit_asr.utils.Utils.REQUEST_RECORD_AUDIO_PERMISSION_CODE;
import static com.example.bjit_asr.utils.Utils.generateRemoteConversationRoomId;
import static com.example.bjit_asr.utils.Utils.getDeviceUniqueId;
import static com.example.bjit_asr.utils.Utils.getRecognitionProgressViewColor;
import static com.example.bjit_asr.utils.Utils.muteDevice;
import static com.example.bjit_asr.utils.Utils.showSnackMessage;
import static com.example.bjit_asr.utils.Utils.unMuteDevice;

public class MainActivity extends AppCompatActivity implements RecognitionListener, Function1<MeowBottomNavigation.Model, Unit>, SupportedSpeechLanguageListener {

    private TextView remoteConCode;
    private SpeechRecognizer speechRecognizer;
    private RecognitionProgressView recognitionProgressView;
    private AudioManager audioManager;
    private int deviceSystemVolume ;
    private View speechContainer;
    private View remoteConversationContainer;
    private RecyclerView recognizeTextRecyclerView;
    private RecyclerView conversationRecyclerView;
    final private ArrayList<RecognizeText> recognizeTexts = new ArrayList<>() ;
    private RecognizeTextAdapter recognizeTextAdapter;
    private ConversationAdapter conversationAdapter;
    private MaterialButton listen;
    private MaterialButton saveConversation;
    private MaterialButton startRemoteConversation;
    private MaterialButton joinRemoteConversation;
    private ImageView remoteConversationQrImage;
    private boolean isRecognizeListening;
    private AppDatabase db;

    String conversationRoomId ;
    Keystores keystores ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isRecognizeListening = false;
        db = AppDb.getInstance(this);
        keystores = Keystores.getInstance(MainActivity.this);

        audioManager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);

        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottom);
        remoteConCode = findViewById(R.id.remote_conversation_code);
        speechContainer = findViewById(R.id.speech_container);
        remoteConversationContainer = findViewById(R.id.remote_conversation_container);
        listen = findViewById(R.id.listen);
        startRemoteConversation = findViewById(R.id.start_remote_conversation);
        joinRemoteConversation = findViewById(R.id.join_remote_conversation);
        remoteConversationQrImage = findViewById(R.id.remote_conversation_qr_code);
        saveConversation = findViewById(R.id.save_conversation);
        recognitionProgressView = (RecognitionProgressView) findViewById(R.id.recognition_view);
        recognizeTextRecyclerView = (RecyclerView) findViewById(R.id.recognize_texts_recyclerview);
        conversationRecyclerView = findViewById(R.id.all_conversations);

        bottomNavigation.add(new MeowBottomNavigation.Model(R.drawable.ic_baseline_speaker_notes_24, R.drawable.ic_baseline_speaker_notes_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(R.drawable.ic_baseline_mic_none_24, R.drawable.ic_baseline_mic_none_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(R.drawable.ic_baseline_online_prediction_24, R.drawable.ic_baseline_online_prediction_24));
        bottomNavigation.show(R.drawable.ic_baseline_speaker_notes_24, true);

        bottomNavigation.setOnClickMenuListener(this);
        bottomNavigation.setOnShowListener(this);

        recognitionProgressView.setColors(getRecognitionProgressViewColor(this));
        recognitionProgressView.play();

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestAudioPermission();
                } else {
                    callStartRecognition();
                }
            }
        });

        saveConversation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openSaveConversation();
            }
        });

        startRemoteConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent remoteConversation = new Intent(MainActivity.this,RemoteConversation.class);
                if(conversationRoomId != null){
                    remoteConversation.putExtra("conversationRoomId",conversationRoomId);
                    startActivity(remoteConversation);
                }else{
                    showToast("Something wrong !");
                }
            }
        });

        joinRemoteConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRemoteJoinDialog();
            }
        });

        if(keystores.get(RECOGNIZER_LANGUAGE_KEY)==null){
            keystores.put(RECOGNIZER_LANGUAGE_KEY,"en");
        }

    }

    private void getSpeechRecognizerLanguagesDetails(){
        Intent detailsIntent =  new Intent(RecognizerIntent.getVoiceDetailsIntent(this));
        sendOrderedBroadcast(
                detailsIntent, null, new SpeechRecLangDetailsChecker(this),
                null, Activity.RESULT_OK, null, null);
    }
    private void openRemoteJoinDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Join Remote Conversation");
        alertDialog.setMessage("Enter Code");

        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_baseline_leak_add_24);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent remoteConversation = new Intent(MainActivity.this,
                                RemoteConversation.class);
                        if(input.getText() != null){
                            remoteConversation.putExtra("conversationRoomId",
                                    input.getText().toString());

                            remoteConversation.putExtra("isFromJoin",
                                    true);

                            getDbRef().child(input.getText().toString()).child(ROOM_STATUS_PATH)
                                    .child("status").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if (!task.isSuccessful()) {
                                                Log.e("firebase", "Error getting data", task.getException());
                                            }
                                            else {
                                                if (task.getResult().getValue() != null && (boolean) task.getResult().getValue()){
                                                    startActivity(remoteConversation);
                                                }else {
                                                    showSnackMessage(MainActivity.this,
                                                            "This remote conversation room is no longer available or invalid room id");
                                                }
                                                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                            }
                                        }
                                    });

                        }else{
                            showToast("Something wrong !");
                        }
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }

    private void openSaveConversation(){
        final View view = LayoutInflater.from(this).inflate(R.layout.save_conversation_dialog, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Save conversation");
        alertDialog.setCancelable(false);

        final EditText title = (EditText) view.findViewById(R.id.title);
        final EditText details = (EditText) view.findViewById(R.id.details);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Conversation conversation = new Conversation();
                conversation.title = title.getText().toString();
                conversation.isConversationRemote = false;
                if (conversation.title != "" || conversation.title != " "){
                    conversation.details = details.getText().toString();
                    conversation.saveAt = String.valueOf(Calendar.getInstance().getTime());
                    long conversationId = db.conversationDao().insertOne(conversation);

                    List<RecognizeText> recognizeTextsForDb = new ArrayList<>();
                    for (RecognizeText recText: recognizeTexts) {
                        recText.setConversationId(conversationId);
                        recognizeTextsForDb.add(recText);
                    }

                    db.recognizeTextDao().insertAll(recognizeTextsForDb);
                    showToast("Successfully save conversation !");
                }else {
                    showToast("Please insert a title");
                }
            }
        });


        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });


        alertDialog.setView(view);
        alertDialog.show();
    }
    private void fetchConversations(){
        conversationRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        conversationAdapter = new ConversationAdapter(db.conversationDao().getAll());
        conversationRecyclerView.setAdapter(conversationAdapter);
    }

    private void updateRecyclerView(String data){
        recognizeTexts.add(new RecognizeText(data));
        recognizeTextAdapter.notifyDataSetChanged();
        recognizeTextRecyclerView.scrollToPosition(recognizeTexts.size()-1);
        if(recognizeTexts.size() > 0){
            saveConversation.setVisibility(View.VISIBLE);
        }
    }


    private void initializeRecognizeTextRecyclerView(){
        recognizeTextAdapter = new RecognizeTextAdapter(recognizeTexts);
        recognizeTextRecyclerView.setAdapter(recognizeTextAdapter);
        recognizeTextRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        unMuteDevice(audioManager,deviceSystemVolume);
        super.onDestroy();
    }


    public Result parseInfoFromBitmap(Bitmap bitmap) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        Log.v("HGH" ,Arrays.toString(pixels).toString());

        RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(),
                bitmap.getHeight(), pixels);
        GlobalHistogramBinarizer binarizer = new GlobalHistogramBinarizer(source);
        BinaryBitmap image = new BinaryBitmap(binarizer);
        Result result = null;
        try {
            result = new QRCodeReader().decode(image);
            return result;
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        return null;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_choose_lang:
                getSpeechRecognizerLanguagesDetails();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void showLanguageChoosePopup(List recSupportedLanguages) {

        final View parent = findViewById(R.id.action_choose_lang);
        PopupMenu popupMenu = new PopupMenu(parent.getContext(), parent);
        Menu menu = popupMenu.getMenu();
        if (recSupportedLanguages == null){
            menu.add(0, 0, Menu.NONE, "Detected No language");
        }

        for (int i = 0; i < recSupportedLanguages.size() ; i++){
            menu.add(0, i, Menu.NONE, recSupportedLanguages.get(i).toString());
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showToast("Speech recognizer language set to "+item.getTitle().toString());
                keystores.put(RECOGNIZER_LANGUAGE_KEY,item.getTitle().toString());

                return true;
            }
        });
        popupMenu.show();
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

    private void startRecognition() {

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognitionProgressView.setVisibility(View.VISIBLE);
        recognitionProgressView.setSpeechRecognizer(speechRecognizer);
        recognitionProgressView.setRecognitionListener(this);
        recognitionProgressView.play();

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, keystores.get(RECOGNIZER_LANGUAGE_KEY));

        deviceSystemVolume = muteDevice(audioManager);
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
    public Unit invoke(MeowBottomNavigation.Model model) {
        int id = model.getId();
        switch (id) {
            case R.drawable.ic_baseline_speaker_notes_24:
                hideRemoteConversationContainer();
                hideSpeechContainer();
                conversationRecyclerView.setVisibility(View.VISIBLE);
                fetchConversations();
                break;
            case R.drawable.ic_baseline_mic_none_24:
                hideConversationContainer();
                hideRemoteConversationContainer();
                speechContainer.setVisibility(View.VISIBLE);
                initializeRecognizeTextRecyclerView();
                conversationRecyclerView.setAdapter(null);
                break;
            case R.drawable.ic_baseline_online_prediction_24:
                hideConversationContainer();
                hideSpeechContainer();
                remoteConversationContainer.setVisibility(View.VISIBLE);
                remoteConversationQrImage.setImageBitmap(generateRemoteConSessionQr());
                break;
        }
        return null;
    }

    private Bitmap generateRemoteConSessionQr(){
        conversationRoomId = generateRemoteConversationRoomId(this);
        remoteConCode.setText(conversationRoomId);
        // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
        QRGEncoder qrgEncoder = new QRGEncoder(conversationRoomId, null, QRGContents.Type.TEXT, 300);
        qrgEncoder.setColorBlack(Color.BLACK);
        qrgEncoder.setColorWhite(Color.WHITE);
        return  qrgEncoder.getBitmap();

    }

    private void hideConversationContainer(){
        conversationRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void hideSpeechContainer(){
        speechContainer.setVisibility(View.INVISIBLE);
        saveConversation.setVisibility(View.INVISIBLE);
        if(speechRecognizer!=null){
            recognitionProgressView.stop();
            recognitionProgressView.setVisibility(View.GONE);
            speechRecognizer.cancel();
        }
        listen.setText("Listen");
        listen.setBackgroundColor(getColor(R.color.blue));
    }

    private void hideRemoteConversationContainer(){
        remoteConversationContainer.setVisibility(View.INVISIBLE);
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
        unMuteDevice(audioManager,deviceSystemVolume);
    }

    @Override
    public void onError(int i) {
        switch (i){
            case SpeechRecognizer.ERROR_NO_MATCH:
                startRecognition();
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                Log.v("hh","kj");
                startRecognition();
                break;
        }
    }

    @Override
    public void onResults(Bundle bundle) {
        showResults(bundle);
        unMuteDevice(audioManager,deviceSystemVolume);
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

    @Override
    public void onLanguageBroadcastReceiveListener(List<String> supportedLanguages) {
        showLanguageChoosePopup(supportedLanguages);
    }

    @Override
    public void onNoLanguageDetected(String msg) {
        showToast(msg);
    }
}