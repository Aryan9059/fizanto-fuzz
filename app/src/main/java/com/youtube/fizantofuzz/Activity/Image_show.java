package com.youtube.fizantofuzz.Activity;

import static android.os.Environment.DIRECTORY_PICTURES;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ortiz.touchview.TouchImageView;
import com.youtube.fizantofuzz.R;

public class Image_show extends AppCompatActivity {
    ImageView image_back, image_save;
    TouchImageView image_show;
    TextView imagename, imagedate;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    String image_url, image_name, image_date, image_root;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        image_back = findViewById(R.id.back_image);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        image_show = findViewById(R.id.image_show);
        imagedate = findViewById(R.id.imagedate);
        imagename = findViewById(R.id.imagename);
        image_url = getIntent().getStringExtra("images");
        image_name = getIntent().getStringExtra("name");
        image_date = getIntent().getStringExtra("date");
        image_root = getIntent().getStringExtra("imgname");
        Glide.with(getApplicationContext()).load(image_url).into(image_show);
        imagedate.setText(image_date);
        imagename.setText(image_name);

        image_save = findViewById(R.id.save_button);
        image_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseStorage = FirebaseStorage.getInstance();
                storageReference = firebaseStorage.getReference("uploads").child(image_root);
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(getApplicationContext(), "Download has started", Toast.LENGTH_SHORT).show();
                        DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalFilesDir(getApplicationContext(), DIRECTORY_PICTURES, image_root);
                        downloadManager.enqueue(request);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Download failed", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}