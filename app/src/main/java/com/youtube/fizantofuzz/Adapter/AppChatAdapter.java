package com.youtube.fizantofuzz.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.youtube.fizantofuzz.ListItemChat;
import com.youtube.fizantofuzz.R;
import com.zolad.zoominimageview.ZoomInImageViewAttacher;

import java.util.List;

public class AppChatAdapter extends RecyclerView.Adapter{

    List<ListItemChat> listViewsChat;
    Context context;
    private String verify;
    private DatabaseReference firebaseDatabase;
    String userdet = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public AppChatAdapter(List<ListItemChat> listViewsChat, Context context) {
        this.listViewsChat = listViewsChat;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(listViewsChat.get(position).getObjectId().equals(userdet) && listViewsChat.get(position).getType().equals("1")){
            return 0;
        } else if(listViewsChat.get(position).getObjectId().equals(userdet) && listViewsChat.get(position).getType().equals("2")){
            return 1; 
        } else if(!listViewsChat.get(position).getObjectId().equals(userdet) && listViewsChat.get(position).getType().equals("1")){
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
            view = layoutInflater.inflate(R.layout.sender_chat, parent, false);
            return new ViewHolder1(view);
        } else if(viewType == 2){
        view = layoutInflater.inflate(R.layout.reciever_chat, parent, false);
        return new ViewHolder2(view);
        } else if(viewType == 1){
            view = layoutInflater.inflate(R.layout.sender_image, parent, false);
            return new ViewHolder3(view);
        }
        view = layoutInflater.inflate(R.layout.receiver_image, parent, false);
        return new ViewHolder4(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if(listViewsChat.get(position).getObjectId().equals(userdet) && listViewsChat.get(position).getType().equals("1")){
            ViewHolder1 viewHolder1 = (ViewHolder1) holder;
            viewHolder1.sendermessage.setText(listViewsChat.get(position).getMessage());
            viewHolder1.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Are you sure want to delete this message from Chats?");
                    builder.setCancelable(true);
                    builder.setTitle("Delete?");
                    builder.setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference("ChatsG").child(listViewsChat.get(position).getRoot()).removeValue();
                            dialog.dismiss();
                            Toast.makeText(context, "Message deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                    return false;
                }
            });
        }

        else if(!listViewsChat.get(position).getObjectId().equals(userdet) && listViewsChat.get(position).getType().equals("1")){
            final ViewHolder2 viewHolder2 = (ViewHolder2) holder;
            viewHolder2.receiverusername.setText(listViewsChat.get(position).getUserchatname());
            viewHolder2.receivermessage.setText(listViewsChat.get(position).getMessage());
            firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(listViewsChat.get(position).getObjectId());
        }

        else if(listViewsChat.get(position).getObjectId().equals(userdet) && listViewsChat.get(position).getType().equals("2")){
            final ViewHolder3 viewHolder3 = (ViewHolder3) holder;
            StorageReference mImageRef = FirebaseStorage.getInstance().getReference(listViewsChat.get(position).getMessage());
            final long ONE_MEGABYTE = 1024 * 1024;
            mImageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    final Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    viewHolder3.lottie_sender.setVisibility(View.GONE);
                    viewHolder3.sendermessagepic.setVisibility(View.VISIBLE);
                    viewHolder3.sendermessagepic.setImageBitmap(bm);
                    ZoomInImageViewAttacher zoom = new ZoomInImageViewAttacher();
                    zoom.attachImageView(viewHolder3.sendermessagepic);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.i("TAG", exception.getMessage());
                }
            });

            viewHolder3.sendermessagepic.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage("Are you sure want to delete this message from Chats?");
                    builder.setCancelable(true);
                    builder.setTitle("Delete?");
                    builder.setPositiveButton("Delete", new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference("ChatsG").child(listViewsChat.get(position).getRoot()).removeValue();
                            dialog.dismiss();
                            Toast.makeText(context, "Message deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                    return false;
                }
            });
        }

        else {
            final ViewHolder4 viewHolder4 = (ViewHolder4) holder;
            viewHolder4.receiverusernamepic.setText(listViewsChat.get(position).getUserchatname());

            firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users").child(listViewsChat.get(position).getObjectId());

            StorageReference mImageRef = FirebaseStorage.getInstance().getReference(listViewsChat.get(position).getMessage());
            final long ONE_MEGABYTE1 = 1024 * 1024;
            mImageRef.getBytes(ONE_MEGABYTE1).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    final Bitmap bm1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    viewHolder4.lottierecieverpic.setVisibility(View.GONE);
                    viewHolder4.receivermessagepic.setVisibility(View.VISIBLE);
                    viewHolder4.receivermessagepic.setImageBitmap(bm1);
                    ZoomInImageViewAttacher zoom = new ZoomInImageViewAttacher();
                    zoom.attachImageView(viewHolder4.receivermessagepic);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.i("TAG", exception.getMessage());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listViewsChat.size();
    }

    class ViewHolder1 extends RecyclerView.ViewHolder{

        TextView sendermessage;
        LinearLayout linearLayout;
        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            sendermessage = itemView.findViewById(R.id.usermessagechat);
            linearLayout = itemView.findViewById(R.id.linearLayout_sender);
        }
    }

    class ViewHolder2 extends RecyclerView.ViewHolder{

        TextView receiverusername;
        TextView receivermessage;
        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            receiverusername = itemView.findViewById(R.id.receiverchatname);
            receivermessage = itemView.findViewById(R.id.receivermessagechat);
        }
    }
    
    class ViewHolder3 extends RecyclerView.ViewHolder{
        
        ImageView sendermessagepic;
        ProgressBar lottie_sender;
        public ViewHolder3(@NonNull View itemView){
            super(itemView);
            lottie_sender = itemView.findViewById(R.id.lottie_sender);
            sendermessagepic = itemView.findViewById(R.id.usermessagechat_img);
        }
    }

    class ViewHolder4 extends RecyclerView.ViewHolder{

        ImageView receivermessagepic;
        TextView receiverusernamepic;
        ProgressBar lottierecieverpic;

        public ViewHolder4(@NonNull View itemView){
            super(itemView);
            lottierecieverpic = itemView.findViewById(R.id.lottie_recieverimg);
            receiverusernamepic = itemView.findViewById(R.id.receiverchatname_img);
            receivermessagepic = itemView.findViewById(R.id.receivermessagechat_img);
        }
    }

    public void deleteData(){

    }
}