package ets.mediaplayer;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Pierre-Luc on 2017-06-08.
 */

public class Playlist {

    private ArrayList<Integer> playlist;
    private SongList songList;
    private int songPlaying = 0;
    public Boolean isShuffled = false;
    private Context context;

    public Playlist(Context applicationContext) {
        context = applicationContext;
        setPlaylist();
    }

    public void setPlaylist() {
        ArrayList<Integer> musicList = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();

        for (int i = 0; i < fields.length; i++) {
            String songName = fields[i].getName();

            Log.d("TestSong", i + " " + songName + " type:" + fields[i].getGenericType());
            if (!songName.equals("$change") && !songName.equals("serialVersionUID")) {
                int songId = context.getResources().getIdentifier(songName, "raw", context.getPackageName());
                musicList.add(songId);
            }
        }

        //SI CA CRASH, REMPLACE LA BOUCLE FOR PAR LE VIEUX CODE CI-DESSOUS
//        musicList.add(R.raw.shrekanthem);
//        musicList.add(R.raw.rockabye);
//        musicList.add(R.raw.shapeofyou);

        playlist = musicList;
    }

    public void updateFromServer(String list)
    {
        Gson gson = new GsonBuilder().create();
        songList = gson.fromJson(list, SongList.class);
        playlist.clear();
        for(SongList.Song s : songList.songs)
        {
            playlist.add(s.ID);
            Log.d("TestSong", s.title);
        }

    }

    public void setRandomPlaylist() {
        Collections.shuffle(playlist);
    }

    public int getCurrentSong() {
        return playlist.get(songPlaying);
    }

    public SongList.Song getCurrentSongInfo()
    {
        for(SongList.Song s : songList.songs)
        {
            if (s.ID == playlist.get(songPlaying))
            {
                return s;
            }
        }
        return null;
    }

    public void setCurrentSong(int id)
    {
        songPlaying = playlist.indexOf(id);
    }

    public SongList.Song getSongById(int id)
    {
        for(SongList.Song s : songList.songs)
        {
            if (s.ID == id)
            {
                return s;
            }
        }
        return null;
    }

    public String getSongPath(int id)
    {
        for(SongList.Song s : songList.songs)
        {
            if (s.ID == id)
            {
                return s.path;
            }
        }
        return null;
    }

    public int getNextSong() {
        if (songPlaying >= playlist.size() - 1) {
            songPlaying = 0;
        }
        else {
            songPlaying++;
        }
        return playlist.get(songPlaying);
    }

    public int getPreviousSong() {
        if (songPlaying == 0) {
            songPlaying = playlist.size() - 1;
        }
        else {
            songPlaying--;
        }
        return playlist.get(songPlaying);
    }

    public void toggleShuffle() {
        if (isShuffled) {
            isShuffled = false;
            setPlaylist();
        }
        else {
            isShuffled = true;
            setRandomPlaylist();
        }
    }

    public void setIsShuffled(Boolean shuffled) {
        isShuffled = shuffled;
    }

    public int size() {
        return playlist.size();
    }

}
