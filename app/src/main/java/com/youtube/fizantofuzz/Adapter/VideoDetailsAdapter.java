package com.youtube.fizantofuzz.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.youtube.fizantofuzz.Model.Item;
import com.youtube.fizantofuzz.Activity.VideoActivity;
import com.youtube.fizantofuzz.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VideoDetailsAdapter extends RecyclerView.Adapter<VideoDetailsAdapter.VideoDetailsViewHolder> {
    private Context context;
    private List<Item> videoDetailsList;
    private String converted_date;

    public VideoDetailsAdapter(Context context, List<Item> videoDetailsList) {
        this.context = context;
        this.videoDetailsList = videoDetailsList;
    }

    @NonNull
    @Override
    public VideoDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
         View view = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);
         return new VideoDetailsViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder (@NonNull VideoDetailsViewHolder holder, @SuppressLint("RecyclerView") final int position){
        try {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            SimpleDateFormat format = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                format = new SimpleDateFormat("YYYY/MM/dd hh:mm:a");
            }
            Date date = dateFormat.parse(videoDetailsList.get(position).getSnippet().getPublishedAt());
        converted_date = format.format(date);
        holder.publishedAt.setText(converted_date);
        } catch (ParseException e) {
            Log.e("DATEE", e.getMessage());
        }
        holder.description.setText(videoDetailsList.get(position).getSnippet().getDescription());
        holder.title.setText(videoDetailsList.get(position).getSnippet().getTitle());
        Glide.with(context).load(videoDetailsList
                .get(position)
                .getSnippet()
                .getThumbnails()
                .getHigh()
                .getUrl()).into(holder.thumbnail);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, VideoActivity.class);
                intent.putExtra("videoID", videoDetailsList.get(position).getId().getVideoId());
                intent.putExtra("video_title", videoDetailsList.get(position).getSnippet().getTitle());
                intent.putExtra("video_des", videoDetailsList.get(position).getSnippet().getDescription());
                intent.putExtra("video_date", converted_date);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount(){
        return videoDetailsList.size();
    }

    public class VideoDetailsViewHolder extends RecyclerView.ViewHolder{

        private TextView publishedAt, title, description;
        private ImageView thumbnail;

        public VideoDetailsViewHolder(View itemView) {
            super(itemView);

            publishedAt = itemView.findViewById(R.id.publishedAt);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }
}
