package com.youtube.fizantofuzz.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.youtube.fizantofuzz.Activity.CreatorActivity;
import com.youtube.fizantofuzz.Activity.LoginActivity;
import com.youtube.fizantofuzz.Activity.ProfileActivity;
import com.youtube.fizantofuzz.YouTube.User;
import com.youtube.fizantofuzz.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private BottomNavigationView navigationBar;
    private FirebaseUser firebaseUser;
    LinearLayout aryan_ll, prateek_ll;
    CardView profile_cv;
    CircleImageView profile_picture;
    ImageView log_out_btn;
    MaterialButton github_btn, share_btn;
    MaterialCardView instagram, telegram, twitter, gmail;
    private DatabaseReference firebaseDatabase, reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        navigationBar = getActivity().findViewById(R.id.bottom_nav);
        aryan_ll = root.findViewById(R.id.aryan_ll);
        prateek_ll = root.findViewById(R.id.prateek_ll);
        instagram = root.findViewById(R.id.instagram);
        telegram = root.findViewById(R.id.telegram);
        twitter = root.findViewById(R.id.twitter);
        gmail = root.findViewById(R.id.gmail);
        github_btn = root.findViewById(R.id.github_btn);
        share_btn = root.findViewById(R.id.share_our_app_btn);

        log_out_btn = root.findViewById(R.id.log_out_btn);
        log_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log_out_btn.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                MaterialAlertDialogBuilder alertDialog1 = new MaterialAlertDialogBuilder(v.getContext());
                alertDialog1.setTitle("Logout");
                alertDialog1.setMessage("Do you want to logout from your account?");
                alertDialog1.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getContext(), LoginActivity.class));
                                getActivity().finish();
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

        profile_cv = root.findViewById(R.id.profile_cv);
        profile_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile_cv.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                startActivity(new Intent(getContext(), ProfileActivity.class));
            }
        });

        profile_picture = root.findViewById(R.id.profile_picture);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getImageURL().equals("default")){
                    profile_picture.setImageResource(R.drawable.default_profile_picture);
                } else {
                    try {
                        Glide.with(getContext()).load(user.getImageURL()).into(profile_picture);
                    } catch (Exception exception){
                        Log.e("Error", exception+"");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        aryan_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aryan_ll.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                Intent intent = new Intent(getContext(), CreatorActivity.class);
                intent.putExtra("name", "Aryan");
                startActivity(intent);
            }
        });

        prateek_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prateek_ll.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                Intent intent = new Intent(getContext(), CreatorActivity.class);
                intent.putExtra("name", "Prateek");
                startActivity(intent);
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
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://github.com/Aryan9059/fizanto-fuzz/releases/download/4.5/app-debug.apk");
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