package ets.mediaplayer;

import android.media.MediaMetadataRetriever;

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

    public String getSong() {

//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(this, getCurrentSong());
//        String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

        return "SongSong";
    }

    public String getArtist() {
        return "ArtistArtist";
    }

    public String getAlbum() {
        return "AlbumAlbum";
    }
}
