package com.youtube.fizantofuzz.Lists;

public class GroupChatList {
    private final String message;
    private final String userChatName;
    private final String objectId;
    private final String type;
    private final String root;
    private final String time;

    public GroupChatList(String message, String userChatName, String objectId, String type, String root, String time) {
        this.message = message;
        this. objectId = objectId;
        this.userChatName = userChatName;
        this.type = type;
        this.root = root;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public String getUserChatName() {
        return userChatName;
    }

    public String getObjectId() {
        return objectId;
    }

    public String getType(){
        return type;
    }

    public String getRoot() {
        return root;
    }

    public String getTime() {
        return time;
    }
}
