package com.example.recipeapp.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ReviewInfo {
    public int user_id;
    public String username;
    public int recipe_id;
    public String comment;
    public int stars;
    public String profilepic;

    public Bitmap getProfilePic(){
        byte[] decodedString = Base64.decode(this.profilepic, Base64.DEFAULT);
        return (BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
    }

}
