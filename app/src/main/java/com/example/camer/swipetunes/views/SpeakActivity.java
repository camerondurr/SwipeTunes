package com.example.camer.swipetunes.views;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



import com.example.camer.swipetunes.R;
import com.example.camer.swipetunes.services.MusicService;

import java.util.ArrayList;
import java.util.Locale;

public class SpeakActivity extends AppCompatActivity {
    private TextView txvResult;
    private Intent playIntent;
    private boolean musicBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak);
        txvResult = (TextView) findViewById(R.id.txvResult);
    }

    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        }
        else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(SpeakActivity.this, result.get(0), Toast.LENGTH_SHORT).show();
                    if(result.get(0).equals("play"))
                    {
                        MainActivity.musicSrv.startSong();
                    }
                    else {
                        if (result.get(0).equals("pause") || result.get(0).equals("bus") || result.get(0).equals("force") || result.get(0).equals("boss") || result.get(0).equals("false") || result.get(0).equals("Porsche")|| result.get(0).equals("squash")) {
                            MainActivity.musicSrv.pauseSong();
                        }
                    }
                }
                break;
        }
    }
}