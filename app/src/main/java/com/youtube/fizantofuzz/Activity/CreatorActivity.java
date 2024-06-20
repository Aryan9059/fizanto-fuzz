package com.youtube.fizantofuzz.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.material.appbar.MaterialToolbar;
import com.youtube.fizantofuzz.R;

public class CreatorActivity extends AppCompatActivity {
    TextView body, status;
    String about_aryan, about_prateek;
    MediaSource source;
    MaterialToolbar aryan_toolbar;
    Uri aryan_uri;
    ImageView back_aryan;
    PlayerView aryan_video;
    SimpleExoPlayer exoPlayer;
    SharedPreferences prefs;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exoPlayer.release();
    }

    @SuppressLint("SetTextI18n")
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
        setContentView(R.layout.activity_aryan_srivastava);

        String name = getIntent().getStringExtra("name");
        body = findViewById(R.id.aryanbody);
        status = findViewById(R.id.aryan_status);
        aryan_toolbar = findViewById(R.id.toolbar_aryan);

        about_prateek = "<p>Hello, I am Prateek Sinha. My passion for creating videos on YouTube led to the establishment of the Fizanto Fuzz channel. Partnering with my friend Aryan, we began producing videos focused on game recordings and topical roasts. Aryan managed the video editing and design, while I was responsible for voice-over recordings, scriptwriting, and acting, especially since Aryan is an introvert.</p>\n" +
                "\n" +
                "    <p>Balancing my studies, particularly during my board exams, limited my time for the channel. Despite this, we persevered and continued posting videos on YouTube. Among our projects, the <strong>\"Quba Mobile Roast\"</strong> video stands out as a personal favorite due to its excellent script and performance, even though, in hindsight, there were areas for improvement.</p>\n" +
                "\n" +
                "    <p>The eighteen months dedicated to our channel were incredibly rewarding, involving days spent scripting, acting, and engaging with our audience, whose feedback was immensely gratifying. Currently, my focus is on my studies, leaving little time for YouTube. Nonetheless, the experience imparted valuable skills, particularly in acting and cinematography.</p>\n" +
                "\n" +
                "    <p>Although we are not releasing new videos, you can stay connected with us through the Fizanto Fuzz app. Thank you for your support, and feel free to follow me on <a href=\"https://www.instagram.com\">Instagram</a>!</p>\n";

        about_aryan = "    <p>Hello, I am Aryan Srivastava, and I create content for the YouTube channel Fizanto Fuzz. I'd like to share a story from my perspective. In March 2020, when the COVID-19 pandemic led to a nationwide lockdown, we suddenly had a lot of free time. Prateek and I decided to launch a YouTube channel named Fizanto Fuzz—credit for the name goes to Prateek.</p>\n" +
                "\n" +
                "    <p>Our goal was to produce videos focused on gameplay and roasts, ensuring we avoided the use of foul language. With each video, we aimed to improve the quality and scripting. However, as the lockdown eased, our schedules became busier, and we could no longer dedicate time to making videos.</p>\n" +
                "\n" +
                "    <p>Being an Android enthusiast, I shifted my focus to developing an app packed with features beneficial to Fizanto Fuzz subscribers. Over the past two years, I have diligently coded, managed, and updated the app. Reflecting on our old videos, they may seem cringe-worthy now, but the experience was invaluable, helping us acquire new skills. I developed a deep interest in professional video editing and graphics design.</p>\n" +
                "\n" +
                "    <p>Though we are no longer uploading videos or posting on Instagram frequently, we remain actively engaged with the Fizanto Fuzz app and are available to respond to emails. Thank you for your continued support, and enjoy using the app!</p>\n";

        DataSource.Factory dataSource = new DefaultDataSourceFactory(this, "Fizanto Fuzz");

        if (name.equals("Aryan")){
            aryan_toolbar.setTitle("Aryan Srivastava");
            source = new ExtractorMediaSource.Factory(dataSource).createMediaSource(RawResourceDataSource.buildRawResourceUri(R.raw.aryan_omoji));
            body.setText(Html.fromHtml(about_aryan));
            status.setText("Content Creator, App Developer");
        }  else {
            aryan_toolbar.setTitle("Prateek Sinha");
            source = new ExtractorMediaSource.Factory(dataSource).createMediaSource(RawResourceDataSource.buildRawResourceUri(R.raw.prateek_omoji));
            body.setText(Html.fromHtml(about_prateek));
            status.setText("Content Creator, Script Specialist");
        }

        aryan_video = findViewById(R.id.aryan_video);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        aryan_video.setPlayer(exoPlayer);
        aryan_video.setUseController(false);
        exoPlayer.prepare(source);
        exoPlayer.setPlayWhenReady(true);
        back_aryan = findViewById(R.id.feed_back);

        back_aryan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back_aryan.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

    }
}