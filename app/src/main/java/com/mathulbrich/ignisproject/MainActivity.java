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
import android.util.Log;
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
    final String SPEECH_RECOGNIZER = "Speech Recognizer DEBUG";

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
            }
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            public void onReadyForSpeech(Bundle params) {
                Log.d(SPEECH_RECOGNIZER, "Ready for Speech!");
            }
            public void onBeginningOfSpeech() {
                Log.d(SPEECH_RECOGNIZER, "Beginning the Speech!");
            }
            public void onRmsChanged(float rmsdB) {

            }
            public void onBufferReceived(byte[] buffer) {
                Log.d(SPEECH_RECOGNIZER, "Buffer received: " + buffer);
            }
            public void onEndOfSpeech() {
                Log.d(SPEECH_RECOGNIZER, "Speech has ended!");
            }
            public void onError(int error) {

                switch (error) {
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        Log.e(SPEECH_RECOGNIZER, "Error. No voice message captured.");
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        Log.e(SPEECH_RECOGNIZER, "Error. The device have insufficient permissions to record audio.");
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        Log.e(SPEECH_RECOGNIZER, "Error. An error has occurred on client side.");
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        Log.e(SPEECH_RECOGNIZER, "Error. There no speech input of user.");
                        break;
                }
            }
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> partial = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Log.d(SPEECH_RECOGNIZER, "Partial results received with values " + partial.get(0));
            }
            public void onEvent(int eventType, Bundle params) {
                Log.d(SPEECH_RECOGNIZER, "Event realized!");
            }

            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Log.d(SPEECH_RECOGNIZER, "Result received " + data.get(0) + "!");

                String userSpeech = data.get(0);
                Response res = Ai.checkSpeechResponse(MainActivity.this, userSpeech);

                if(res != null)
                    speak(res);
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
        intent.putExtra(EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 20000); //2 sec
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
