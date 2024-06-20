package com.youtube.fizantofuzz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.youtube.fizantofuzz.Lists.FeedStoryList;
import com.youtube.fizantofuzz.R;
import com.youtube.fizantofuzz.Activity.StoriesActivity;

import java.util.List;

public class FeedStoryAdapter extends RecyclerView.Adapter<FeedStoryAdapter.ViewHolder0> {

    List<FeedStoryList> feedStoryLists;
    Context context;

    public FeedStoryAdapter(List<FeedStoryList> feedStoryLists, Context context) {
        this.feedStoryLists = feedStoryLists;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder0 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story_feed, parent, false);
        return new FeedStoryAdapter.ViewHolder0(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder0 holder, int position) {
        final FeedStoryList feedStoryList = feedStoryLists.get(position);
        Glide.with(context).load(feedStoryList.getPic()).into(holder.imageView);
        holder.time.setText(feedStoryList.getDate());
        holder.title.setText(feedStoryList.getTitle());

        FirebaseDatabase.getInstance().getReference("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString().contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            holder.linearLayout.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                            MaterialAlertDialogBuilder alertDialog1 = new MaterialAlertDialogBuilder(v.getContext());
                            alertDialog1.setTitle("Delete");
                            alertDialog1.setMessage("Do you want to delete this story from the app?");
                            alertDialog1.setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                            FirebaseDatabase.getInstance().getReference("Stories").child(feedStoryList.getParent()).removeValue();
                                            dialog.dismiss();
                                            Toast.makeText(context, "Story deleted successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).setCancelable(true)
                                    .setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create();
                            alertDialog1.show();
                            return false;
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StoriesActivity.class);
                intent.putExtra("title", feedStoryList.getTitle());
                intent.putExtra("Urlofv", feedStoryList.getUrl());
                intent.putExtra("date", feedStoryList.getDate());
                intent.putExtra("parent", feedStoryList.getParent());
                intent.putExtra("pic", feedStoryList.getPic());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedStoryLists.size();
    }

    public class ViewHolder0 extends RecyclerView.ViewHolder {

        private MaterialCardView linearLayout;
        TextView time, title;
        private ImageView imageView;

        public ViewHolder0(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.activity_main_ratio);
            imageView = itemView.findViewById(R.id.image_story);
            time = itemView.findViewById(R.id.time_tv);
            title = itemView.findViewById(R.id.title_tv);
        }
    }
}
