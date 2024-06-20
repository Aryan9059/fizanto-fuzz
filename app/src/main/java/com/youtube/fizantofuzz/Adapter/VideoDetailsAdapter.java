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
import com.google.android.material.card.MaterialCardView;
import com.youtube.fizantofuzz.YouTube.Item;
import com.youtube.fizantofuzz.Activity.VideoActivity;
import com.youtube.fizantofuzz.YouTube.VideoStats.VideoStats;
import com.youtube.fizantofuzz.R;
import com.youtube.fizantofuzz.Retrofit.GetDataService;
import com.youtube.fizantofuzz.Retrofit.RetrofitInstance;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoDetailsAdapter extends RecyclerView.Adapter<VideoDetailsAdapter.VideoDetailsViewHolder> {
    private Context context;
    private List<Item> videoDetailsList;
    private String converted_date, day_str, date_str;

    public VideoDetailsAdapter(Context context, List<Item> videoDetailsList) {
        this.context = context;
        this.videoDetailsList = videoDetailsList;
    }

    @NonNull
    @Override
    public VideoDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
         View view = LayoutInflater.from(context).inflate(R.layout.item_youtube_video, parent, false);
         return new VideoDetailsViewHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder (@NonNull VideoDetailsViewHolder holder, @SuppressLint("RecyclerView") final int position){
        GetDataService dataService = RetrofitInstance.getRetrofit().create(GetDataService.class);
        Call<VideoStats> videoStatsRequest = dataService.getVideoStats("statistics", "AIzaSyAzXyjw8hhgvecFzTfGosS72Bkor457wAs", videoDetailsList.get(position).getId().getVideoId());
        videoStatsRequest.enqueue(new Callback<VideoStats>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<VideoStats> call, Response<VideoStats> response) {
                if(response.isSuccessful()){
                    if(response.body()!=null){

                        try {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                            SimpleDateFormat format = null;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                            }
                            Date date = dateFormat.parse(videoDetailsList.get(position).getSnippet().getPublishedAt());
                            converted_date = format.format(date);
                            String new_date = converted_date.substring(0, 10);
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy'T'HH:mm:ss");
                                new_date = new_date.replace("/", "-") + "T00:00:00";
                                LocalDateTime dateTime = LocalDateTime.parse(new_date, formatter);
                                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                                DateTimeFormatter formatter_date = DateTimeFormatter.ofPattern("MMM d");
                                date_str = dateTime.format(formatter_date);
                                DateTimeFormatter formatter_day = DateTimeFormatter.ofPattern("yyyy");
                                day_str = dateTime.format(formatter_day);
                                holder.description.setText(response.body().getItems().get(0).getStatistics().getViewCount() + " Views" + " · " + response.body().getItems().get(0).getStatistics().getLikeCount() + " Likes" + " · " + dateTime.format(formatter1));
                            } else {
                                date_str = converted_date;
                                day_str = converted_date;
                                holder.description.setText(response.body().getItems().get(0).getStatistics().getViewCount() + " Views" + " · " + response.body().getItems().get(0).getStatistics().getLikeCount() + " Likes" + " · " + new_date);
                            }

                        } catch (ParseException e) {
                            Log.e("DATE", e.getMessage());
                        }
                    } else{
                        Log.e(">>>> RESPONSE BODY NULL", "NULL");
                    }
                } else{
                    Log.e("RESPONSE UNSUCCESSFUL", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<VideoStats> call, Throwable t) {     }
        });

        String title_str = videoDetailsList.get(position).getSnippet().getTitle();
        if (title_str.contains("n&#39;")){
            title_str = title_str.replace("n&#39;", "&");
        }
        holder.title.setText(title_str);
        Glide.with(context).load(videoDetailsList
                .get(position)
                .getSnippet()
                .getThumbnails()
                .getHigh()
                .getUrl()).into(holder.thumbnail);

        String finalTitle_str = title_str;
        holder.materialCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, VideoActivity.class);
                intent.putExtra("videoID", videoDetailsList.get(position).getId().getVideoId());
                intent.putExtra("video_title", finalTitle_str);
                intent.putExtra("video_des", videoDetailsList.get(position).getSnippet().getDescription());
                intent.putExtra("video_date", date_str);
                intent.putExtra("video_day", day_str);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount(){
        return videoDetailsList.size();
    }

    public class VideoDetailsViewHolder extends RecyclerView.ViewHolder{

        private TextView title, description;
        private ImageView thumbnail;
        private MaterialCardView materialCardView;

        public VideoDetailsViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            materialCardView = itemView.findViewById(R.id.card_click_feed);
            description = itemView.findViewById(R.id.description);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }
}
