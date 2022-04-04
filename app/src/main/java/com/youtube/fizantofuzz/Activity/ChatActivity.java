package com.youtube.fizantofuzz.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import com.wessam.library.NetworkChecker;
import com.youtube.fizantofuzz.R;
import com.youtube.fizantofuzz.Model.Utils;
import com.youtube.fizantofuzz.Adapter.OnItemClick;
import com.youtube.fizantofuzz.Fragment.ChatsFragment;
import com.youtube.fizantofuzz.Fragment.ProfileFragment;
import com.youtube.fizantofuzz.Fragment.UsersFragment;
import com.youtube.fizantofuzz.Model.Chat;
import com.youtube.fizantofuzz.Model.User;
import java.util.ArrayList;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements OnItemClick {

    CircleImageView profile_image;
    TextView username;
    ImageView logout;
    FirebaseAuth firebaseAuth;
    ProgressDialog dialog;
    LinearLayout linear_chat;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    OnItemClick onItemClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        this.onItemClick = this;

        linear_chat = findViewById(R.id.linear_chat);

        dialog = Utils.showLoader(ChatActivity.this);

        profile_image = findViewById(R.id.profile_image);
        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        final ViewPager viewPager = findViewById(R.id.view_pager);

        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                }
            }
        };

        profile_image.setOnClickListener(view -> {
            TabLayout.Tab tab = tabLayout.getTabAt(2);
            tab.select();

        });

        username = findViewById(R.id.username);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        logout = findViewById(R.id.logout_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);

        if(!NetworkChecker.isNetworkConnected(this)){
            dialog.dismiss();
            Snackbar.make(linear_chat, "No internet connection.", Snackbar.LENGTH_LONG)
                    .setAction("Back", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    }).setActionTextColor(getResources().getColor(R.color.ui_accent3)).setTextColor(getResources().getColor(R.color.text_main)).setBackgroundTint(getResources().getColor(R.color.back_dark)).show();
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BottomSheetMaterialDialog mDialog = new BottomSheetMaterialDialog.Builder(ChatActivity.this)
                        .setTitle("Logout?")
                        .setMessage("Do you want to logout from this user?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", R.drawable.ic_round_done_24, new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                FirebaseAuth.getInstance().signOut();
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("No", R.drawable.ic_outline_cancel_24, new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setAnimation(R.raw.exit_app)
                        .build();
                mDialog.getAnimationView().setScaleType(ImageView.ScaleType.FIT_CENTER);
                mDialog.show();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.drawable.picsign);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                int unread = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isIsseen()){
                        unread++;
                    }
                }

                if (unread == 0){
                    viewPagerAdapter.addFragment(ChatsFragment.newInstance(onItemClick), "Chats");
                } else {
                    viewPagerAdapter.addFragment(ChatsFragment.newInstance(onItemClick), "("+unread+") Chats");
                }

                viewPagerAdapter.addFragment(UsersFragment.newInstance(onItemClick), "Users");
                viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");
                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);

                if(dialog!=null){
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case  R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChatActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }
        return false;
    }

    @Override
    public void onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    }

    @Override
    public void onItemCLick(String uid, View view) {

        ViewProfileActivity viewProfileActivity =
                ViewProfileActivity.newInstance(uid,this);
        viewProfileActivity.show(getSupportFragmentManager(),
                "view_profile");
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
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
