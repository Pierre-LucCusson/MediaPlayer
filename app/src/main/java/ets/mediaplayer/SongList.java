package ets.mediaplayer;

/**
 * Created by Gabriel on 2017-06-15.
 */

public class SongList {

    public Song[] songs;

    public class Song
    {
        public int ID;
        public String title;
        public int length;
        public String artist;
        public String album;
        public String image;
        public String path;

    }
}
