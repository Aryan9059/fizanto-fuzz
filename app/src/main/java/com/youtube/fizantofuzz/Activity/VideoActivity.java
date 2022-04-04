package com.youtube.fizantofuzz.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;
import com.google.android.material.snackbar.Snackbar;
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
import com.wessam.library.NetworkChecker;
import com.youtube.fizantofuzz.Model.Comments.Comment;
import com.youtube.fizantofuzz.Model.VideoStats.VideoStats;
import com.youtube.fizantofuzz.R;
import com.youtube.fizantofuzz.Retrofit.GetDataService;
import com.youtube.fizantofuzz.Retrofit.RetrofitInstance;
import com.youtube.fizantofuzz.Adapter.AppCommentAdapter;
import com.youtube.fizantofuzz.Adapter.CommentsAdapter;
import com.youtube.fizantofuzz.Lists.ListItem;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import io.github.pierry.progress.Progress;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoActivity extends AppCompatActivity {

    Bundle bundle;
    String videoID, fullName, userId, imageURL;
    YouTubePlayerView playerView;
    ConstraintLayout constraint_video;
    TextView views, likes, dislikes, video_title, video_date, video_description;
    CommentsAdapter commentsAdapter;
    RecyclerView commentsRecyclerView, appRecyclerView;
    RecyclerView.Adapter adapter;
    Progress dialog;
    private List<ListItem> listItems;
    ImageView appCommentButton, collapse_btn;
    EditText appCommentText;
    private FirebaseUser fuser;
    DatabaseReference reference;

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        video_title = findViewById(R.id.title_video);
        video_date = findViewById(R.id.date_video);
        constraint_video = findViewById(R.id.constraint_video);
        video_description = findViewById(R.id.video_des);
        views = findViewById(R.id.views);
        appCommentButton = findViewById(R.id.commentButton);
        appCommentText = findViewById(R.id.commentText);
        likes = findViewById(R.id.likes);
        dislikes = findViewById(R.id.dislikes);
        appRecyclerView = findViewById(R.id.appComment);
        appRecyclerView.setHasFixedSize(true);
        dialog = new Progress(VideoActivity.this);
        appRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listItems = new ArrayList<>();
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        bundle = new Bundle();
        bundle = getIntent().getExtras();
        playerView = findViewById(R.id.playerview);
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userId = fuser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        Bundle details = getIntent().getExtras();
        video_title.setText(details.getString("video_title"));
        video_date.setText(details.getString("video_date"));
        video_description.setText(details.getString("video_des"));
        CardView cardView = findViewById(R.id.card_video);
        collapse_btn = findViewById(R.id.collapse_btn);

        collapse_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video_description.getVisibility() == View.GONE){
                    TransitionManager.beginDelayedTransition(cardView,  new AutoTransition());
                    video_description.setVisibility(View.VISIBLE);
                    collapse_btn.setImageURI(Uri.parse("android.resource://com.youtube.fizantofuzz/drawable/collapse_arrow"));
                } else {
                    TransitionManager.beginDelayedTransition(cardView,  new AutoTransition());
                    video_description.setVisibility(View.GONE);
                    collapse_btn.setImageURI(Uri.parse("android.resource://com.youtube.fizantofuzz/drawable/expand_arrow"));
                }
            }
        });

        if(!NetworkChecker.isNetworkConnected(this)){
            Snackbar.make(constraint_video, "No internet connection.", Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", null).setActionTextColor(getResources().getColor(R.color.ui_accent3)).setTextColor(getResources().getColor(R.color.text_main)).setBackgroundTint(getResources().getColor(R.color.back_dark)).show();
        }

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
                    sendMessage(fullName, commentString, userId, videoID, imageURL);
                }
            }
        });

        FirebaseDatabase.getInstance().getReference("Comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listItems.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("senderName").getValue().toString();
                    String id = snapshot.child("senderId").getValue().toString();
                    String videoid = snapshot.child("videoId").getValue().toString();
                    String comment = snapshot.child("message").getValue().toString();
                    String root = snapshot.child("root").getValue().toString();
                    String imgurl = snapshot.child("imageURL").getValue().toString();

                    if(videoid.equals(videoID)){
                        ListItem listItem = new ListItem(name, comment, id, root, imgurl);
                        listItems.add(listItem);
                    }

                    adapter = new AppCommentAdapter(listItems, VideoActivity.this);
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

        ImageView share_video = findViewById(R.id.video_share);
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
                    Toast.makeText(VideoActivity.this, "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onFailure(Call<Comment.Model> call, Throwable t) {
                Toast.makeText(VideoActivity.this, "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
                Log.e("TAG", t.getMessage());
            }
        });
    }

    private void setUpRecyclerView(List<Comment.Item> items) {
        commentsAdapter = new CommentsAdapter(VideoActivity.this, items);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VideoActivity.this);
        commentsRecyclerView.setLayoutManager(layoutManager);
        commentsRecyclerView.setAdapter(commentsAdapter);
    }

    private void getStats() {
        GetDataService dataService = RetrofitInstance.getRetrofit().create(GetDataService.class);
        Call<VideoStats> videoStatsRequest = dataService.getVideoStats("statistics", "AIzaSyAzXyjw8hhgvecFzTfGosS72Bkor457wAs", videoID);
        videoStatsRequest.enqueue(new Callback<VideoStats>() {
            @Override
            public void onResponse(Call<VideoStats> call, Response<VideoStats> response) {
                if(response.isSuccessful()){
                    if(response.body()!=null){
                        views.setText(response.body().getItems().get(0).getStatistics().getViewCount());
                        likes.setText(response.body().getItems().get(0).getStatistics().getLikeCount());
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
    }

    private void sendMessage(String senderName, String message, String senderId, String videoID, String imageURL){
        final Date currentTime = Calendar.getInstance().getTime();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(fuser.getUid() + currentTime.toString());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("senderName", senderName);
        hashMap.put("message", message);
        hashMap.put("root", fuser.getUid() + currentTime.toString());
        hashMap.put("senderId", senderId);
        hashMap.put("videoId", videoID);
        hashMap.put("imageURL", imageURL);
        reference.setValue(hashMap);

        ListItem listItem2 = new ListItem(this.fullName, appCommentText.getText().toString(), senderId, fuser.getUid() + currentTime.toString(), imageURL);
        appCommentText.setText("");
        adapter.notifyDataSetChanged();
        listItems.add(listItem2);
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
        pictureInPictureView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_picture_in_picture_24));

        pictureInPictureView.setOnClickListener( view -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                boolean supportsPIP = getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE);
                if(supportsPIP)
                    enterPictureInPictureMode();
                    playerView.getPlayerUIController().showUI(false);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Can't enter picture in picture mode")
                        .setMessage("In order to enter picture in picture mode you need a SDK version >= N.")
                        .show();
            }
        });
        youTubePlayerView.getPlayerUIController().addView( pictureInPictureView );
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);

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
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        });
    }
}
