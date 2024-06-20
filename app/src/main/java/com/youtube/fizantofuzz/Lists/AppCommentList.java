package com.youtube.fizantofuzz.Lists;

public class AppCommentList {
    private String comment;
    private String userId;
    private String root;

    public AppCommentList(String comment, String userId, String root) {
        this.comment = comment;
        this.userId = userId;
        this.root = root;
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

}
