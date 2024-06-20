package com.youtube.fizantofuzz.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.youtube.fizantofuzz.Dialog.AddTextFeedDialog;
import com.youtube.fizantofuzz.R;

public class TextFeedActivity extends AppCompatActivity {
    String image, body, date, title, parent;
    private ImageView imageView, back_btn, edit_btn;
    private TextView title_tv, body_tv, date_tv;
    SharedPreferences prefs;

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
        setContentView(R.layout.activity_text_feed);

        title_tv = findViewById(R.id.onefeedtitle);
        back_btn = findViewById(R.id.feed_back);
        body_tv = findViewById(R.id.onefeedtext);
        date_tv = findViewById(R.id.onefeeddate);
        edit_btn = findViewById(R.id.edit_feed);
        imageView = findViewById(R.id.iamgeonefeed);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back_btn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

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

        image = getIntent().getStringExtra("image");
        body = getIntent().getStringExtra("body");
        date = getIntent().getStringExtra("date");
        title = getIntent().getStringExtra("title");
        parent = getIntent().getStringExtra("parent");

        title_tv.setText(title);
        body_tv.setText(Html.fromHtml(body));
        date_tv.setText("Dated: " + date);

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_btn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                DialogFragment dialog = new AddTextFeedDialog();
                Bundle bundle = new Bundle();
                bundle.putBoolean("feed_inside", true);
                bundle.putString("image", image);
                bundle.putString("body", body);
                bundle.putString("date", date);
                bundle.putString("title", title);
                bundle.putString("parent", parent);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "feed");
            }
        });

        if (!image.equals("no")) {
            Glide.with(getApplicationContext()).load(image).into(imageView);
        }
    }
}