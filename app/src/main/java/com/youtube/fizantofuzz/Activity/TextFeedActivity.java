package com.youtube.fizantofuzz.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.youtube.fizantofuzz.R;

public class TextFeedActivity extends AppCompatActivity {
    String image, body, date, title;
    private ImageView imageView, back_btn;
    FloatingActionButton floatingActionButton;
    private TextView titletv, bodytv, datetv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_feed_text);

        titletv = findViewById(R.id.onefeedtitle);
        back_btn = findViewById(R.id.back_btn_onefeedtext);
        bodytv = findViewById(R.id.onefeedtext);
        datetv = findViewById(R.id.onefeeddate);
        imageView = findViewById(R.id.iamgeonefeed);
        floatingActionButton = findViewById(R.id.share_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        image = getIntent().getStringExtra("image");
        body = getIntent().getStringExtra("body");
        date = getIntent().getStringExtra("date");
        title = getIntent().getStringExtra("title");

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                startActivity(Intent.createChooser(shareIntent, "Share..."));
            }
        });

        titletv.setText(title);
        bodytv.setText(Html.fromHtml(body));
        datetv.setText("Dated: " + date);

        if (!image.equals("no")) {
            Glide.with(getApplicationContext()).load(image).into(imageView);
        }
    }
}