package com.youtube.fizantofuzz.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import com.youtube.fizantofuzz.Activity.Image_show;
import com.youtube.fizantofuzz.Activity.MainActivity;
import com.youtube.fizantofuzz.Activity.TextFeedActivity;
import com.youtube.fizantofuzz.Model.Chat;
import com.youtube.fizantofuzz.R;
import com.zolad.zoominimageview.ZoomInImageViewAttacher;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static  final int MSG_TEXT_LEFT = 0;
    public static  final int MSG_TEXT_RIGHT = 1;
    public static  final int MSG_PIC_LEFT = 2;
    public static  final int MSG_PIC_RIGHT = 3;


    DatabaseReference reference;

    Context mContext;
    List<Chat> mChat;
    String imageurl;

    FirebaseUser fuser;

    public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TEXT_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        } else if(viewType == MSG_TEXT_LEFT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        } else if(viewType == MSG_PIC_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.pic_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.pic_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = mChat.get(position);

        if (chat.getType().equals("0")){
            holder.show_message.setText(chat.getMessage());

            if(mChat.get(position).getSender().equals(fuser.getUid())) {

                holder.show_message.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {

                        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext)
                                .setTitle("Delete?")
                                .setMessage("Do you want to delete your message for everyone?")
                                .setCancelable(false)
                                .setPositiveButton("Delete", R.drawable.ic_baseline_delete_24, new MaterialDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        FirebaseDatabase.getInstance().getReference("Chats").child(chat.getRoot()).removeValue();
                                        dialogInterface.dismiss();
                                        Toast.makeText(mContext, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).setNegativeButton("Cancel", R.drawable.icon_cancel, new MaterialDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setAnimation(R.raw.delete_anim)
                                .build();
                        mBottomSheetDialog.show();
                        return false;
                    }
                });

            }

        } else {
            Glide.with(mContext).load(chat.getMessage()).into(holder.show_pic);
            ZoomInImageViewAttacher zoom = new ZoomInImageViewAttacher();
            zoom.attachImageView(holder.show_pic);
            holder.show_pic.setVisibility(View.VISIBLE);
            holder.show_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, Image_show.class);
                    intent.putExtra("images", chat.getMessage());
                    intent.putExtra("date", holder.convertTime(chat.getTime()));
                    intent.putExtra("name", chat.getName());
                    intent.putExtra("imgname", chat.getImgname());
                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(holder.show_pic, "image_show");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, pairs);
                    mContext.startActivity(intent, options.toBundle());
                }
            });

            if(mChat.get(position).getSender().equals(fuser.getUid())) {

                holder.show_pic.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {

                        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext)
                                .setTitle("Delete?")
                                .setMessage("Do you want to delete your photo for everyone?")
                                .setCancelable(false)
                                .setPositiveButton("Delete", R.drawable.ic_baseline_delete_24, new MaterialDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        FirebaseDatabase.getInstance().getReference("Chats").child(chat.getRoot()).removeValue();
                                        dialogInterface.dismiss();
                                        Toast.makeText(mContext, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).setNegativeButton("Cancel", R.drawable.icon_cancel, new MaterialDialog.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setAnimation(R.raw.delete_anim)
                                .build();
                        mBottomSheetDialog.show();

                        return false;
                    }
                });

            }

        }

        if(chat.getTime()!=null && !chat.getTime().trim().equals("")) {
            holder.time_tv.setText(holder.convertTime(chat.getTime()));
        }

        if (imageurl.equals("default")){
            holder.profile_image.setImageResource(R.drawable.picsign);
        } else {
            Glide.with(mContext).load(imageurl).into(holder.profile_image);
        }

        if (position == mChat.size()-1){
            if (chat.isIsseen()){
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            holder.txt_seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public ImageView profile_image, show_pic;
        public TextView txt_seen;
        public TextView time_tv;
        public ProgressBar lottie_sender;
        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            show_pic = itemView.findViewById(R.id.show_pic);
            lottie_sender = itemView.findViewById(R.id.lottie_pic);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            time_tv = itemView.findViewById(R.id.time_tv);
        }

        public String convertTime(String time){
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            String dateString = formatter.format(new Date(Long.parseLong(time)));
            return dateString;
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid()) && mChat.get(position).getType().equals("0")){
            return MSG_TEXT_RIGHT;
        } else if (!mChat.get(position).getSender().equals(fuser.getUid()) && mChat.get(position).getType().equals("0")) {
            return MSG_TEXT_LEFT;
        } else if (mChat.get(position).getSender().equals(fuser.getUid()) && mChat.get(position).getType().equals("1")) {
            return MSG_PIC_RIGHT;
        } else{
            return MSG_PIC_LEFT;
        }
    }
}
