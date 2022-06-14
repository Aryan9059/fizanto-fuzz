package com.youtube.fizantofuzz.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.youtube.fizantofuzz.R;
import com.youtube.fizantofuzz.Adapter.AppChatAdapter;
import com.youtube.fizantofuzz.Model.APIService;
import com.youtube.fizantofuzz.Model.User;
import com.youtube.fizantofuzz.Notifications.Client;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {

    private RecyclerView chat_recyclerview;
    private ImageView emojipng;
    private List<com.youtube.fizantofuzz.ListItemChat> listItemsChat;
    private RecyclerView.Adapter chatappadapter;
    ImageView sendchat;
    APIService apiService;
    Uri uri;
    private EmojiEditText sendchatedittext;
    String userId, fullName;
    ImageView imageButton;
    View rootview;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    boolean notify = false;
    boolean notifyg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userId = fuser.getUid();
        rootview = findViewById(R.id.rootview_chat);
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullName = snapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        sendchat = findViewById(R.id.sendchatbutton);
        imageButton = findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyg = true;
                StartCrop();
            }
        });

        sendchatedittext = findViewById(R.id.sendchatedittext);
        listItemsChat = new ArrayList<>();
        emojipng = findViewById(R.id.emojipng);

        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootview).build(sendchatedittext);
        emojipng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emojiPopup.isShowing()){
                    emojipng.setImageURI(Uri.parse("android.resource://com.youtube.fizantofuzz/drawable/icon_emoji"));
                } else {
                    emojipng.setImageURI(Uri.parse("android.resource://com.youtube.fizantofuzz/drawable/icon_keyboard"));
                }
                emojiPopup.toggle();
            }
        });

        chat_recyclerview = findViewById(R.id.recycler_chat);
        chat_recyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        chat_recyclerview.setLayoutManager(linearLayoutManager);
        chatappadapter = new AppChatAdapter(listItemsChat, getApplicationContext());
        chat_recyclerview.setAdapter(chatappadapter);

        ImageView back_grp = findViewById(R.id.back_grp);
        back_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupChatActivity.super.onBackPressed();
            }
        });

        FirebaseDatabase.getInstance().getReference("ChatsG").orderByChild("time").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listItemsChat.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("senderName").getValue().toString();
                    String id = snapshot.child("senderId").getValue().toString();
                    String type = snapshot.child("type").getValue().toString();
                    String message = snapshot.child("message").getValue().toString();
                    String root = snapshot.child("root").getValue().toString();
                    String time = snapshot.child("time").getValue().toString();
                    com.youtube.fizantofuzz.ListItemChat listItemChat = new com.youtube.fizantofuzz.ListItemChat(message, name, id, type, root, time);
                    if(!listItemsChat.contains(listItemChat)){
                        listItemsChat.add(listItemChat);
                        chatappadapter.notifyDataSetChanged();
                        chat_recyclerview.scrollToPosition(listItemsChat.size() - 1);
                        chat_recyclerview.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG", "Read failed");
            }
        });

        sendchat.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                notify = true;
                final String messageString = sendchatedittext.getText().toString();
                if(messageString.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Type something", Toast.LENGTH_SHORT).show();
                } else{
                    final Date currentTime = Calendar.getInstance().getTime();
                    DatabaseReference refe = FirebaseDatabase.getInstance().getReference("ChatsG").child(fuser.getUid() + currentTime);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("senderName", fullName);
                    hashMap.put("message", messageString);
                    hashMap.put("senderId", fuser.getUid());
                    hashMap.put("type", "1");
                    hashMap.put("root", fuser.getUid() + currentTime.toString());
                    hashMap.put("time", (-new Date().getTime()) + "");
                    refe.setValue(hashMap);
                    com.youtube.fizantofuzz.ListItemChat listItem3 = new com.youtube.fizantofuzz.ListItemChat(messageString, fullName, fuser.getUid(), "1", fuser.getUid() + currentTime.toString(), (-new Date().getTime()) + "");
                    sendchatedittext.setText("");
                    chatappadapter.notifyDataSetChanged();
                    listItemsChat.add(listItem3);
                    chat_recyclerview.scrollToPosition(listItemsChat.size());
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if (notify) {
                                sendNotification(fuser.getUid(), user.getUsername(), messageString);
                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });
    }

    private void StartCrop() {
        CropImage.startPickImageActivity(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(getApplicationContext(), data);
            if (CropImage.isReadExternalStoragePermissionsRequired(getApplicationContext(), imageUri)) {
                uri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                        ,0);
            } else{
                startCrop(imageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                final ProgressDialog pd = new ProgressDialog(this);
                pd.setIndeterminateDrawable(getResources().getDrawable(R.drawable.icon_upload));
                pd.setMessage("Uploading...");
                final Date currentTime = Calendar.getInstance().getTime();
                final StorageReference storageReference = FirebaseStorage.getInstance().getReference(fuser.getUid() + currentTime.toString());
                MaterialDialog mDialog = new MaterialDialog.Builder(this)
                        .setTitle("Upload?")
                        .setMessage("Are you sure want to upload this image in Chats?")
                        .setCancelable(false)
                        .setPositiveButton("Upload", R.drawable.ic_round_done_24, new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                try {
                                    dialogInterface.dismiss();
                                    pd.show();
                                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    byte[] data1 = baos.toByteArray();
                                    final UploadTask uploadTask = storageReference.putBytes(data1);
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatsG").child(fuser.getUid() + currentTime.toString());
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("senderName", fullName);
                                            hashMap.put("message", fuser.getUid() + currentTime.toString());
                                            hashMap.put("senderId", fuser.getUid());
                                            hashMap.put("type", "2");
                                            hashMap.put("root", fuser.getUid() + currentTime.toString());
                                            hashMap.put("time", (-new Date().getTime()) + "");
                                            reference.setValue(hashMap);
                                            pd.dismiss();
                                            reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

                                            reference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    User user = dataSnapshot.getValue(User.class);
                                                    if (notifyg) {
                                                        sendNotification(fuser.getUid(), user.getUsername(), "📷 Photo");
                                                    }
                                                    notifyg = false;
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
                                        }
                                    });

                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Failed to upload photo", Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                        }
                                    });

                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                    pd.dismiss();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", R.drawable.icon_cancel, new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setAnimation(R.raw.upload_anim)
                        .build();
                mDialog.show();
            }
        }
    }

    private void startCrop(Uri imageUri) {
        Intent intent = CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .getIntent(this);
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void sendNotification(String receiver, final String username, final String message) {
    }

}
