package ets.mediaplayer;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.CompoundButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;


public class MediaPlayerActivity extends AppCompatActivity {

    VisualizerView visualizerView;

    MediaPlayer player;
    private Visualizer visualizer;
    Playlist playlist;
    Timer timer;
    HttpClient client;
    boolean isClientPlaying = true;
    private ArrayList<Float> xGesture = new ArrayList<Float>();

    private Handler seekBarHandler = new Handler();


    public void preferences(View view)
    {
        Intent i = new Intent(this, MPpreferencesActivity.class);
        startActivity(i);
    }

    public String sendCommand(String command)
    {
            try {
                return client.run(command);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return null;
    }


    public void play(View view) {
        Button button = (Button) view;
        if (player.isPlaying()){
            button.setText(R.string.play);
            visualizer.setEnabled(false);
            player.pause();
            sendCommand(client.PAUSE);


        }else{
            button.setText(R.string.pause);
            initVisualizer();
            visualizer.setEnabled(true);
            player.start();
            sendCommand(client.RESUME);
        }
        Log.d("Test", String.format("Play: %b", player.isPlaying() ));
    }

    public void next(View view) {
        playNextSong();
        Log.d("Test", "Next was clicked");
    }

    public void back(View view) {
        playPreviousSong();
        Log.d("Test", "Back was clicked");
    }

    public void loop(View view) {
        if (player.isLooping()) {
            player.setLooping(false);
        }
        else {
            player.setLooping(true);
        }
        sendCommand(client.REPEAT);

        Log.d("Test", String.format("Loop: %b",player.isLooping() ));
    }

    public void shuffle(View view) {
        playlist.toggleShuffle();
        sendCommand(client.SHUFFLE);
        Log.d("Test", String.format("Shuffling: %b",playlist.isShuffled ));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        client = new HttpClient();

        visualizerView = (VisualizerView) findViewById(R.id.myvisualizerview);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        playlist = new Playlist(getApplicationContext());
        playlist.updateFromServer(sendCommand(client.PLAYLIST));
        prepareStreaming(playlist.getCurrentSongInfo());

        player.setVolume(0,0);
        sendCommand(client.RESUME);
        sendCommand(client.PAUSE);
        player.pause();
        player.setVolume(1,1);

        //Play button
        final Button playButton = (Button) findViewById(R.id.playButton);
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
        ToggleButton ControlButton = (ToggleButton) findViewById(R.id.ServerControl);
        ControlButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ProgressBar pb = (ProgressBar) findViewById(R.id.IsSynced);

                isClientPlaying = isChecked;
                if (isChecked)
                {

                    pb.setVisibility(View.VISIBLE);
                    player.setVolume(1,1);
                }else{
                    player.setVolume(0,0);
                    pb.setVisibility(View.INVISIBLE);
                }

            }
        });


        //Loop button
        ToggleButton loopButton = (ToggleButton) findViewById(R.id.repeatButton);
        loopButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setText(R.string.loop);
                player.setLooping(isChecked);
                sendCommand(client.REPEAT);
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

                    ((SeekBar) findViewById(R.id.timeSeekBar)).setMax(totalTime);

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

    private void setSongDetails(SongList.Song song) {

        TextView songText =(TextView) findViewById(R.id.songText);
        songText.setText(song.title);

        TextView artistText =(TextView) findViewById(R.id.artistText);
        artistText.setText(song.artist);

        TextView albumText =(TextView) findViewById(R.id.albumText);
        albumText.setText(song.artist);
    }

    public void prepareStreaming(SongList.Song song)
    {
        if (player != null)
        {
            player.reset();
        }

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(client.urlHead + song.path);
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initVisualizer();
        visualizer.setEnabled(true);
        player.start();
        setSongDetails(song);

        if (isClientPlaying)
        {
            player.setVolume(1,1);
        }else{
            player.setVolume(0,0);
        }
    }

    public void playSong(int id) {
        prepareStreaming(playlist.getSongById(id));

        ((Button) findViewById(R.id.playButton)).setText(R.string.pause);

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playNextSong();
            }
        });
    }

    public void playNextSong()
    {
        int id = Integer.valueOf(sendCommand(client.NEXT));
        playSong(id);
    }

    public void playPreviousSong()
    {
        int id = Integer.valueOf(sendCommand(client.BACK));
        playSong(id);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                xGesture = new ArrayList<Float>();
                Log.d("motionTest", "Action MotionEvent was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE):
                int mActivePointerId = event.getPointerId(0);
                int pointerIndex = event.findPointerIndex(mActivePointerId);
                float x = event.getX(pointerIndex);
                xGesture.add(x);
                Log.d("motionTest", "Action MotionEvent was MOVE");
                return true;
            case (MotionEvent.ACTION_UP):
                checkGestures();
                Log.d("motionTest", "Action MotionEvent was UP");
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    private void checkGestures() {

        Boolean leftGesture = true;
        Boolean rightGesture = true;

        for(int i = 0; i < xGesture.size() - 1; i++) {
            if (xGesture.get(i) > xGesture.get(i+1)) {
                leftGesture = false;
            }
            if (xGesture.get(i) < xGesture.get(i+1)) {
                rightGesture = false;
            }
        }

        if (leftGesture) {
            playNextSong();
        }

        if (rightGesture) {
            if (player.getCurrentPosition() > 5000) {
                player.seekTo(0);
            } else {
                playPreviousSong();
            }
        }
    }

    private void initVisualizer() {


        if (visualizer != null && visualizer.getEnabled()) {
            visualizer.setEnabled(false);
        }
        visualizer = new Visualizer(player.getAudioSessionId());
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        visualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        visualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }
}


