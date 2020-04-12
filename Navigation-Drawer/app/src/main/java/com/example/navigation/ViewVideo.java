package com.example.navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Locale;

public class ViewVideo extends AppCompatActivity {

    private VideoView mainVideoView;
    private ImageView playBtn;
    private TextView currentTimer;
    private TextView durationTimer;
    private ProgressBar currentProgres;
    private ProgressBar bufferProgress;

    private boolean isPlaying;

    private Uri videoUri;

    private int current = 0;
    private int duration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_streaming);

        isPlaying = false;

        mainVideoView = (VideoView) findViewById(R.id.mainVideoView);
        playBtn = (ImageView) findViewById(R.id.playBtn);
        currentProgres = (ProgressBar) findViewById(R.id.videoProgress);
        currentProgres.setMax(100);

        bufferProgress = (ProgressBar) findViewById(R.id.bufferProgress);
        currentTimer = (TextView) findViewById(R.id.currentTimer);
        durationTimer = (TextView) findViewById(R.id.durationTimer);

        videoUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/video-surveillance-580ac.appspot.com/o/2020-04-06%2F2020-04-06-15-32-38.mp4?alt=media&token=5cbd776d-735b-4abb-a564-9dfdf9947954");
        mainVideoView.setVideoURI(videoUri);
        mainVideoView.requestFocus();



        // it determines if the video is buffering
        mainVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {

                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                        bufferProgress.setVisibility(View.GONE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                        bufferProgress.setVisibility(View.VISIBLE);
                        return true;
                    }
                }
                return false;
            }
        });


        mainVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // duration in seconds
                duration = mp.getDuration() / 1000;
                String durationString = String.format(Locale.US, "%02d:%02d", duration / 60, duration % 60);

                durationTimer.setText(durationString);
            }
        });


        mainVideoView.start();
        isPlaying = true;
        playBtn.setImageResource(R.drawable.ic_pause);

        new VideoProgress().execute();


        playBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    mainVideoView.pause();
                    isPlaying = false;
                    playBtn.setImageResource(R.drawable.ic_play);
                } else {
                    mainVideoView.start();
                    isPlaying = true;
                    playBtn.setImageResource(R.drawable.ic_pause);
                }
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        isPlaying = false;
    }

    public class VideoProgress extends AsyncTask<Integer/*Param*/, Integer/*Progress*/, Void/*Result*/> {

        @Override
        protected Void doInBackground(Integer... integers) {
            do {
              if(isPlaying){
                  current = mainVideoView.getCurrentPosition() / 1000;
                  publishProgress(current);
              }
            } while (currentProgres.getProgress() <= 100);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            try {
                int currentPercent = values[0] * 100 / duration;
                currentProgres.setProgress(currentPercent);
                String durationString = String.format(Locale.US, "%02d:%02d", values[0] / 60, values[0] % 60);
                currentTimer.setText(durationString);

            } catch (Exception e) { }
        }
    }
}
