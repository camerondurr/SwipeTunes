package com.example.camer.swipetunes.views;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camer.swipetunes.R;
import com.example.camer.swipetunes.model.Song;
import com.example.camer.swipetunes.services.MusicService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends  AppCompatActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener
{
    private static final int REQUEST_SONG = 0;
    // Song List
    private ArrayList<Song> songList;
    private Type typeOfList;
    private int songCurrent;
    private ImageView ivAlbum;
    private TextView songTitleTextView;
    private TextView songArtistTextView;

    // Song List Favorites
    private SharedPreferences favorites;

    // MusicService
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;

    // GestureDetector
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    // Media Player
    private boolean isPlaying = false;
    private boolean isFav=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        songList = new ArrayList();
        typeOfList = new TypeToken<List<Song>>(){}.getType();

        // Initial get song from SD
        getSong();
        songCurrent = 0;

        favorites = getSharedPreferences("favorites", Context.MODE_PRIVATE);

        // get views to design
        songTitleTextView = findViewById(R.id.songTitleTextView);
        songArtistTextView = findViewById(R.id.songArtistTextView);
        ivAlbum = findViewById(R.id.ivAlbum);
        Button songsButton = findViewById(R.id.songsButton);

        // songsButtonOnClick
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

    // get song list from SharePreferences
    private void getFavorites() {
        songList.clear();
        Map<String, String> favoritesMap = (Map<String, String>) favorites.getAll();
        for (Map.Entry<String, String> entry : favoritesMap.entrySet()) {
            Song song = new Gson().fromJson(entry.getValue(),Song.class);
            songList.add(song);
        }
    }


    // get song list from SD
    private void getSong() {
        songList.clear();
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, MediaStore.Audio.Media.TITLE);
        if(musicCursor!=null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            int albumIdColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM_ID);
            //add songs to list
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                long thisAlbumId = musicCursor.getLong(albumIdColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist,thisAlbumId));
            } while (musicCursor.moveToNext());
        }
    }

    // button songs
    private void songsButtonOnClick() {
        Intent intent = new Intent(this, SongActivity.class);
        // Serialized songList for send to SongActivity
        String songListJson = new Gson().toJson(songList, typeOfList);
        intent.putExtra("songListJson",songListJson);
        startActivityForResult(intent,REQUEST_SONG);
    }

    // return song's position from Song Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_SONG){
                songCurrent = data.getIntExtra("position",0);
                Log.d(DEBUG_TAG, "pos:"+songCurrent);
                setShowSong();
            }
        }
    }

    // play and view song
    private void setShowSong() {
        musicSrv.setSong(songCurrent);
        musicSrv.playSong();
        isPlaying = true;
        songTitleTextView.setText(songList.get(songCurrent).getTitle());
        songArtistTextView.setText(songList.get(songCurrent).getArtist());
        showBMAlbum();
    }


    private void showBMAlbum() {
        Bitmap bm = getBMAlbum(songList.get(songCurrent).getAlbumId());
        if (bm != null)
            ivAlbum.setImageBitmap(bm);
        else
            ivAlbum.setImageResource(R.drawable.song);
    }

    // get image album
    private Bitmap getBMAlbum(long albumId) {
        Bitmap bm = null;
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID+ "=?",
                new String[] {String.valueOf(albumId)},
                null);

        if (cursor.moveToFirst()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            bm = BitmapFactory.decodeFile(path);
        }
        return bm;
    }

    private void addFavorite(){
        Song song = songList.get(songCurrent);
        SharedPreferences.Editor editor = favorites.edit();
        editor.putString(song.getId()+"", new Gson().toJson(song,Song.class));
        editor.apply();
    }

    private void removeFavorite(){
        Song song = songList.get(songCurrent);
        SharedPreferences.Editor editor = favorites.edit();
        editor.remove(song.getId()+"");
        editor.apply();
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
            setShowSong();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
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
        if (songList.size()==0){
            Toast.makeText(this, "Empty song list...", Toast.LENGTH_LONG).show();
            return false;
        }
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
                        songCurrent = songCurrent==0?songList.size()-1:songCurrent-1;
                    }
                    else
                    {
                        // Left Swipe
                        Toast.makeText(MainActivity.this, "next song", Toast.LENGTH_SHORT).show();
                        songCurrent = songCurrent==songList.size()-1?0:songCurrent+1;
                    }
                    setShowSong();
                    result = true;
                }
            }
            else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0)
                {
                    // Down Swipe
                    if (!isFav) {
                        Toast.makeText(MainActivity.this, "save song to favorites", Toast.LENGTH_SHORT).show();
                        addFavorite();
                    }
                }
                else
                {
                    if (isFav){
                         // Up Swipe
                        Toast.makeText(MainActivity.this, "remove song to favorites", Toast.LENGTH_SHORT).show();
                        removeFavorite();
                        getFavorites();
                        if (songList.size()>0) {
                            songCurrent = songCurrent >= songList.size() - 1 ? 0 : songCurrent + 1;
                            setShowSong();
                        }
                        else {
                            musicSrv.pauseSong();
                            clearView();
                        }
                    }
                }

                result = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    private void clearView() {
        songTitleTextView.setText("");
        songArtistTextView.setText("");
        ivAlbum.setImageResource(R.drawable.song);
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
        if (songList.size()==0){
            Toast.makeText(this, "Empty song list...", Toast.LENGTH_LONG).show();
            return false;
        }
        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        if (isPlaying)
        {
            Toast.makeText(MainActivity.this, "pause", Toast.LENGTH_SHORT).show();
            musicSrv.pauseSong();

        }
        else
        {
            Toast.makeText(MainActivity.this, "play", Toast.LENGTH_SHORT).show();
            musicSrv.nopauseSong();
        }
        isPlaying = !isPlaying;
        return true;
    }

    // bar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_fav:
                if (isFav) {
                    item.setIcon(AppCompatResources.getDrawable(this,android.R.drawable.star_off));
                    getSong();
                }
                else {
                    item.setIcon(AppCompatResources.getDrawable(this,android.R.drawable.star_on));
                    getFavorites();
                }
                if (songList.size()==0) {
                    Toast.makeText(this, "Empty song list...", Toast.LENGTH_LONG).show();
                    musicSrv.pauseSong();
                    clearView();
                }
                else {
                    songCurrent = 0;
                    setShowSong();
                }
                isFav = !isFav;

        }
        return super.onOptionsItemSelected(item);
    }
}