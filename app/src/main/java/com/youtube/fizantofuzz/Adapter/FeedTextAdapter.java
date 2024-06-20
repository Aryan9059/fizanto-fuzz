package com.youtube.fizantofuzz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
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
import com.youtube.fizantofuzz.Lists.FeedTextList;
import com.youtube.fizantofuzz.Activity.TextFeedActivity;
import com.youtube.fizantofuzz.R;
import java.util.List;

public class FeedTextAdapter extends RecyclerView.Adapter<FeedTextAdapter.ViewHolder>{
        List<FeedTextList> listViews;
        Context context;

    public FeedTextAdapter(List<FeedTextList> listViews, Context context) {
        this.listViews = listViews;
        this.context = context;
    }

    @NonNull
    @Override
    public FeedTextAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_feed, parent, false);
        return new FeedTextAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedTextAdapter.ViewHolder holder, int position) {
        final FeedTextList listItem = listViews.get(position);
        holder.title.setText(listItem.getTitle());
        holder.body.setText(Html.fromHtml(listItem.getText()));
        holder.date.setText(listItem.getDate());

        FirebaseDatabase.getInstance().getReference("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString().contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    holder.feedLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            holder.feedLayout.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                            MaterialAlertDialogBuilder alertDialog1 = new MaterialAlertDialogBuilder(v.getContext());
                            alertDialog1.setTitle("Delete");
                            alertDialog1.setMessage("Do you want to delete this feed from the app?");
                            alertDialog1.setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(android.content.DialogInterface dialog, int which) {
                                            FirebaseDatabase.getInstance().getReference("Feed").child(listItem.getParent()).removeValue();
                                            dialog.dismiss();
                                            Toast.makeText(context, "Feed deleted successfully", Toast.LENGTH_SHORT).show();
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

        if (listItem.getImage().equals("no")){
            Log.e("IMAGES", "No Image Present");
        } else {
            Glide.with(context).load(listItem.getImage()).into(holder.image);
            holder.image.setVisibility(View.VISIBLE);
        }

        holder.feedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TextFeedActivity.class);
                intent.putExtra("body", listItem.getText());
                intent.putExtra("date", listItem.getDate());
                intent.putExtra("image", listItem.getImage());
                intent.putExtra("title", listItem.getTitle());
                intent.putExtra("parent", listItem.getParent());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listViews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, body, date;
        ImageView image;
        MaterialCardView feedLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            feedLayout = itemView.findViewById(R.id.card_click_feed);
            title = itemView.findViewById(R.id.feedtitle);
            body = itemView.findViewById(R.id.feedbody);
            date = itemView.findViewById(R.id.feeddate);
            image = itemView.findViewById(R.id.feedimage);
        }
    }
}
