package com.youtube.fizantofuzz.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_text_layout, parent, false);
        return new FeedTextAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedTextAdapter.ViewHolder holder, int position) {
        final FeedTextList listItem = listViews.get(position);
        holder.title.setText(listItem.getTitle());
        holder.body.setText(Html.fromHtml(listItem.getText()));
        holder.date.setText(listItem.getDate());

        if (listItem.getImage().equals("no")){
            Log.e("IMAGES", "No Image Present");
        } else {
            Glide.with(context).load(listItem.getImage()).into(holder.image);
            holder.image.setVisibility(View.VISIBLE);
            holder.cardview.setVisibility(View.VISIBLE);
        }

        holder.feedlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TextFeedActivity.class);
                intent.putExtra("body", listItem.getText());
                intent.putExtra("date", listItem.getDate());
                intent.putExtra("image", listItem.getImage());
                intent.putExtra("title", listItem.getTitle());
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
        LinearLayout feedlayout;
        CardView cardview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardview = itemView.findViewById(R.id.cardimagefeed);
            feedlayout = itemView.findViewById(R.id.linearfeeds);
            title = itemView.findViewById(R.id.feedtitle);
            body = itemView.findViewById(R.id.feedbody);
            date = itemView.findViewById(R.id.feeddate);
            image = itemView.findViewById(R.id.feedimage);
        }
    }
}
