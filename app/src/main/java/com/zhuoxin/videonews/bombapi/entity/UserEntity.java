package com.zhuoxin.videonews.bombapi.entity;

/**
 * 用户实体类,(注册时的请求体)
 * Created by Administrator on 2016/12/22.
 */

public class UserEntity {
    private String username;
    private String password;

    public UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
