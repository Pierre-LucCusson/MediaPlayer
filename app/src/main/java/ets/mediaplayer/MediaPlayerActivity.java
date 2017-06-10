package ets.mediaplayer;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MediaPlayerActivity extends AppCompatActivity {

    MediaPlayer player;
    Playlist playlist;
    Timer timer;

    private Handler seekBarHandler = new Handler();

    public void play(View view) {
        Button button = (Button) view;
        if (player.isPlaying()){
            button.setText(R.string.play);
            player.pause();
        }else{
            button.setText(R.string.pause);
            player.start();
        }
        Log.d("Test", String.format("Play: %b", player.isPlaying() ));
    }

    public void next(View view) {

//        player.getSelectedTrack();
//        player.getTrackInfo();
//        player.selectTrack(2);

        player.reset();
        player = MediaPlayer.create(this, playlist.getNextSong());
        player.start();

        setSongDetails();

        Log.d("Test", "Next was clicked");
    }

    public void back(View view) {
        if (player.getCurrentPosition() < 5000) {
            player.seekTo(0);
        }
        else {
            player.reset();
            player = MediaPlayer.create(this, playlist.getPreviousSong());
            player.start();
        }

        setSongDetails();

        Log.d("Test", "Back was clicked");
    }

    public void loop(View view) {
        if (player.isLooping()) {
            player.setLooping(false);
        }
        else {
            player.setLooping(true);
        }
        Log.d("Test", String.format("Loop: %b",player.isLooping() ));
    }

    public void shuffle(View view) {
        playlist.toggleShuffle();
        Log.d("Test", String.format("Shuffling: %b",playlist.isShuffled ));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        for (String s:this.getResources().getAssets().getLocales()) {

            Log.d("Test", s);
        }

        playlist = new Playlist();

        player = MediaPlayer.create(this , playlist.getCurrentSong());
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                ((Button) findViewById(R.id.playButton)).setText(R.string.pause);
            }
        });


        //Play button
        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                play(view);
            }
        } );

        //Next button
        final Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                next(view);
            }
        } );

        //Back button
        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                back(view);
            }
        } );


        //Loop button
        ToggleButton loopButton = (ToggleButton) findViewById(R.id.repeatButton);
        loopButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setText(R.string.loop);
                player.setLooping(isChecked);
            }
        });

        //Seek bar
        ((SeekBar) findViewById(R.id.timeSeekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (player != null && b)
                {
                    player.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(player != null){
                    int currentTime = player.getCurrentPosition() / 1000;
                    int totalTime = player.getDuration() / 1000;
                    ((SeekBar) findViewById(R.id.timeSeekBar)).setProgress(currentTime);

                    ((TextView) findViewById(R.id.currentTime)).setText(String.format("%2d:%2d", currentTime / 60, currentTime % 60));

                    ((TextView) findViewById(R.id.totalTime)).setText(String.format("%2d:%2d", totalTime / 60, totalTime % 60));
                }
                seekBarHandler.postDelayed(this, 1000);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_media_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private void setSongDetails() {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Uri myUri = Uri.parse("android.resource://" + getPackageName() + "/" + playlist.getCurrentSong());
        retriever.setDataSource(this, myUri);

        String songName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artistName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
        String albumName = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

        TextView songText =(TextView) findViewById(R.id.songText);
        songText.setText(songName);

        TextView artistText =(TextView) findViewById(R.id.artistText);
        artistText.setText(artistName);

        TextView albumText =(TextView) findViewById(R.id.albumText);
        albumText.setText(albumName);
    }

    /*
    public void playSong() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                player.reset();
                player = MediaPlayer.create(this, R.raw.shrekanthem);
                //player = MediaPlayer.create(this, playlist.getNextSong());
                player.start();
                //if (playlist.size() > i+1) {
                //    playSong();
                //}
            }
        },player.getDuration()+100);
    }
    */
}
