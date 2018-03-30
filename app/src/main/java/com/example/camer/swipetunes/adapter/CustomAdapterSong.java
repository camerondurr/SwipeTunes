package com.example.camer.swipetunes.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.camer.swipetunes.R;
import com.example.camer.swipetunes.model.Song;

import java.util.List;

public class CustomAdapterSong extends RecyclerView.Adapter<CustomAdapterSong.ViewHolder> {

    private Context context;
    private int layout;
    private List<Song> dataList;

    public CustomAdapterSong(Context context, int itemLayout, List<Song> dataList) {
        this.context = context;
        this.dataList = dataList;
        this.layout = itemLayout;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(this.layout, parent, false);
        return new ViewHolder(v,context);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        Song item = dataList.get(position);
        holder.tvsong_title.setText(item.getTitle());
        holder.tvsong_artist.setText(item.getArtist());
        // image
        Bitmap bm = getBMAlbum(item.getAlbumId());
        if (bm != null)
            holder.ivAlbumRow.setImageBitmap(bm);
        else
            holder.ivAlbumRow.setImageResource(R.drawable.song);

        // holder.itemView.setTag(item);
        holder.itemView.setTag(position);

    }

    @Override public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvsong_title;
        public TextView tvsong_artist;
        public ImageView ivAlbumRow;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            tvsong_title = itemView.findViewById(R.id.tvsong_title);
            tvsong_artist = itemView.findViewById(R.id.tvsong_artist);
            ivAlbumRow = itemView.findViewById(R.id.ivAlbumRow);
        }
    }

    private Bitmap getBMAlbum(long albumId) {
        Bitmap bm = null;
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
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
}