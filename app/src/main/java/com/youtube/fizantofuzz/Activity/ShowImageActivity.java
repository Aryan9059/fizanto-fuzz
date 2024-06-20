package com.youtube.fizantofuzz.Activity;

import static android.os.Environment.DIRECTORY_PICTURES;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ortiz.touchview.TouchImageView;
import com.youtube.fizantofuzz.R;

public class ShowImageActivity extends AppCompatActivity {
    ImageView image_back;
    MaterialButton image_save;
    TouchImageView image_show;
    TextView image_name_tv, image_date_tv;
    FirebaseStorage firebaseStorage;
    String path;
    StorageReference storageReference;
    String image_url, image_name, image_date, image_root;

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

        image_back = findViewById(R.id.back_image);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_back.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                finish();
            }
        });

        image_show = findViewById(R.id.image_show);
        image_date_tv = findViewById(R.id.imagedate);
        image_name_tv = findViewById(R.id.imagename);
        image_url = getIntent().getStringExtra("images");
        image_name = getIntent().getStringExtra("name");
        image_date = getIntent().getStringExtra("date");
        image_root = getIntent().getStringExtra("imgname");
        Glide.with(getApplicationContext()).load(image_url).into(image_show);
        image_date_tv.setText(image_date);
        image_name_tv.setText(image_name);

        if (getIntent().getStringExtra("place").equals("0")){
            path = image_root.substring(0, 2) + image_root.substring(39, 41) + image_root.substring(62, 66);
        } else {
            path = image_root.substring(0, 2) + image_root.substring(39, 41) + "." + image_root.substring(62, 65);
        }

        Uri uri = Uri.parse(image_url);

        image_save = findViewById(R.id.save_button);
        image_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_save.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                Toast.makeText(getApplicationContext(), "Saving picture", Toast.LENGTH_SHORT).show();
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle("Saving Picture");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDescription("Saving picture on your phone.");
                request.setDestinationInExternalPublicDir(DIRECTORY_PICTURES, path);
                downloadManager.enqueue(request);
            }
        });
    }
}