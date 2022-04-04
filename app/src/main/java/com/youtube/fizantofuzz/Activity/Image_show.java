package com.youtube.fizantofuzz.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.youtube.fizantofuzz.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

public class Image_show extends AppCompatActivity {
    ImageView image_back, image_save;
    ImageViewZoom image_show;
    TextView imagename, imagedate;
    String image_url, image_name, image_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_show);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        Glide.with(getApplicationContext()).load(image_url).into(image_show);
        imagedate.setText(image_date);
        imagename.setText(image_name);

        image_save = findViewById(R.id.save_button);
        image_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Glide.with(getApplicationContext()).asBitmap().load(image_url).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            try {
                                Toast.makeText(Image_show.this, "Download Started", Toast.LENGTH_SHORT).show();
                                saveImageToExternal("anything", resource);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
                    //URL url = new URL(image_url);
                    //Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch(Exception e) {
                    System.out.println(e);
                }
            }
        });

    }

    public void saveImageToExternal(String imgName, Bitmap bm) throws IOException {
        //Create Path to save Image
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM); //Creates app specific folder
        path.mkdirs();
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());

        File file = wrapper.getDir("Images",MODE_PRIVATE);

        file = new File(file, image_name+image_date+".png");// Imagename.png
        try{
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream); // Compress Image
            stream.flush();
            stream.close();
            Toast.makeText(Image_show.this, "Download Done", Toast.LENGTH_SHORT).show();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(getApplicationContext(),new String[] { file.getAbsolutePath() }, null,new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        } catch(Exception e) {
            Log.e("ExternalStorage", e + "");
            throw new IOException();
        }
    }

}