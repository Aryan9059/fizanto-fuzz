package com.youtube.fizantofuzz.Fragment;

import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wessam.library.NetworkChecker;
import com.youtube.fizantofuzz.Activity.AryanSrivastava;
import com.youtube.fizantofuzz.Activity.ChatActivity;
import com.youtube.fizantofuzz.PrateekSinha;
import com.youtube.fizantofuzz.R;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private BottomNavigationView navigationBar;
    private FirebaseUser firebaseUser;
    ImageSlider sliderView;
    CardView aryan_card, prateek_card;
    LinearLayout instagram, telegram, twitter, gmail, github_btn, share_btn;
    private DatabaseReference firebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        navigationBar = getActivity().findViewById(R.id.bottom_nav);
        aryan_card = root.findViewById(R.id.card_aryan);
        prateek_card = root.findViewById(R.id.card_prateek);
        sliderView = root.findViewById(R.id.image_slider);
        instagram = root.findViewById(R.id.instagram);
        telegram = root.findViewById(R.id.telegram);
        twitter = root.findViewById(R.id.twitter);
        gmail = root.findViewById(R.id.gmail);
        github_btn = root.findViewById(R.id.github_btn);
        share_btn = root.findViewById(R.id.share_our_app_btn);

        aryan_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AryanSrivastava.class));
            }
        });

        prateek_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), PrateekSinha.class));
            }
        });

        github_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Aryan9059/fizanto-fuzz"));
                startActivity(intent);
            }
        });

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "http://bit.ly/fizantofuzzsite");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Our App");
                startActivity(Intent.createChooser(shareIntent, "Share..."));
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/fizantofuzz/"));
                startActivity(intent);
            }
        });

        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/fizantofuzzchannel"));
                startActivity(intent);
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/fizantofuzz"));
                startActivity(intent);
            }
        });

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:fizantofuzz@gmail.com?subject=Feedback%20On%20App&body=Hey!"));
                startActivity(emailIntent);
            }
        });

        root.findViewById(R.id.nested_scroll_view).setFocusable(false);
        root.findViewById(R.id.image_slider).requestFocus();


        final List<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.slider1,
                "", ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.slider2,
                "", ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.slider3,
                "", ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.slider4,
                "", ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.slider5,
                "", ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.slider6,
                "", ScaleTypes.FIT));

        sliderView.setImageList(imageList, ScaleTypes.FIT);

        sliderView.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {
                if(i == 1) {
                    if(NetworkChecker.isNetworkConnected(getContext())){
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VideosFragment()).commit();
                        navigationBar.setSelectedItemId(R.id.bottom_videos);
                    }

                }
                if(i == 2) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                    builder.setMessage("You can add this widget by long pressing on your homescreen and clicking the Widget option and selecting the Fizanto Dial.")
                            .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setTitle("Add Widget")
                            .create().show();
                }
                if(i == 3) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://bit.ly/fizantofuzzsite"));
                    startActivity(intent);
                }
                if(i == 4) {
                    startActivity(new Intent(getContext(), ChatActivity.class));
                }
                if(i == 5) {
                    if(NetworkChecker.isNetworkConnected(getContext())){
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FeedsFragment()).commit();
                        navigationBar.setSelectedItemId(R.id.bottom_feeds);
                    }
                }
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return root;
    }
}