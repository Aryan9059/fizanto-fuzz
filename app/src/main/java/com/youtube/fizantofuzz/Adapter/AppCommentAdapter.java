package com.youtube.fizantofuzz.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import com.youtube.fizantofuzz.Lists.ListItem;
import com.youtube.fizantofuzz.R;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class AppCommentAdapter extends RecyclerView.Adapter<AppCommentAdapter.ViewHolder> {

    List<ListItem> listViews;
    Context context;

    public AppCommentAdapter(List<ListItem> listViews, Context context) {
        this.listViews = listViews;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_comment_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final ListItem listItem = listViews.get(position);
        holder.appName.setText(listItem.getAppName());
        holder.appComment.setText(listItem.getComment());
        Glide.with(context).load(listItem.getImgurl()).into(holder.appUserThumbnail);
        if(listItem.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.image_delete.setVisibility(View.VISIBLE);
            holder.image_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) context)
                            .setTitle("Delete?")
                            .setMessage("Are you sure want to delete comment from this video?")
                            .setCancelable(false)
                            .setPositiveButton("Delete", R.drawable.ic_baseline_delete_24, new MaterialDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    FirebaseDatabase.getInstance().getReference("Comments").child(listItem.getRoot()).removeValue();
                                    listViews.remove(position);
                                    dialogInterface.dismiss();
                                    Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).setNegativeButton("Cancel", R.drawable.ic_outline_cancel_24, new MaterialDialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setAnimation(R.raw.delete_anim)
                            .build();
                    mBottomSheetDialog.show();
                }
            });
        }
//        StorageReference mImageRef = FirebaseStorage.getInstance().getReference("Profile").child(listItem.getUserId());
//        final long ONE_MEGABYTE = 1024 * 1024;
//        mImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                holder.appUserThumbnail.setImageBitmap(bm);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.i("TAG", exception.getMessage());
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return listViews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView appName, appComment;
        CircleImageView appUserThumbnail;
        ImageView image_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appName = itemView.findViewById(R.id.app_user_username);
            image_delete = itemView.findViewById(R.id.image_delete);
            appUserThumbnail = itemView.findViewById(R.id.app_user_thumbnail);
            appComment = itemView.findViewById(R.id.app_comment);
        }
    }
}
