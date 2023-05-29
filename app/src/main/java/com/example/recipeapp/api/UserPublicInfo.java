package com.example.recipeapp.api;

public class UserPublicInfo {
    public int user_id;
    public String username;
    public String profilepic;
    public boolean following;

    @Override
    public String toString() {
        return "UserPublicInfo{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", profilepic='" + profilepic + '\'' +
                ", following=" + following +
                '}';
    }
}
