package com.example.camer.swipetunes;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener
{
    // GestureDetector
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    // Media Player
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button songsButton = findViewById(R.id.songsButton);
        songsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songsButtonOnClick();
            }
        });

        // Gesture Detector
        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setOnDoubleTapListener(this);
    }

    private void songsButtonOnClick() {
        Intent intent = new Intent(this, SongActivity.class);
        startActivity(intent);
    }

    // GestureDetector
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (this.mDetector.onTouchEvent(event))
        {
            return true;
        }
        return super.onTouchEvent(event);
    }
    @Override
    public boolean onDown(MotionEvent event)
    {
        Log.d(DEBUG_TAG,"onDown: " + event.toString());
        return true;
    }
    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY)
    {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
        boolean result = false;
        try
        {
            float diffY = event2.getY() - event1.getY();
            float diffX = event2.getX() - event1.getX();
            if (Math.abs(diffX) > Math.abs(diffY))
            {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0)
                    {
                        // Right Swipe
                        Toast.makeText(MainActivity.this, "previous song", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        // Left Swipe
                        Toast.makeText(MainActivity.this, "next song", Toast.LENGTH_SHORT).show();
                    }
                    result = true;
                }
            }
            else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0)
                {
                    // Down Swipe
                    Toast.makeText(MainActivity.this, "save song", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // Up Swipe
                    // Toast.makeText(MainActivity.this, "top", Toast.LENGTH_SHORT).show();
                }
                result = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
    @Override
    public void onLongPress(MotionEvent event)
    {
        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
    }
    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY)
    {
        Log.d(DEBUG_TAG, "onScroll: " + event1.toString() + event2.toString());
        return true;
    }
    @Override
    public void onShowPress(MotionEvent event)
    {
        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }
    @Override
    public boolean onSingleTapUp(MotionEvent event)
    {
        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }
    @Override
    public boolean onDoubleTap(MotionEvent event)
    {
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        return true;
    }
    @Override
    public boolean onDoubleTapEvent(MotionEvent event)
    {
        Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }
    @Override
    public boolean onSingleTapConfirmed(MotionEvent event)
    {
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        if (isPlaying)
        {
            Toast.makeText(MainActivity.this, "pause", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(MainActivity.this, "play", Toast.LENGTH_SHORT).show();
        }
        isPlaying = !isPlaying;
        return true;
    }
}