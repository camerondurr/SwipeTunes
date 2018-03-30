package com.example.camer.swipetunes.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.camer.swipetunes.R;
import com.example.camer.swipetunes.adapter.CustomAdapterSong;
import com.example.camer.swipetunes.model.Song;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SongActivity extends AppCompatActivity {

    // Song List
    private ArrayList<Song> songList;
    private Type typeOfList;
    private RecyclerView rvSongs;
    private CustomAdapterSong adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        rvSongs = findViewById(R.id.rvSongs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Providing up navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get songs
        String songListJson = getIntent().getStringExtra("songListJson");
        typeOfList = new TypeToken<List<Song>>(){}.getType();
        songList = new Gson().fromJson(songListJson,typeOfList);

        showSongs();
    }

    private void showSongs() {
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomAdapterSong(this, R.layout.rowsong, songList);
        rvSongs.setAdapter(adapter);
    }

    public void songPicked(View view){
        int position = (int) view.getTag();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        finish();
    }
}
