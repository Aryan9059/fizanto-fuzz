package com.youtube.fizantofuzz.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.youtube.fizantofuzz.Activity.ShowImageActivity;
import com.youtube.fizantofuzz.Lists.GroupChatList;
import com.youtube.fizantofuzz.R;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatAdapter extends RecyclerView.Adapter{

    List<GroupChatList> listViewsChat;
    Context context;
    private String verify;
    private DatabaseReference firebaseDatabase;
    String userdata = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public GroupChatAdapter(List<GroupChatList> listViewsChat, Context context) {
        this.listViewsChat = listViewsChat;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(listViewsChat.get(position).getObjectId().equals(userdata) && listViewsChat.get(position).getType().equals("1")){
            return 0;
        } else if(listViewsChat.get(position).getObjectId().equals(userdata) && listViewsChat.get(position).getType().equals("2")){
            return 1; 
        } else if(!listViewsChat.get(position).getObjectId().equals(userdata) && listViewsChat.get(position).getType().equals("1")){
            return 2;
        }
        return 3;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType == 0){
            view = layoutInflater.inflate(R.layout.item_group_chat_right, parent, false);
            return new ViewHolder1(view);
        } else if(viewType == 2){
        view = layoutInflater.inflate(R.layout.item_group_chat_left, parent, false);
        return new ViewHolder2(view);
        } else if(viewType == 1){
            view = layoutInflater.inflate(R.layout.item_group_pic_right, parent, false);
            return new ViewHolder3(view);
        }
        view = layoutInflater.inflate(R.layout.item_group_pic_left, parent, false);
        return new ViewHolder4(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if(listViewsChat.get(position).getObjectId().equals(userdata) && listViewsChat.get(position).getType().equals("1")){
            ViewHolder1 viewHolder1 = (ViewHolder1) holder;

            String time = listViewsChat.get(position).getTime().replace("-", "");
            Timestamp timestamp = new Timestamp(Long.parseLong(time));
            Date date = new Date(timestamp.getTime());
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("MMMM d, h:mm a");
            viewHolder1.time_tv.setText(dateFormat.format(date));

            viewHolder1.senderMessage.setText(listViewsChat.get(position).getMessage());
            viewHolder1.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    viewHolder1.linearLayout.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    MaterialAlertDialogBuilder alertDialog1 = new MaterialAlertDialogBuilder(v.getContext());
                    alertDialog1.setTitle("Delete");
                    alertDialog1.setMessage("Do you want to delete the message for everyone?");
                    alertDialog1.setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference("ChatsG").child(listViewsChat.get(position).getRoot()).removeValue();
                                    dialog.dismiss();
                                    Toast.makeText(context, "Message deleted successfully", Toast.LENGTH_SHORT).show();
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

        else if(!listViewsChat.get(position).getObjectId().equals(userdata) && listViewsChat.get(position).getType().equals("1")){
            final ViewHolder2 viewHolder2 = (ViewHolder2) holder;

            String time = listViewsChat.get(position).getTime().replace("-", "");
            Timestamp timestamp = new Timestamp(Long.parseLong(time));
            Date date = new Date(timestamp.getTime());
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("MMMM d, h:mm a");
            viewHolder2.time_tv.setText(dateFormat.format(date));

            FirebaseDatabase.getInstance().getReference("Users").child(listViewsChat.get(position).getObjectId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("verification").getValue().toString().equals("yes")){
                        viewHolder2.verify.setVisibility(View.VISIBLE);
                    }
                    Glide.with(context).load(snapshot.child("imageURL").getValue().toString()).into(viewHolder2.profile_pic);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            viewHolder2.receiverName.setText(listViewsChat.get(position).getUserChatName());
            viewHolder2.receiverMessage.setText(listViewsChat.get(position).getMessage());
            firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(listViewsChat.get(position).getObjectId());
        }

        else if(listViewsChat.get(position).getObjectId().equals(userdata) && listViewsChat.get(position).getType().equals("2")){
            final ViewHolder3 viewHolder3 = (ViewHolder3) holder;

            String time = listViewsChat.get(position).getTime().replace("-", "");
            Timestamp timestamp = new Timestamp(Long.parseLong(time));
            Date date = new Date(timestamp.getTime());
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("MMMM d, h:mm a");
            viewHolder3.time_tv.setText(dateFormat.format(date));

            viewHolder3.senderPic.setVisibility(View.VISIBLE);
            Glide.with(context).load(Uri.parse(listViewsChat.get(position).getMessage())).into(viewHolder3.senderPic);

            viewHolder3.senderPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShowImageActivity.class);
                    intent.putExtra("images", listViewsChat.get(position).getMessage());
                    intent.putExtra("date", dateFormat.format(date));
                    intent.putExtra("name", listViewsChat.get(position).getUserChatName());
                    intent.putExtra("imgname", listViewsChat.get(position).getRoot());
                    intent.putExtra("place", "1");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            viewHolder3.senderPic.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    viewHolder3.senderPic.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                    MaterialAlertDialogBuilder alertDialog1 = new MaterialAlertDialogBuilder(v.getContext());
                    alertDialog1.setTitle("Delete");
                    alertDialog1.setMessage("Do you want to delete the message for everyone?");
                    alertDialog1.setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(android.content.DialogInterface dialog, int which) {
                                    FirebaseDatabase.getInstance().getReference("ChatsG").child(listViewsChat.get(position).getRoot()).removeValue();
                                    dialog.dismiss();
                                    Toast.makeText(context, "Message deleted successfully", Toast.LENGTH_SHORT).show();
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

        else {
            final ViewHolder4 viewHolder4 = (ViewHolder4) holder;
            viewHolder4.receiverUsernamePic.setText(listViewsChat.get(position).getUserChatName());

            String time = listViewsChat.get(position).getTime().replace("-", "");
            Timestamp timestamp = new Timestamp(Long.parseLong(time));
            Date date = new Date(timestamp.getTime());
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("MMMM d, h:mm a");
            viewHolder4.time_tv.setText(dateFormat.format(date));

            FirebaseDatabase.getInstance().getReference("Users").child(listViewsChat.get(position).getObjectId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("verification").getValue().toString().equals("yes")){
                        viewHolder4.verify.setVisibility(View.VISIBLE);
                    }
                    Glide.with(context).load(snapshot.child("imageURL").getValue().toString()).into(viewHolder4.profile_pic);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            viewHolder4.receiverPic.setVisibility(View.VISIBLE);
            Glide.with(context).load(Uri.parse(listViewsChat.get(position).getMessage())).into(viewHolder4.receiverPic);

            viewHolder4.receiverPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShowImageActivity.class);
                    intent.putExtra("images", listViewsChat.get(position).getMessage());
                    intent.putExtra("date", dateFormat.format(date));
                    intent.putExtra("name", listViewsChat.get(position).getUserChatName());
                    intent.putExtra("imgname", listViewsChat.get(position).getRoot());
                    intent.putExtra("place", "1");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return listViewsChat.size();
    }

    class ViewHolder1 extends RecyclerView.ViewHolder{

        TextView senderMessage, time_tv;
        LinearLayout linearLayout;
        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.usermessagechat);
            linearLayout = itemView.findViewById(R.id.linearLayout_sender);
            time_tv = itemView.findViewById(R.id.time_tv);
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder{

        TextView receiverName;
        ImageView verify;
        TextView receiverMessage, time_tv;
        CircleImageView profile_pic;
        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            receiverName = itemView.findViewById(R.id.receiverchatname);
            receiverMessage = itemView.findViewById(R.id.receivermessagechat);
            profile_pic = itemView.findViewById(R.id.img_receiver);
            time_tv = itemView.findViewById(R.id.time_tv);
            verify = itemView.findViewById(R.id.verify_img);
        }
    }
    
    class ViewHolder3 extends RecyclerView.ViewHolder{
        
        ImageView senderPic;
        TextView time_tv;
        public ViewHolder3(@NonNull View itemView){
            super(itemView);
            senderPic = itemView.findViewById(R.id.usermessagechat_img);
            time_tv = itemView.findViewById(R.id.time_tv);
        }
    }

    class ViewHolder4 extends RecyclerView.ViewHolder{

        ImageView receiverPic, verify;
        TextView receiverUsernamePic, time_tv;
        CircleImageView profile_pic;

        public ViewHolder4(@NonNull View itemView){
            super(itemView);
            receiverUsernamePic = itemView.findViewById(R.id.receiverchatname_img);
            receiverPic = itemView.findViewById(R.id.receivermessagechat_img);
            profile_pic = itemView.findViewById(R.id.img_receiver);
            time_tv = itemView.findViewById(R.id.time_tv);
            verify = itemView.findViewById(R.id.verify_img);
        }
    }

    public void deleteData(){

    }
}