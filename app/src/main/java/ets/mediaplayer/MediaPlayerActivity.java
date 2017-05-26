package ets.mediaplayer;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.media.MediaPlayer;
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
import java.util.concurrent.TimeUnit;

public class MediaPlayerActivity extends AppCompatActivity {

    MediaPlayer player;

    private Handler seekBarHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        for (String s:this.getResources().getAssets().getLocales()) {

            Log.d("Test", s);
        }


        player = MediaPlayer.create(this ,R.raw.shrekanthem);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                ((Button) findViewById(R.id.playButton)).setText(R.string.play);
            }
        });


        //Play button
        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                Log.d("Test", String.format("%b",player.isPlaying() ));
                if (player.isPlaying()){
                    button.setText(R.string.play);
                    player.pause();
                }else{
                    button.setText(R.string.pause);
                    player.start();
                }
            }
        } );

        //Next button
        final Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                //TODO: Next
            }
        } );

        //Back button
        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                player.seekTo(0);
                //TODO: if player.position = 0 => back

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
}
