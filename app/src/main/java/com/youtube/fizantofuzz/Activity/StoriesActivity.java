package com.youtube.fizantofuzz.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.youtube.fizantofuzz.Dialog.AddStoryFeedDialog;
import com.youtube.fizantofuzz.R;
import java.util.Formatter;
import java.util.Locale;

public class StoriesActivity extends AppCompatActivity {
    SimpleExoPlayer exoPlayer;
    ConstraintLayout constraint_story;
    LinearProgressIndicator player_progress;
    TextView text_title, text_date, text_time;
    ImageView play_button, back_story, edit_btn;
    CardView exo_card;
    Runnable runnable;
    ProgressBar buffer;
    LinearLayout layout_front;
    private PlayerView playerView;
    SharedPreferences prefs;
    private String url, title, date, pic, parent;

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
        prefs = getSharedPreferences("Themes", MODE_PRIVATE);
        String themeMode = prefs.getString("current", "");
        switch (themeMode){
            case "":
            case "Main":
                setTheme(R.style.Theme_Main);
                break;
            case "Blue":
                setTheme(R.style.Theme_Blue);
                break;
            case "Yellow":
                setTheme(R.style.Theme_Yellow);
                break;
            case "Pink":
                setTheme(R.style.Theme_Pink);
                break;
            case "Green":
                setTheme(R.style.Theme_Green);
                break;
            case "Teal":
                setTheme(R.style.Theme_Teal);
                break;
            case "Purple":
                setTheme(R.style.Theme_Purple);
                break;
            case "Red":
                setTheme(R.style.Theme_Red);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.screen_background, typedValue,true);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        playerView = findViewById(R.id.exo_stories);
        edit_btn = findViewById(R.id.edit_story);
        player_progress = findViewById(R.id.player_progress);
        text_date = findViewById(R.id.exo_date);
        buffer = findViewById(R.id.buffer_story);
        constraint_story = findViewById(R.id.constraint_story);
        back_story = findViewById(R.id.back_story);
        back_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back_story.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
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
        parent = getIntent().getStringExtra("parent");
        pic = getIntent().getStringExtra("pic");
        text_date.setText(date);
        text_title.setText(title);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(this).build();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        Uri video = Uri.parse(url);
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        playerView.setPlayer(exoPlayer);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);

        FirebaseDatabase.getInstance().getReference("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue().toString().contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    edit_btn.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_btn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                DialogFragment dialog = new AddStoryFeedDialog();
                Bundle bundle = new Bundle();
                bundle.putBoolean("feed_inside", true);
                bundle.putString("pic", pic);
                bundle.putString("Urlofv", url);
                bundle.putString("date", date);
                bundle.putString("title", title);
                bundle.putString("parent", parent);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "feed");
            }
        });

        exoPlayer.addListener(new Player.EventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playbackState == ExoPlayer.STATE_READY) {
                    int timeMs = ((int) exoPlayer.getDuration());
                    StringBuilder mFormatBuilder = new StringBuilder();
                    Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
                    int totalSeconds = timeMs / 1000;
                    int seconds = totalSeconds % 60;
                    final int time = seconds*1000;
                    mFormatBuilder.setLength(0);
                    String final_time = String.valueOf(totalSeconds);
                    text_time.setText(final_time + " Seconds");
                    exo_card.setVisibility(View.VISIBLE);
                }
                if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    buffer.setVisibility(View.VISIBLE);
                } else {
                    buffer.setVisibility(View.GONE);
                }
            }
        });

        Handler handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                player_progress.setProgress((int) ((exoPlayer.getCurrentPosition()*100)/exoPlayer.getDuration()));
                handler.postDelayed(runnable, 1000);
            }
        };
        handler.postDelayed(runnable, 0);

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