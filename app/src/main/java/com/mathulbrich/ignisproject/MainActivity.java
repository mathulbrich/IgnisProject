package com.mathulbrich.ignisproject;

import static android.speech.RecognizerIntent.*;
import static android.Manifest.*;
import static android.content.pm.PackageManager.*;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.format.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Response[] responses;
    TextView message;
    TextView timeText;
    ImageView image;
    SpeechRecognizer speechRecognizer;
    boolean pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message = findViewById(R.id.message);
        image = findViewById(R.id.ignis);
        timeText = findViewById(R.id.timeText);
        responses = Ai.getAiResponses();

        checkPermissions();
        checkDeviceFunctions();
        startTimeCheck();

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();

                //after 4 seconds, stop
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        speechRecognizer.stopListening();
                    }
                }, 4000);
            }
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            public void onReadyForSpeech(Bundle params) {}
            public void onBeginningOfSpeech() {}
            public void onRmsChanged(float rmsdB) {}
            public void onBufferReceived(byte[] buffer) {}
            public void onEndOfSpeech() {}
            public void onError(int error) {}
            public void onPartialResults(Bundle partialResults) {}
            public void onEvent(int eventType, Bundle params) {}

            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null)
                    message.setText(data.get(0));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    protected void startListening() {
        Intent intent = new Intent(ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(EXTRA_LANGUAGE, "en-US");
        //intent.putExtra(EXTRA_PROMPT, "Listening...");
        speechRecognizer.startListening(intent);

    }

    protected void speak(Response r) {
        if(!pressed) {
            final MediaPlayer mp = MediaPlayer.create(this, r.getVoice());
            mp.start();
            message.setText(r.getMessage());
            image.setImageResource(r.getImage());

            // when the sounds over, release the press lock
            Handler handler = new Handler();

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    while(mp.isPlaying());
                    pressed = false;
                }
            };
            handler.post(runnable);
        }

    }

    protected void startTimeCheck() {

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while(!isInterrupted()) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                timeText.setText(DateFormat.format("hh:mm", Calendar.getInstance().getTime()));
                            }
                        });
                        Thread.sleep(1000);
                    }
                } catch(InterruptedException e) {}

            }
        };

        t.start();
    }

    protected void checkPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkCallingOrSelfPermission(permission.RECORD_AUDIO) == PERMISSION_DENIED) {
                requestPermissions(new String[]{permission.RECORD_AUDIO}, 101);
            }

        }
    }

    protected void checkDeviceFunctions() {
        if(!SpeechRecognizer.isRecognitionAvailable(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage(R.string.no_recognition_available);
            builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    dialog.dismiss();
                }
            });

            builder.create().show();
        }
    }

}
