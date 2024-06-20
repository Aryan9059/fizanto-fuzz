package com.youtube.fizantofuzz.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.youtube.fizantofuzz.BuildConfig;
import com.youtube.fizantofuzz.R;

public class SettingsActivity extends AppCompatActivity {
    ImageView settings_back;
    TextView version_tv;
    CardView version_cv;
    DatabaseReference firebaseDatabase, fdata;
    SharedPreferences prefs;
    private String version, latest_v, link;
    int i;
    ConstraintLayout design_cl, update_cl, source_cl, mail_cl, privacy_cl, terms_cl;

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
        setContentView(R.layout.activity_settings);

        version_cv = findViewById(R.id.version_cv);
        version_tv = findViewById(R.id.version_tv);
        version_tv.setText("Currently using v" + BuildConfig.VERSION_NAME);

        i = 0;
        version_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                version_cv.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                if (i == 0){
                    i++;
                } else if(i == 1){
                    i++;
                } else if(i == 2){
                    i++;
                } else{
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SettingsActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.dialog_version, null);
                    builder.setView(view)
                            .setTitle("App Version")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                    i = 0;
                }
            }
        });

        settings_back = findViewById(R.id.settings_back);
        settings_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Version");

        design_cl = findViewById(R.id.design_cl);
        design_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, DesignActivity.class));
            }
        });

        update_cl = findViewById(R.id.update_cl);
        update_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        latest_v = snapshot.getValue().toString();

                        try {
                            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                            version = pInfo.versionName;
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        if(!version.equals(latest_v)){
                            fdata = FirebaseDatabase.getInstance().getReference("Link");
                            fdata.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    link = snapshot.getValue().toString();

                                    MaterialAlertDialogBuilder alertDialog1 = new MaterialAlertDialogBuilder(SettingsActivity.this);
                                    alertDialog1.setTitle("Update");
                                    alertDialog1.setMessage("A new version of Fizanto Fuzz App is available to download.");
                                    alertDialog1.setPositiveButton("Download", new android.content.DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                    startActivity(intent);
                                                    dialog.dismiss();
                                                }
                                            }).setCancelable(true)
                                            .setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(android.content.DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create();
                                    alertDialog1.show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        } else {
                            MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(SettingsActivity.this);
                            alertDialog.setTitle("Update");
                            alertDialog.setMessage("The Fizanto Fuzz App is up to date.");
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).setCancelable(true)
                                    .create();
                            alertDialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        source_cl = findViewById(R.id.source_code_cl);
        source_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Aryan9059/fizanto-fuzz")));
            }
        });

        mail_cl = findViewById(R.id.mail_cl);
        mail_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:fizantofuzz@gmail.com")));
            }
        });

        privacy_cl = findViewById(R.id.privacy_cs);
        privacy_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, PolicyActivity.class));
            }
        });

        terms_cl = findViewById(R.id.terms_cs);
        terms_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, TermsActivity.class));
            }
        });

    }
}