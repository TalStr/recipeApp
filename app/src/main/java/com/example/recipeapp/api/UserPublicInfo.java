package com.example.recipeapp.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class UserPublicInfo {
    public int user_id;
    public String username;
    public boolean following;
    public String profilepic;

    @Override
    public String toString() {
        return "UserPublicInfo{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", profilepic='" + profilepic + '\'' +
                ", following=" + following +
                '}';
    }
    public Bitmap getProfilePic(){
        byte[] decodedString = Base64.decode(this.profilepic, Base64.DEFAULT);
        return (BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
    }
}
