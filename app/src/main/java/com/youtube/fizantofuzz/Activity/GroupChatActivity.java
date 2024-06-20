package com.youtube.fizantofuzz.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.youtube.fizantofuzz.Lists.GroupChatList;
import com.youtube.fizantofuzz.R;
import com.youtube.fizantofuzz.Adapter.GroupChatAdapter;
import com.youtube.fizantofuzz.YouTube.APIService;
import com.youtube.fizantofuzz.YouTube.User;
import com.youtube.fizantofuzz.Notifications.Client;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {

    private RecyclerView chat_recyclerview;
    private ImageView emoji_png;
    private StorageTask uploadTask;
    private List<GroupChatList> listItemsChat;
    private RecyclerView.Adapter chat_app_adapter;
    ImageView send_chat;
    APIService apiService;
    Uri uri;
    SharedPreferences prefs;
    private EmojiEditText send_chat_edit_text;
    String userId, fullName;
    ImageView imageButton;
    View rootview;
    private FirebaseUser fuser;
    private DatabaseReference reference;
    boolean notify = false;
    boolean notify_g = false;
    private static final int IMAGE_REQUEST = 1;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private Uri imageUri = null;
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
        setContentView(R.layout.activity_group_chat);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri ->{
            if (uri != null){
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(GroupChatActivity.this);
                dialogBuilder.setView(R.layout.dialog_progress_pic)
                        .setCancelable(false).create();

                AlertDialog materialDialogs = dialogBuilder.create();
                materialDialogs.show();

                ContentResolver contentResolver = getContentResolver();
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                String type = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

                final Date currentTime = Calendar.getInstance().getTime();

                final StorageReference storageReference = FirebaseStorage.getInstance().getReference(fuser.getUid() + currentTime + "." + type);

                uploadTask = storageReference.putFile(uri);
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw  task.getException();
                        }

                        return  storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri downloadUri = task.getResult();
                                    String mUri = downloadUri.toString();
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatsG").child(fuser.getUid() + currentTime + type + "");
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("senderName", fullName);
                                    hashMap.put("message", mUri);
                                    hashMap.put("senderId", fuser.getUid());
                                    hashMap.put("type", "2");
                                    hashMap.put("root", fuser.getUid() + currentTime + type + "");
                                    hashMap.put("time", (-new Date().getTime()) + "");
                                    reference.setValue(hashMap);
                                    materialDialogs.dismiss();
                                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            User user = dataSnapshot.getValue(User.class);
                                            if (notify_g) {
                                                sendNotification(fuser.getUid(), user.getUsername(), "📷 Photo");
                                            }
                                            notify_g = false;
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
                                    materialDialogs.dismiss();
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
        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        userId = fuser.getUid();
        rootview = findViewById(R.id.rootview_chat);
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.bar_background, typedValue,true);
        getWindow().setStatusBarColor(typedValue.data);
        getWindow().setNavigationBarColor(typedValue.data);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullName = snapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        send_chat = findViewById(R.id.sendchatbutton);
        imageButton = findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                notify_g = true;
                openImage();
            }
        });

        send_chat_edit_text = findViewById(R.id.sendchatedittext);
        listItemsChat = new ArrayList<>();
        emoji_png = findViewById(R.id.emojipng);

        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootview).build(send_chat_edit_text);
        emoji_png.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emojiPopup.isShowing()){
                    emoji_png.setImageURI(Uri.parse("android.resource://com.youtube.fizantofuzz/drawable/ic_baseline_emoji_24"));
                } else {
                    emoji_png.setImageURI(Uri.parse("android.resource://com.youtube.fizantofuzz/drawable/ic_baseline_keyboard_24"));
                }
                emojiPopup.toggle();
            }
        });

        send_chat_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")){
                    send_chat.setVisibility(View.GONE);
                } else {
                    send_chat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        chat_recyclerview = findViewById(R.id.recycler_chat);
        chat_recyclerview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        chat_recyclerview.setLayoutManager(linearLayoutManager);
        chat_app_adapter = new GroupChatAdapter(listItemsChat, getApplicationContext());
        chat_recyclerview.setAdapter(chat_app_adapter);

        ImageView back_grp = findViewById(R.id.back_grp);
        back_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
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
                    GroupChatList groupChatList = new GroupChatList(message, name, id, type, root, time);
                    if(!listItemsChat.contains(groupChatList)){
                        listItemsChat.add(groupChatList);
                        chat_app_adapter.notifyDataSetChanged();
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

        send_chat.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                notify = true;
                final String messageString = send_chat_edit_text.getText().toString();
                if(messageString.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Type something", Toast.LENGTH_SHORT).show();
                } else{
                    final Date currentTime = Calendar.getInstance().getTime();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ChatsG").child(fuser.getUid() + currentTime);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("senderName", fullName);
                    hashMap.put("message", messageString);
                    hashMap.put("senderId", fuser.getUid());
                    hashMap.put("type", "1");
                    hashMap.put("root", fuser.getUid() + currentTime + "");
                    hashMap.put("time", (-new Date().getTime()) + "");
                    ref.setValue(hashMap);
                    GroupChatList listItem3 = new GroupChatList(messageString, fullName, fuser.getUid(), "1", fuser.getUid() + currentTime + "", (-new Date().getTime()) + "");
                    send_chat_edit_text.setText("");
                    chat_app_adapter.notifyDataSetChanged();
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

    private void openImage() {
        pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void sendNotification(String receiver, final String username, final String message) {
    }

}
