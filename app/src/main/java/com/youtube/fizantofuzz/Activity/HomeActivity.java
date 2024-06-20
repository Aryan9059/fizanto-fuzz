package com.youtube.fizantofuzz.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.wessam.library.NetworkChecker;
import com.youtube.fizantofuzz.Adapter.HomePagerAdapter;
import com.youtube.fizantofuzz.Fragment.ChatFragment;
import com.youtube.fizantofuzz.Fragment.FeedsFragment;
import com.youtube.fizantofuzz.Fragment.HomeFragment;
import com.youtube.fizantofuzz.Fragment.VideosFragment;
import com.youtube.fizantofuzz.R;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
public class HomeActivity extends AppCompatActivity{
    private String version, latest_v, link;
    BottomNavigationView btm;
    CircleImageView profile_btn_chat;
    ImageView settings_btn;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    FloatingActionButton fab_add;
    CoordinatorLayout coordinatorLayout3;
    CollapsingToolbarLayout ctl;
    DatabaseReference firebaseDatabase, fdata;
    SharedPreferences prefs;
    ViewPager2 pagerMain;
    ArrayList<Fragment> arr = new ArrayList<>();

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
        setContentView(R.layout.activity_home);

        OneSignal.promptForPushNotifications();

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.navigation, typedValue,true);
        getWindow().setNavigationBarColor(typedValue.data);

        pagerMain = findViewById(R.id.fragment_container_new);
        arr.add(new HomeFragment());
        arr.add(new VideosFragment());
        arr.add(new ChatFragment());
        arr.add(new FeedsFragment());

        fab_add = findViewById(R.id.fab_add);
        profile_btn_chat = findViewById(R.id.profile_btn_chat);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, NewChatActivity.class));
            }
        });

        coordinatorLayout3 = findViewById(R.id.coordinator_layout_snackbar);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        profile_btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings_btn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });

        FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("imageURL").getValue().toString();
                if (!image.equals("default")){
                    Glide.with(getApplicationContext()).load(image).into(profile_btn_chat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ctl = findViewById(R.id.ctl_home);
        btm = findViewById(R.id.bottom_nav);
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Version");

        settings_btn = findViewById(R.id.settings_btn);
        settings_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings_btn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            }
        });

        if(!NetworkChecker.isNetworkConnected(this)){
            TypedValue typedValue_text = new TypedValue();
            Resources.Theme theme_text = getTheme();
            theme.resolveAttribute(R.attr.screen_background, typedValue_text,true);

            TypedValue typedValue_back = new TypedValue();
            Resources.Theme theme_back = getTheme();
            theme.resolveAttribute(R.attr.bar_ui, typedValue_back,true);

            Snackbar.make(coordinatorLayout3, "No internet connection.", Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", null).setTextColor(typedValue_text.data).setBackgroundTint(typedValue_back.data).show();
        }

        HomePagerAdapter viewPagerAdapter = new HomePagerAdapter(this, arr);
        pagerMain.setAdapter(viewPagerAdapter);

        pagerMain.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                switch (position){

                    case 0:
                        btm.getMenu().getItem(0).setChecked(true);
                        profile_btn_chat.setVisibility(View.GONE);
                        fab_add.setVisibility(View.GONE);
                        ctl.setTitle("Home");
                        break;

                    case 1:
                        btm.getMenu().getItem(1).setChecked(true);
                        profile_btn_chat.setVisibility(View.GONE);
                        fab_add.setVisibility(View.GONE);
                        ctl.setTitle("Videos");
                        break;

                    case 2:
                        btm.getMenu().getItem(2).setChecked(true);
                        profile_btn_chat.setVisibility(View.VISIBLE);
                        fab_add.setVisibility(View.VISIBLE);
                        ctl.setTitle("Chats");
                        break;

                    case 3:
                        btm.getMenu().getItem(3).setChecked(true);
                        profile_btn_chat.setVisibility(View.GONE);
                        fab_add.setVisibility(View.GONE);
                        ctl.setTitle("Feeds");
                        break;

                }
            }
        });

        btm.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.bottom_home:
                        btm.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                        pagerMain.setCurrentItem(0);
                        break;

                    case R.id.bottom_videos:
                        btm.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                        pagerMain.setCurrentItem(1);
                        break;

                    case R.id.bottom_chats:
                        btm.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                        pagerMain.setCurrentItem(2);
                        break;
                    case R.id.bottom_feeds:
                        btm.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                        pagerMain.setCurrentItem(3);
                        break;

                }
                return false;
            }
        });

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

                            MaterialAlertDialogBuilder alertDialog1 = new MaterialAlertDialogBuilder(HomeActivity.this);
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }



    @Override
    public void onBackPressed(){

        if (pagerMain.getCurrentItem() == 0) {
            super.onBackPressed();
            finish();
        } else {
            pagerMain.setCurrentItem(0);
        }

    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

}
