package com.youtube.fizantofuzz.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.youtube.fizantofuzz.YouTube.Comments.Comment;
import com.youtube.fizantofuzz.YouTube.VideoStats.VideoStats;
import com.youtube.fizantofuzz.R;
import com.youtube.fizantofuzz.Retrofit.GetDataService;
import com.youtube.fizantofuzz.Retrofit.RetrofitInstance;
import com.youtube.fizantofuzz.Adapter.AppCommentAdapter;
import com.youtube.fizantofuzz.Adapter.YouTubeCommentsAdapter;
import com.youtube.fizantofuzz.Lists.AppCommentList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoActivity extends AppCompatActivity {

    Bundle bundle;
    String videoID, fullName, userId, imageURL;
    YouTubePlayerView playerView;
    ConstraintLayout constraint_video;
    TextView views, likes, video_title, video_date;
    YouTubeCommentsAdapter commentsAdapter;
    RecyclerView commentsRecyclerView, appRecyclerView;
    RecyclerView.Adapter adapter;
    private List<AppCommentList> appCommentLists;
    ImageView appCommentButton;
    EditText appCommentText;
    private FirebaseUser fuser;
    DatabaseReference reference;
    SharedPreferences prefs;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int flg = getWindow().getAttributes().flags;
        if ((flg & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else{
            finish();
        }
    }

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
        setContentView(R.layout.activity_video);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.screen_background, typedValue,true);
        getWindow().setStatusBarColor(typedValue.data);

        video_title = findViewById(R.id.title_video);
        video_date = findViewById(R.id.date_video);
        constraint_video = findViewById(R.id.constraint_video);
        views = findViewById(R.id.views);
        appCommentButton = findViewById(R.id.commentButton);
        appCommentText = findViewById(R.id.commentText);

        appCommentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){
                    appCommentButton.setVisibility(View.GONE);
                } else {
                    appCommentButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        likes = findViewById(R.id.likes);
        appRecyclerView = findViewById(R.id.appComment);
        appRecyclerView.setHasFixedSize(true);
        appRecyclerView.setNestedScrollingEnabled(false);
        appRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        appCommentLists = new ArrayList<>();
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setNestedScrollingEnabled(false);
        bundle = new Bundle();
        bundle = getIntent().getExtras();
        playerView = findViewById(R.id.playerview);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userId = fuser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        Bundle details = getIntent().getExtras();
        video_title.setText(details.getString("video_title"));
        video_date.setText(details.getString("video_des"));
        ConstraintLayout cardVideo = findViewById(R.id.card_video);

        cardVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardVideo.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                final Dialog dialog = new Dialog(VideoActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bottomsheet_video);

                TextView bottom_title = dialog.findViewById(R.id.bottom_title);
                TextView bottom_likes = dialog.findViewById(R.id.bottom_likes);
                TextView bottom_views = dialog.findViewById(R.id.bottom_views);
                TextView bottom_date = dialog.findViewById(R.id.bottom_date);
                TextView bottom_des = dialog.findViewById(R.id.bottom_des);
                TextView bottom_day = dialog.findViewById(R.id.bottom_day);
                MaterialButton bottom_link = dialog.findViewById(R.id.bottom_link);

                bottom_title.setText(details.getString("video_title"));
                bottom_des.setText(details.getString("video_des"));
                String date = details.getString("video_day");
                bottom_date.setText(date);
                String day = details.getString("video_date");
                bottom_day.setText(day);
                videoID = bundle.getString("videoID");
                bottom_link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoID));
                        startActivity(intent);
                    }
                });

                GetDataService dataService = RetrofitInstance.getRetrofit().create(GetDataService.class);
                Call<VideoStats> videoStatsRequest = dataService.getVideoStats("statistics", "AIzaSyAzXyjw8hhgvecFzTfGosS72Bkor457wAs", videoID);
                videoStatsRequest.enqueue(new Callback<VideoStats>() {
                    @Override
                    public void onResponse(Call<VideoStats> call, Response<VideoStats> response) {
                        if(response.isSuccessful()){
                            if(response.body()!=null){
                                bottom_views.setText(response.body().getItems().get(0).getStatistics().getViewCount());
                                bottom_likes.setText(response.body().getItems().get(0).getStatistics().getLikeCount());
                            } else{
                                Log.e(">>>> RESPONSE BODY NULL", "NULL");
                            }
                        } else{
                            Log.e("RESPONSE UNSUCCESSFUL", String.valueOf(response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<VideoStats> call, Throwable t) {
                        Toast.makeText(VideoActivity.this, "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();         }
                });

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
                dialog.getWindow().setGravity(Gravity.BOTTOM);

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               fullName = snapshot.child("name").getValue().toString();
               imageURL = snapshot.child("imageURL").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        appCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String commentString = appCommentText.getText().toString();
                if(commentString.isEmpty()){
                    Toast.makeText(VideoActivity.this, "Please type something", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendMessage(commentString, userId, videoID);
                }
            }
        });

        FirebaseDatabase.getInstance().getReference("Comments").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                appCommentLists.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.child("senderId").getValue().toString();
                    String videoID = snapshot.child("videoId").getValue().toString();
                    String comment = snapshot.child("message").getValue().toString();
                    String root = snapshot.child("root").getValue().toString();

                    if(videoID.equals(VideoActivity.this.videoID)){
                        AppCommentList appCommentList = new AppCommentList(comment, id, root);
                        appCommentLists.add(appCommentList);
                    }

                    adapter = new AppCommentAdapter(appCommentLists, VideoActivity.this);
                    appRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG", "Read failed");
            }
        });

        if(bundle!=null){
            videoID = bundle.getString("videoID");
            Log.e("YOUTUBE VIDEO ID", videoID);
        } else
            Log.e("VIDEO ID NULL", videoID.concat(" "));

        MaterialCardView share_video = findViewById(R.id.share);
        share_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" + videoID);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Video");
                startActivity(Intent.createChooser(shareIntent, "Share..."));
            }
        });

        initYouTubePlayerView();
        getStats();
        getCommentData();
    }

    private void getCommentData() {
        GetDataService dataService = RetrofitInstance.getRetrofit().create(GetDataService.class);
        Call<Comment.Model> commentsRequest = dataService
                .getCommentsData("snippet", videoID, "100", "AIzaSyAzXyjw8hhgvecFzTfGosS72Bkor457wAs");

        commentsRequest.enqueue(new Callback<Comment.Model>() {
            @Override
            public void onResponse(Call<Comment.Model> call, Response<Comment.Model> response) {
                if(response.isSuccessful()){
                    if(response.body()!=null){
                        Log.e("TAG", "Response Successful");
                        setUpRecyclerView(response.body().getItems());
                    }
                    else{
                        Log.e("TAG","Response Body is Null");
                    }
                }
                else{
                    Log.e("TAG","Response Unsuccessful");
                    Toast.makeText(VideoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onFailure(Call<Comment.Model> call, Throwable t) {
                Toast.makeText(VideoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.getMessage());
            }
        });
    }

    private void setUpRecyclerView(List<Comment.Item> items) {
        commentsAdapter = new YouTubeCommentsAdapter(VideoActivity.this, items);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VideoActivity.this);
        commentsRecyclerView.setLayoutManager(layoutManager);
        commentsRecyclerView.setAdapter(commentsAdapter);
    }

    private void getStats() {
        GetDataService dataService = RetrofitInstance.getRetrofit().create(GetDataService.class);
        Call<VideoStats> videoStatsRequest = dataService.getVideoStats("statistics", "AIzaSyAzXyjw8hhgvecFzTfGosS72Bkor457wAs", videoID);
        videoStatsRequest.enqueue(new Callback<VideoStats>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<VideoStats> call, Response<VideoStats> response) {
                if(response.isSuccessful()){
                    if(response.body()!=null){
                        views.setText(response.body().getItems().get(0).getStatistics().getViewCount() + " Views");
                        likes.setText(response.body().getItems().get(0).getStatistics().getLikeCount() + " Likes");
                    } else{
                        Log.e(">>>> RESPONSE BODY NULL", "NULL");
                    }
                } else{
                    Log.e("RESPONSE UNSUCCESSFUL", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<VideoStats> call, Throwable t) {
                Toast.makeText(VideoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();         }
        });
    }

    private void sendMessage(String message, String senderId, String videoID){
        appCommentText.setText("");
        final Date currentTime = Calendar.getInstance().getTime();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(fuser.getUid() + currentTime);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("root", fuser.getUid() + currentTime);
        hashMap.put("senderId", senderId);
        hashMap.put("videoId", videoID);
        reference.setValue(hashMap);
    }

    private void initYouTubePlayerView() {
        getLifecycle().addObserver(playerView);
        initPictureInPicture(playerView);

        playerView.initialize(youTubePlayer -> {
            youTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady() {
                    loadVideo(youTubePlayer, videoID);
                }
            });
            addFullScreenListenerToPlayer(youTubePlayer);
        }, true);
    }

    private void initPictureInPicture(YouTubePlayerView youTubePlayerView) {
        ImageView pictureInPictureView = new ImageView(this);
        pictureInPictureView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_picture_in_picture_24));

        pictureInPictureView.setOnClickListener( view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                boolean supportsPIP = getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE);
                if(supportsPIP){
                    enterPictureInPictureMode();
                    playerView.getPlayerUIController().showUI(false);
                }
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Can't enter picture in picture mode")
                        .setMessage("In order to enter picture in picture mode you need a SDK version >= N.")
                        .show();
            }
        });
        youTubePlayerView.getPlayerUIController().addView( pictureInPictureView );
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        }

        if(isInPictureInPictureMode) {
            playerView.enterFullScreen();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            playerView.getPlayerUIController().showUI(false);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            playerView.exitFullScreen();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            playerView.getPlayerUIController().showUI(true);
        }
    }

    private void loadVideo(YouTubePlayer youTubePlayer, String videoId) {
        youTubePlayer.cueVideo(videoId, 0);
        youTubePlayer.loadVideo(videoId, 0);
    }

    private void addFullScreenListenerToPlayer(final com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer youTubePlayer) {
        playerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {

            @Override
            public void onYouTubePlayerEnterFullScreen() {
                playerView.setFitsSystemWindows(true);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            @SuppressLint("SourceLockedOrientationActivity")
            @Override
            public void onYouTubePlayerExitFullScreen() {
                playerView.setFitsSystemWindows(false);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        });
    }
}
