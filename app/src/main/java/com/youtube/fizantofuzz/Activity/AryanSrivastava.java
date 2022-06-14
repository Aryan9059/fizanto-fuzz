package com.youtube.fizantofuzz.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.youtube.fizantofuzz.R;

public class AryanSrivastava extends AppCompatActivity {
    TextView body;
    String about_aryan;
    ImageView back_aryan;
    FloatingActionButton share_aryan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aryan_srivastava);

        body = findViewById(R.id.aryanbody);
        back_aryan = findViewById(R.id.back_btn_aryan);
        share_aryan = findViewById(R.id.share_aryan);
        about_aryan = "<p>Hi! I am Aryan Srivastava, creating content for the YouTube channel Fizanto Fuzz. So, I would like to share a story from my perspective. In March 2020, COVID-19 happened and nation-wide lockdown was imposed. We had a lot of time to get ourselves engaged in some fun hobby. So, I and Prateek decided to open a YouTube channel named Fizanto Fuzz. Don&apos;t blame me for the terrible name, it was proposed by Prateek. We decided to make videos related to gameplays and roasts and decided not to use foul language in our videos. We tried to improve video by video, quality and script wise.&nbsp;</p>" +
                "<p>But after the unlock, we got busier and were unable to make videos anymore. But being an Android enthusiast, I decided to work on an app that would be packed with features helpful for the Fizanto Fuzz subscribers. From then on, I have worked hard coding, managing and updating the app for 2 years now. Looking back on that videos, it feels cringe but on the other side, it helped us to gain some new skills. I learned about professional video editing and fell in love with graphics designing.</p>" +
                "<p>Today, though we are not uploading videos and haven&apos;t posted on Instagram for a long time, we are actively working on the Fizanto Fuzz app and are available to reply to you on our email. I think that&apos;s all from my side, enjoy using the app!</p>";

        body.setText(Html.fromHtml(about_aryan));

        back_aryan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        share_aryan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Aryan9059"));
                startActivity(intent);
            }
        });

    }
}