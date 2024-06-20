package com.youtube.fizantofuzz.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.youtube.fizantofuzz.Lists.AppCommentList;
import com.youtube.fizantofuzz.R;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class AppCommentAdapter extends RecyclerView.Adapter<AppCommentAdapter.ViewHolder> {

    List<AppCommentList> listViews;
    Context context;

    public AppCommentAdapter(List<AppCommentList> listViews, Context context) {
        this.listViews = listViews;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app_comment, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final AppCommentList appCommentList = listViews.get(position);

        FirebaseDatabase.getInstance().getReference("Users").child(appCommentList.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("verification").getValue().toString().equals("yes")){
                    holder.verify.setVisibility(View.VISIBLE);
                }
                holder.appName.setText(snapshot.child("name").getValue().toString());
                try {
                    Glide.with(context).load(snapshot.child("imageURL").getValue().toString()).into(holder.appUserThumbnail);
                } catch (Exception ignored){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        holder.appComment.setText(appCommentList.getComment());

        if(appCommentList.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    holder.parent.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    MaterialAlertDialogBuilder alertDialog1 = new MaterialAlertDialogBuilder(v.getContext());
                    alertDialog1.setTitle("Delete");
                    alertDialog1.setMessage("Are you sure want to delete your comment from this video?");
                    alertDialog1.setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference("Comments").child(appCommentList.getRoot()).removeValue();
                                    listViews.remove(position);
                                    dialog.dismiss();
                                    Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
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
    public int getItemCount() {
        return listViews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView appName, appComment;
        CircleImageView appUserThumbnail;
        LinearLayout parent;
        ImageView verify;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            appName = itemView.findViewById(R.id.app_user_username);
            appUserThumbnail = itemView.findViewById(R.id.app_user_thumbnail);
            appComment = itemView.findViewById(R.id.app_comment);
            parent = itemView.findViewById(R.id.parent_comment);
            verify = itemView.findViewById(R.id.verify_comment);
        }
    }
}
