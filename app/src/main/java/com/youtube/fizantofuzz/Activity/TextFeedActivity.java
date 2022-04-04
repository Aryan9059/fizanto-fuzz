package com.youtube.fizantofuzz.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.youtube.fizantofuzz.R;

public class TextFeedActivity extends AppCompatActivity {
    String image, body, date, title;
    private ImageView imageView;
    FloatingActionButton floatingActionButton;
    private TextView titletv, bodytv, datetv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_feed_text);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp, null));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextFeedActivity.super.onBackPressed();
            }
        });

        titletv = findViewById(R.id.onefeedtitle);
        bodytv = findViewById(R.id.onefeedtext);
        datetv = findViewById(R.id.onefeeddate);
        imageView = findViewById(R.id.iamgeonefeed);

        floatingActionButton = findViewById(R.id.share_btn);

        image = getIntent().getStringExtra("image");
        body = getIntent().getStringExtra("body");
        date = getIntent().getStringExtra("date");
        title = getIntent().getStringExtra("title");

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, body);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                startActivity(Intent.createChooser(shareIntent, "Share..."));
            }
        });

        titletv.setText(title);
        bodytv.setText(body);
        datetv.setText("Dated: " + date);

        if (!image.equals("no")) {
            Glide.with(getApplicationContext()).load(image).into(imageView);
        }
    }
}