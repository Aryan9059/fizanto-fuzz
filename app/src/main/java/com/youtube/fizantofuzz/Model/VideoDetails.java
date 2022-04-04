package com.youtube.fizantofuzz.Model;

import com.google.gson.annotations.*;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoDetails {

    @SerializedName("items")
    @Expose
    private List<Item> items = null;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

}