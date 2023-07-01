package com.example.recipeapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class CurrentUser {
    private static CurrentUser instance = null;

    // User information
    private String username;
    private int userID;
    private Bitmap profilePic;

    private CurrentUser() {
    }

    public static CurrentUser getInstance() {
        if(instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }
    public void setInfo(int userID, String username, Bitmap profilePic){
        this.userID = userID;
        this.username = username;
        this.profilePic = profilePic;
    }
    public void setInfo(int userID, String username, String profilePic){
        this.userID = userID;
        this.username = username;
        byte[] decodedString = Base64.decode(profilePic, Base64.DEFAULT);
        this.profilePic = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
    public void setProfilePic(Bitmap bitmap){
        this.profilePic = bitmap;
    }
    public void setProfilePic(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        this.profilePic = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
    public Bitmap getProfilePic(){
        return this.profilePic;
    }
}
