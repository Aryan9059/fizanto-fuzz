package com.youtube.fizantofuzz.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.youtube.fizantofuzz.Activity.ShowImageActivity;
import com.youtube.fizantofuzz.YouTube.Chat;
import com.youtube.fizantofuzz.R;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SingleChatAdapter extends RecyclerView.Adapter<SingleChatAdapter.ViewHolder> {

    public static  final int MSG_TEXT_LEFT = 0;
    public static  final int MSG_TEXT_RIGHT = 1;
    public static  final int MSG_PIC_LEFT = 2;
    public static  final int MSG_PIC_RIGHT = 3;


    DatabaseReference reference;

    Context mContext;
    List<Chat> mChat;
    String imageurl;

    FirebaseUser fuser;

    public SingleChatAdapter(Context mContext, List<Chat> mChat, String imageurl){
        this.mChat = mChat;
        this.mContext = mContext;
        this.imageurl = imageurl;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TEXT_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_single_chat_right, parent, false);
            return new ViewHolder(view);
        } else if(viewType == MSG_TEXT_LEFT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_single_chat_left, parent, false);
            return new ViewHolder(view);
        } else if(viewType == MSG_PIC_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_single_pic_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_single_pic_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Chat chat = mChat.get(holder.getBindingAdapterPosition());

        String time = chat.getTime();
        if (holder.getBindingAdapterPosition() != 0){
            final String time_last = mChat.get(holder.getBindingAdapterPosition()-1).getTime();
            Timestamp timestamp = new Timestamp(Long.parseLong(time));
            Timestamp timestamp_last = new Timestamp(Long.parseLong(time_last));
            Date date = new Date(timestamp.getTime());
            Date date_last = new Date(timestamp_last.getTime());
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            @SuppressLint("SimpleDateFormat") DateFormat requiredDateFormat = new SimpleDateFormat("MMMM d");
            if (!dateFormat.format(date).equals(dateFormat.format(date_last))){
                holder.date_chat.setText(requiredDateFormat.format(date));
                holder.date_parent.setVisibility(View.VISIBLE);
            }
        } else {
            Timestamp timestamp = new Timestamp(Long.parseLong(time));
            Date date = new Date(timestamp.getTime());
            @SuppressLint("SimpleDateFormat") DateFormat requiredDateFormat = new SimpleDateFormat("MMMM d");
            holder.date_chat.setText(requiredDateFormat.format(date));
            holder.date_parent.setVisibility(View.VISIBLE);
        }

        if (chat.getType().equals("0")){
            holder.show_message.setText(chat.getMessage());

            if(mChat.get(position).getSender().equals(fuser.getUid())) {

                holder.left_parent.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        holder.left_parent.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                        MaterialAlertDialogBuilder alertDialog1 = new MaterialAlertDialogBuilder(v.getContext());
                        alertDialog1.setTitle("Delete");
                        alertDialog1.setMessage("Do you want to delete the message for everyone?");
                        alertDialog1.setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        FirebaseDatabase.getInstance().getReference("Chats").child(chat.getRoot()).removeValue();
                                        dialog.dismiss();

                                        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                                                .child(fuser.getUid())
                                                .child(mChat.get(position).getReceiver());

                                        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                chatRef.child("last").setValue("Message Deleted");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
                                                .child(mChat.get(position).getReceiver())
                                                .child(fuser.getUid());
                                        chatRefReceiver.child("id").setValue(fuser.getUid());
                                        chatRefReceiver.child("last").setValue("Message Deleted");

                                        Toast.makeText(mContext, "Deleted successfully", Toast.LENGTH_SHORT).show();
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

        } else {
            Timestamp timestamp = new Timestamp(Long.parseLong(time));
            Date date = new Date(timestamp.getTime());
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("MMMM d, h:mm a");

            Glide.with(mContext).load(chat.getMessage()).into(holder.show_pic);
            holder.show_pic.setVisibility(View.VISIBLE);
            holder.show_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ShowImageActivity.class);
                    intent.putExtra("images", chat.getMessage());
                    intent.putExtra("date", dateFormat.format(date));
                    intent.putExtra("name", chat.getName());
                    intent.putExtra("imgname", chat.getImgname());
                    intent.putExtra("place", "0");
                    mContext.startActivity(intent);
                }
            });

            if(mChat.get(position).getSender().equals(fuser.getUid())) {

                holder.show_pic.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        holder.show_pic.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                        MaterialAlertDialogBuilder alertDialog1 = new MaterialAlertDialogBuilder(v.getContext());
                        alertDialog1.setTitle("Delete");
                        alertDialog1.setMessage("Do you want to delete the picture for everyone?");
                        alertDialog1.setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(android.content.DialogInterface dialog, int which) {
                                        FirebaseDatabase.getInstance().getReference("Chats").child(chat.getRoot()).removeValue();
                                        dialog.dismiss();
                                        Toast.makeText(mContext, "Deleted successfully", Toast.LENGTH_SHORT).show();
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

        if(chat.getTime()!=null && !chat.getTime().trim().equals("")) {
            holder.time_tv.setText(holder.convertTime(chat.getTime()));
        }

        if (position == mChat.size()-1){
            if (chat.isIsseen()){
                holder.txt_seen.setText(" · Seen");
            } else {
                holder.txt_seen.setText(" · Delivered");
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

        public TextView show_message, date_chat;
        public CardView date_parent;
        public LinearLayout left_parent;

        public ImageView show_pic;
        public TextView txt_seen;
        public TextView time_tv;
        public ViewHolder(View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            show_pic = itemView.findViewById(R.id.show_pic);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            time_tv = itemView.findViewById(R.id.time_tv);
            left_parent = itemView.findViewById(R.id.left_parent);
            date_chat = itemView.findViewById(R.id.date_chat);
            date_parent = itemView.findViewById(R.id.date_parent);
        }

        public String convertTime(String time){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
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
