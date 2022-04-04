package com.youtube.fizantofuzz;

public class ListItemChat {
    private final String message;
    private final String userchatname;
    private final String objectId;
    private final String type;
    private final String root;
    private final String time;

    public ListItemChat(String message, String userchatname, String objectId, String type, String root, String time) {
        this.message = message;
        this. objectId = objectId;
        this.userchatname = userchatname;
        this.type = type;
        this.root = root;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public String getUserchatname() {
        return userchatname;
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
