package com.example.dell.cemchat.Models;

import java.io.Serializable;

/**
 * Created by DELL on 5/2/2017.
 */

public class User implements Serializable {
    private String Name;
    private String userMail;
    private String userId;
    private String photo;
    private String phone;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User() {
    }


    public User(String Name, String phone, String photo, String userMail) {
        this.Name = Name;
        this.phone=phone;

        this.photo = photo;
        this.userMail=userMail;

    }
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {

        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}


