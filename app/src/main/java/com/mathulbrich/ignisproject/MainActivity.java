package com.mathulbrich.ignisproject;

import static android.speech.RecognizerIntent.*;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    Response[] responses;
    TextView message;
    TextView timeText;
    ImageView image;
    final static int SPEECH_CODE = 77;

    boolean pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message = findViewById(R.id.message);
        image = findViewById(R.id.ignis);
        timeText = findViewById(R.id.timeText);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();
            }
        });

        responses = Ai.getAiResponses();
        startTimeCheck();
    }

    protected void startListening() {
        Intent intent = new Intent(ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(EXTRA_LANGUAGE, "en-US");
        intent.putExtra(EXTRA_PROMPT, "Listening...");
        startActivityForResult(intent, SPEECH_CODE);
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
        Handler handler = new Handler();

        handler.post(new Runnable() {
            public void run() {
                Date date = Calendar.getInstance(Locale.getDefault()).getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                dateFormat.setTimeZone(TimeZone.getDefault());

                while (true)
                    timeText.setText(dateFormat.format(date));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case SPEECH_CODE:
                if(resultCode == RESULT_OK && data != null) {
                    String resultText = data.getStringArrayExtra(EXTRA_RESULTS)[0];
                    String toLowerCase = resultText.toLowerCase();

                    boolean already = false;

                    for(Response r : responses) {

                        for(int resource : r.getTrigger()) {
                            if(toLowerCase.contains(getString(resource))) {
                                speak(r);
                                already = true;
                                break;
                            }
                        }
                        if(already)
                            break;
                    }

                    if(!already)
                        speak(responses[Ai.AI_DONT_PRAY]); //speak generic phrase to not understand

                }
                break;
        }

    }
}
