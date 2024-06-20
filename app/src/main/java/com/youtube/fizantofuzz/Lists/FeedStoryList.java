package com.youtube.fizantofuzz.Lists;

public class FeedStoryList {
    String title, url, pic, date, parent;

    public FeedStoryList(String title, String url, String pic, String date,  String parent){
        this.title = title;
        this.url = url;
        this.pic = pic;
        this.date = date;
        this.parent = parent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public FeedStoryList(){

    }
}
