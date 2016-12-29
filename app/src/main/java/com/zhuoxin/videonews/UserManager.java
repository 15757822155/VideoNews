package com.zhuoxin.videonews;

/**
 * Created by Administrator on 2016/12/22.
 */

public class UserManager {


    private static UserManager sInstance;

    public static UserManager getInstance() {
        if (sInstance == null) {
            sInstance = new UserManager();
        }
        return sInstance;
    }

    private UserManager() {
    }

    private String username;
    private String objectId;
    private boolean isPlay;

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isOffline() {
        return username == null || objectId == null;
    }

    public void clear() {
        username = null;
        objectId = null;
    }
}
