package com.youtube.fizantofuzz.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.youtube.fizantofuzz.R;

public class MainActivity extends AppCompatActivity {
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = getSharedPreferences("Themes", MODE_PRIVATE);
        String themeMode = prefs.getString("current", "");
        switch (themeMode) {
            case "":
            case "Main":
                setTheme(R.style.Theme_Splash_Main);
                break;
            case "Blue":
                setTheme(R.style.Theme_Splash_Blue);
                break;
            case "Yellow":
                setTheme(R.style.Theme_Splash_Yellow);
                break;
            case "Pink":
                setTheme(R.style.Theme_Splash_Pink);
                break;
            case "Green":
                setTheme(R.style.Theme_Splash_Green);
                break;
            case "Teal":
                setTheme(R.style.Theme_Splash_Teal);
                break;
            case "Purple":
                setTheme(R.style.Theme_Splash_Purple);
                break;
            case "Red":
                setTheme(R.style.Theme_Splash_Red);
                break;
        }
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_main);

        ImageView image = findViewById(R.id.imageView9);
        FirebaseUser firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();

        Intent mainIntent;
        if (firebaseAuth != null) {
            mainIntent = new Intent(MainActivity.this, HomeActivity.class);
        } else {
            mainIntent = new Intent(MainActivity.this, LoginActivity.class);
        }
        startActivity(mainIntent);
        MainActivity.this.finish();
    }
}




