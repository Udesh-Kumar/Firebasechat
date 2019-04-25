package com.example.cc.firebasechat;

import de.hdodenhof.circleimageview.CircleImageView;

public class Users
{
    String name;
     String email;
     String username;
     String password;
     String userId;
     String gender;



    public Users(String name, String email, String username, String password, String userId, String strGender, CircleImageView profile_image) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userId = userId;
        this.gender = strGender;


    }

    public Users() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }




    }


