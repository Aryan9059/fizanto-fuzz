package com.youtube.fizantofuzz.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.youtube.fizantofuzz.Adapter.SingleChatAdapter;
import com.youtube.fizantofuzz.YouTube.APIService;
import com.youtube.fizantofuzz.YouTube.Chat;
import com.youtube.fizantofuzz.YouTube.User;
import com.youtube.fizantofuzz.Notifications.Client;
import com.youtube.fizantofuzz.Notifications.Data;
import com.youtube.fizantofuzz.Notifications.MyResponse;
import com.youtube.fizantofuzz.Notifications.Sender;
import com.youtube.fizantofuzz.Notifications.Token;
import com.youtube.fizantofuzz.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingleChatActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username, status;
    SharedPreferences prefs;
    private ImageView emojiPng;
    View rootview;
    CardView card_progress;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    Toolbar toolbar;
    FirebaseUser fuser;
    DatabaseReference reference;
    ImageView imageButton;
    ImageView btn_send, verify_img;
    EmojiEditText text_send;
    SingleChatAdapter messageAdapter;
    List<Chat> mChat;
    Uri uri;
    RecyclerView recyclerView;
    Intent intent;
    ValueEventListener seenListener;
    String userid, username_str, verify;
    APIService apiService;
    boolean notify = false;
    boolean notifyGroup = false;
    private StorageTask uploadTask;
    StorageReference storageReference;
    private Uri imageUri;
    private static final int IMAGE_REQUEST = 1;
    String mUri;

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
        setContentView(R.layout.activity_single_chat);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.bar_background, typedValue,true);
        getWindow().setStatusBarColor(typedValue.data);
        getWindow().setNavigationBarColor(typedValue.data);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        emojiPng = findViewById(R.id.emojipng);
        card_progress = findViewById(R.id.card_progress);
        text_send = findViewById(R.id.text_send);
        rootview = findViewById(R.id.rootview);
        status = findViewById(R.id.status_user);
        verify_img = findViewById(R.id.verify_img);
        toolbar = findViewById(R.id.toolbar);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri ->{
            if (uri != null){
                ContentResolver contentResolver = getContentResolver();
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                String type = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

                final Date currentTime = Calendar.getInstance().getTime();
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(SingleChatActivity.this);
                dialogBuilder.setView(R.layout.dialog_progress_pic)
                        .setCancelable(false).create();

                AlertDialog materialDialogs = dialogBuilder.create();
                materialDialogs.show();

                if (uri != null){
                    storageReference = FirebaseStorage.getInstance().getReference("uploads");
                    final StorageReference fileReference = storageReference.child(fuser.getUid() + currentTime + "." + type);

                    uploadTask = fileReference.putFile(uri);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw  task.getException();
                            }

                            return  fileReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                Uri downloadUri = task.getResult();
                                mUri = downloadUri.toString();
                                String time = String.valueOf(System.currentTimeMillis());
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(fuser.getUid() + currentTime + "");
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sender", fuser.getUid());
                                hashMap.put("receiver", userid);
                                hashMap.put("message", ""+mUri);
                                hashMap.put("isseen", false);
                                hashMap.put("time", time);
                                hashMap.put("ctime", currentTime);
                                hashMap.put("name", username_str);
                                hashMap.put("type", "1");
                                hashMap.put("root", fuser.getUid() + currentTime + "");
                                hashMap.put("imgname", fuser.getUid() + currentTime + "." + type);

                                reference.setValue(hashMap);

                                final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                                        .child(fuser.getUid())
                                        .child(userid);

                                chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()){
                                            chatRef.child("id").setValue(userid);
                                        }
                                        chatRef.child("last").setValue("📷 Photo");
                                        chatRef.child("time").setValue("-"+time);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                                        .child(userid)
                                        .child(fuser.getUid());
                                chatRefReceiver.child("id").setValue(fuser.getUid());
                                chatRefReceiver.child("last").setValue("📷 Photo");
                                chatRefReceiver.child("time").setValue("-"+time);

                                reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        if (notifyGroup) {
                                            sendNotification(userid, user.getUsername(), "📷 Photo");
                                        }
                                        notifyGroup = false;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                            materialDialogs.dismiss();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            materialDialogs.dismiss();
                        }
                    });

                    if (uploadTask != null && uploadTask.isInProgress()) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                                ,0);
                    }
                }
            }
        });

        text_send.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){
                    btn_send.setVisibility(View.GONE);
                } else {
                    btn_send.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(v -> {
            notifyGroup = true;
            imageButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            openImage();
        });

        ImageView back_mess = findViewById(R.id.back_mess);
        back_mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back_mess.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                SingleChatActivity.super.onBackPressed();
            }
        });

        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootview).build(text_send);
        emojiPng.setOnClickListener(v -> {
            if(emojiPopup.isShowing()){
                emojiPng.setImageURI(Uri.parse("android.resource://com.youtube.fizantofuzz/drawable/ic_baseline_emoji_24"));
            } else {
                emojiPng.setImageURI(Uri.parse("android.resource://com.youtube.fizantofuzz/drawable/ic_baseline_keyboard_24"));
            }
            emojiPopup.toggle();
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(view -> {
            notify = true;
            String msg = text_send.getText().toString();
            String time = String.valueOf(System.currentTimeMillis());
            if (!msg.equals("")){
                sendMessage(fuser.getUid(), userid, msg, time);
            } else {
                Toast.makeText(SingleChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
            }
            text_send.setText("");
        });


        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username_str = user.getUsername();
                verify = user.getVerification();
                status.setText(user.getBio());

                toolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(SingleChatActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.bottomsheet_profile);

                        ImageView bottom_verify = dialog.findViewById(R.id.dialog_verify);
                        TextView bottom_name = dialog.findViewById(R.id.dialog_name);
                        TextView bottom_email = dialog.findViewById(R.id.dialog_email);
                        TextView bottom_status = dialog.findViewById(R.id.dialog_status);
                        CircleImageView bottom_profile = dialog.findViewById(R.id.dialog_profile);

                        bottom_name.setText(user.getUsername());
                        if (user.getVerification().equals("yes")){
                            bottom_verify.setVisibility(View.VISIBLE);
                        }
                        bottom_status.setText(user.getBio());
                        FirebaseDatabase.getInstance().getReference("Users").child(user.getId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                bottom_email.setText(snapshot.child("email").getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        if (!user.getImageURL().equals("default")){Glide.with(SingleChatActivity.this).load(user.getImageURL()).into(bottom_profile);}
                        dialog.show();
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
                        dialog.getWindow().setGravity(Gravity.BOTTOM);
                    }
                });

                if (verify.equals("yes")){
                    verify_img.setVisibility(View.VISIBLE);
                }

                if(Objects.equals(user.getStatus(), "online")){
                    status.setText("Online");
                }
                username.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.drawable.default_profile_picture);
                } else {
                    //and this
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                }

                readMessages(fuser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(userid);
    }

    private void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message, String time){

        final Date currentTime = Calendar.getInstance().getTime();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chats").child(fuser.getUid() + currentTime + "");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("time", time);
        hashMap.put("ctime", currentTime);
        hashMap.put("name", username_str);
        hashMap.put("type", "0");
        hashMap.put("root", fuser.getUid() + currentTime + "");
        hashMap.put("imgname", "null");

        reference.setValue(hashMap);

        // add user to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    chatRef.child("id").setValue(userid);
                }
                chatRef.child("last").setValue(message);
                chatRef.child("time").setValue("-"+time);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        
        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(userid)
                .child(fuser.getUid());
        chatRefReceiver.child("id").setValue(fuser.getUid());
        chatRefReceiver.child("last").setValue(message);
        chatRefReceiver.child("time").setValue("-"+time);

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotification(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotification(String receiver, final String username, final String message){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(), R.drawable.notification_icon, message + "", username + "",
                            userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessages(final String myID, final String userid, final String imageurl){
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.orderByChild("time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if ((chat.getReceiver().equals(myID) && chat.getSender().equals(userid)) ||
                            (chat.getReceiver().equals(userid) && chat.getSender().equals(myID))){
                        mChat.add(chat);
                    }
                }
                messageAdapter = new SingleChatAdapter(SingleChatActivity.this, mChat, imageurl);
                recyclerView.setAdapter(messageAdapter);
                card_progress.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
        currentUser("none");
    }

    private void openImage() {
        pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());

    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}
