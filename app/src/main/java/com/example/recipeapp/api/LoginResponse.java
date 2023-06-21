package com.example.recipeapp.api;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("username")
    private String username;

    @SerializedName("profilePic")
    private String profilePic;

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePic() {
        return profilePic;
    }
}
