package com.youtube.fizantofuzz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ritesh.ratiolayout.RatioRelativeLayout;
import com.youtube.fizantofuzz.Lists.Member;
import com.youtube.fizantofuzz.Activity.StoryFeedActivity;
import com.youtube.fizantofuzz.R;
import java.util.List;

public class Stories_Adapter extends RecyclerView.Adapter<Stories_Adapter.ViewHolder0> {

    List<Member> members;
    Context context;

    public Stories_Adapter(List<Member> members, Context context) {
        this.members = members;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder0 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stories_feed, parent, false);
        return new Stories_Adapter.ViewHolder0(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder0 holder, int position) {
        final Member member = members.get(position);
        Glide.with(context).load(member.getPic()).into(holder.imageView);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StoryFeedActivity.class);
                intent.putExtra("title", member.getTitle());
                intent.putExtra("Urlofv", member.getUrl());
                intent.putExtra("date", member.getDate());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder0 extends RecyclerView.ViewHolder {

        private RatioRelativeLayout linearLayout;
        private ImageView imageView;

        public ViewHolder0(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.activity_main_ratio);
            imageView = itemView.findViewById(R.id.image_story);
        }
    }
}
