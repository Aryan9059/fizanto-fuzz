package com.youtube.fizantofuzz.Lists;

public class Member {
    String title, url, pic, date;

    public Member(String title, String url, String pic, String date){
        this.title = title;
        this.url = url;
        this.pic = pic;
        this.date = date;
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

    public Member(){

    }
}
