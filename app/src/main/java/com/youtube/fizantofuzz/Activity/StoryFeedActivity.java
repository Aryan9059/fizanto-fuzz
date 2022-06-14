package com.youtube.fizantofuzz.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.material.snackbar.Snackbar;
import com.wessam.library.NetworkChecker;
import com.youtube.fizantofuzz.R;
import java.util.Formatter;
import java.util.Locale;

public class StoryFeedActivity extends AppCompatActivity {

    SimpleExoPlayer exoPlayer;
    ConstraintLayout constraint_story;
    TextView text_title, text_date, text_time;
    ImageView play_button, back_story;
    CardView exo_card;
    LinearLayout layout_front;
    private PlayerView playerView;
    private String url, title, date;

    @Override
    protected void onStop() {
        super.onStop();
        exoPlayer.release();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exoPlayer.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_story);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        playerView = findViewById(R.id.exo_stories);
        text_date = findViewById(R.id.exo_date);
        constraint_story = findViewById(R.id.constraint_story);
        back_story = findViewById(R.id.back_story);
        back_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout_front = findViewById(R.id.layout_front);
        text_title = findViewById(R.id.exo_title);
        exo_card = findViewById(R.id.exo_card);
        text_time = findViewById(R.id.text_time);
        play_button = findViewById(R.id.play_button);
        url = getIntent().getStringExtra("Urlofv");
        title = getIntent().getStringExtra("title");
        date = getIntent().getStringExtra("date");
        text_date.setText(date);
        text_title.setText(title);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        if(!NetworkChecker.isNetworkConnected(this)){
            Snackbar.make(constraint_story, "No internet connection.", Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", null).setActionTextColor(getResources().getColor(R.color.ui_accent3)).setTextColor(getResources().getColor(R.color.text_main)).setBackgroundTint(getResources().getColor(R.color.back_dark)).show();
        }

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(this).build();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        Uri video = Uri.parse(url);
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
        playerView.setPlayer(exoPlayer);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);

        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == ExoPlayer.STATE_READY) {
                    int timeMs = ((int) exoPlayer.getDuration());
                    StringBuilder mFormatBuilder = new StringBuilder();
                    Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
                    int totalSeconds = timeMs / 1000;
                    int seconds = totalSeconds % 60;
                    int minutes = (totalSeconds / 60) % 60;
                    mFormatBuilder.setLength(0);
                    String finaltime = String.valueOf(seconds);
                    text_time.setText(finaltime + " Seconds");
                    exo_card.setVisibility(View.VISIBLE);
                } //Type Here!
            }
        });

        layout_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exoPlayer.isPlaying()){
                    exoPlayer.setPlayWhenReady(false);
                    play_button.setVisibility(View.VISIBLE);
                } else{
                    exoPlayer.setPlayWhenReady(true);
                    play_button.setVisibility(View.GONE);
                }
            }
        });

    }
}