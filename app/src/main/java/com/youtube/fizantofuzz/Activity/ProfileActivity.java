package com.youtube.fizantofuzz.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
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
import com.youtube.fizantofuzz.Dialog.UpdateProfileDialog;
import com.youtube.fizantofuzz.YouTube.User;
import com.youtube.fizantofuzz.Dialog.ChangePasswordDialog;
import com.youtube.fizantofuzz.R;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    MaterialCardView change_pic;
    private DatabaseReference reference;
    private ImageView profile_picture;
    SharedPreferences prefs;
    Uri uri;
    String email;
    TextView email_tv, name_tv, about_text;
    ImageView profile_back, verify;
    ConstraintLayout sign_out_cl, password_cl, name_cl, delete_cl;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private StorageTask uploadTask;

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
        setContentView(R.layout.activity_profile);

        email_tv = findViewById(R.id.email_tv);
        name_tv = findViewById(R.id.name_tv);
        about_text = findViewById(R.id.about_text);
        sign_out_cl = findViewById(R.id.sign_out_cl);
        password_cl = findViewById(R.id.password_cl);
        name_cl = findViewById(R.id.name_cl);
        verify = findViewById(R.id.verify_img);
        delete_cl = findViewById(R.id.delete_cl);

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri ->{
            if (uri != null){
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(ProfileActivity.this);
                dialogBuilder.setView(R.layout.dialog_progress_pic)
                        .setCancelable(false).create();

                AlertDialog pd = dialogBuilder.create();
                pd.show();

                final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "");

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
                            String mUri = downloadUri.toString();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageURL", ""+mUri);
                            reference.updateChildren(map);
                        }

                        else {
                            Toast.makeText(ProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });

                if (uploadTask != null && uploadTask.isInProgress()) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                            ,0);
                }
            }
        });

        profile_picture = findViewById(R.id.profile_picture_p);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getVerification().equals("yes")) {
                    verify.setVisibility(View.VISIBLE);
                }
                name_tv.setText(user.getUsername());
                about_text.setText(user.getBio());
                if (user.getImageURL().equals("default")){
                    profile_picture.setImageResource(R.drawable.default_profile_picture);
                } else {
                    try {
                        Glide.with(ProfileActivity.this).load(user.getImageURL()).into(profile_picture);
                    } catch (Exception exception){
                        Log.e("Error", exception+"");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        delete_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                MaterialAlertDialogBuilder alertDialog1 = new MaterialAlertDialogBuilder(v.getContext());
                alertDialog1.setTitle("Delete Account");
                alertDialog1.setMessage("Do you want to delete your account forever? You wont be able to recover any kind of data related to your account afterwards.");
                alertDialog1.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                databaseReference.child("name").setValue("Deleted User");
                                databaseReference.child("imageURL").setValue("default");
                                databaseReference.child("verification").setValue("no");
                                databaseReference.child("search").setValue("deleted_acc");
                                databaseReference.child("bio").setValue("This account was deleted by the user.");
                                databaseReference.child("username").setValue("Deleted User");
                                FirebaseAuth.getInstance().getCurrentUser().delete();
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                                finish();
                            }
                        }).setCancelable(true)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                alertDialog1.show();
            }
        });

        sign_out_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_out_cl.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        });

        name_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new UpdateProfileDialog();
                dialog.show(getSupportFragmentManager(), "update");
            }
        });

        password_cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordDialog passwordDialog = new ChangePasswordDialog();
                passwordDialog.show(getSupportFragmentManager(), "passwordDialog");
            }
        });

        change_pic = findViewById(R.id.pic_change);

        change_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_pic.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                openImage();
            }
        });

        profile_back = findViewById(R.id.profile_back);
        profile_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        email_tv.setText(email);
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