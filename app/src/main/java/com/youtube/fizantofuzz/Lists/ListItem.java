package com.youtube.fizantofuzz.Lists;

public class ListItem {
    private String appname;
    private String comment;
    private String userId;
    private String root;
    private String imgurl;

    public ListItem(String appname, String comment, String userId, String root, String imgurl) {
        this.appname = appname;
        this.comment = comment;
        this.userId = userId;
        this.root = root;
        this.imgurl = imgurl;
    }

    public String getAppName() {
        return appname;
    }

    public String getComment() {
        return comment;
    }

    public String getUserId() {
        return userId;
    }

    public String getRoot() {
        return root;
    }

    public String getImgurl() {
        return imgurl;
    }
}
