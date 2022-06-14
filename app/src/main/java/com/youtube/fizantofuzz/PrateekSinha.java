package com.youtube.fizantofuzz;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PrateekSinha extends AppCompatActivity {
    TextView body;
    String about_prateek;
    ImageView back_prateek;
    FloatingActionButton share_prateek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prateek_sinha);

        body = findViewById(R.id.prateekbody);
        back_prateek = findViewById(R.id.back_btn_prateek);
        share_prateek = findViewById(R.id.share_prateek);
        about_prateek = "<p>Hi! This is Prateek Sinha here. I&apos;m passionate about making videos on YouTube and that was fulfilled by creating the Fizanto Fuzz channel. I partnered up with Aryan, my homemate and started making videos based on game recordings and roasts on common topics. Aryan handled the video editing and design part. I was assigned the work of recording voice-overs to gameplays and played the main role in crafting of scripts for our videos. Since Aryan is an introvert, I used to handle the acting part a lot.</p>" +
                "<p>Since it was my Matric, I had to concentrate a lot on my studies and thus couldn&apos;t give much time to our channel. But somehow we managed everything and started posting on YouTube. For me, till date the best video of ours is the Quba Mobile Roast video. We wrote an excellent script on that and acted nicely. I feel we could have acted better. Watching those videos now makes me cringe, but I still feel we did a good job on that.</p>" +
                "<p>Those one and a half year were brilliant. A day of scripting, one for acting out the script and then posting the video was much fun. And the response we used to get from our subscribers was heart-filling. Now, my life is all about studies and I don&apos;t have any time for YouTube. That was a very good time which helped us learn a lot of new skills. Acting is not as easy as it looks. Cinematography is the skill I got interested on. Though we not releasing new videos anymore, you can be engaged with us through the Fizanto Fuzz app. Thank you and follow me on Instagram!&nbsp;</p>";

        body.setText(Html.fromHtml(about_prateek));

        back_prateek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        share_prateek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/prateeksinha_19/"));
                startActivity(intent);
            }
        });

    }
}