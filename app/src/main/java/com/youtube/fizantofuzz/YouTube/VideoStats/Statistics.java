package com.youtube.fizantofuzz.YouTube.VideoStats;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Statistics {
    @SerializedName("viewCount")
    @Expose
    private String viewCount;

    @SerializedName("likeCount")
    @Expose
    private String likeCount;

    @SerializedName("dislikeCount")
    @Expose
    private String dislikeCount;

    public String getViewCount(){
        return viewCount;
    }
    public String getLikeCount(){
        return likeCount;
    }
    public String getDislikeCount(){
        return dislikeCount;
    }
    public void setViewCount(String viewCount){
        this.viewCount = viewCount;
    }
    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }
    public void setDislikeCount(String dislikeCount) {
        this.dislikeCount = dislikeCount;
    }
}
