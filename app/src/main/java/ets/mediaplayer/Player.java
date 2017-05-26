package ets.mediaplayer;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import java.io.FileDescriptor;
import java.util.List;

/**
 * Created by AK90090 on 2017-05-25.
 */

public class Player {

    List<AssetFileDescriptor> songsList;

    public Player(AssetManager assetManager)
    {

        assetManager.getLocales();
    }


}
