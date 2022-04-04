package com.youtube.fizantofuzz.Retrofit;

import com.youtube.fizantofuzz.Model.Comments.Comment;
import com.youtube.fizantofuzz.Model.VideoDetails;
import com.youtube.fizantofuzz.Model.VideoStats.VideoStats;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDataService {
    @GET("search")
    Call<VideoDetails> getVideoData(
          @Query("part") String part,
          @Query("channelId") String channelId,
          @Query("maxResults") int maxResults,
          @Query("type") String type,
          @Query("key") String key,
          @Query("order") String order
    );

    @GET("videos")
    Call<VideoStats> getVideoStats(
            @Query("part") String part,
            @Query("key") String key,
            @Query("id") String id
    );

    @GET("commentThreads")
    Call<Comment.Model> getCommentsData(
            @Query("part") String part,
            @Query("videoId") String videoId,
            @Query("maxResults") String maxResults,
            @Query("key") String key
    );
}
