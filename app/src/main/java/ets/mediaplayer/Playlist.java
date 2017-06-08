package ets.mediaplayer;

import java.util.ArrayList;

/**
 * Created by Pierre-Luc on 2017-06-08.
 */

public class Playlist {

    private ArrayList<Integer> playlist;
    private int songPlaying = 0;
    public Boolean isShuffled = false;

    public Playlist() {
        setPlaylist();
    }

    public void setPlaylist() {
        ArrayList<Integer> musicList = new ArrayList<>();
        musicList.add(R.raw.shrekanthem);
        musicList.add(R.raw.rockabye);
        musicList.add(R.raw.shapeofyou);
        playlist = musicList;
    }

    public void setRandomPlaylist() {
        ArrayList<Integer> musicList = new ArrayList<>();
        musicList.add(R.raw.rockabye);
        musicList.add(R.raw.shrekanthem);
        musicList.add(R.raw.shapeofyou);
        playlist = musicList;
    }

    public int getCurrentSong() {
        return playlist.get(songPlaying);
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
