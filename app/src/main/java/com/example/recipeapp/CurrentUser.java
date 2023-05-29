package com.example.recipeapp;

public class CurrentUser {
    private static CurrentUser instance = null;

    // User information
    private String username;
    private int userID;

    private CurrentUser() {
        // Exists only to defeat instantiation.
    }

    public static CurrentUser getInstance() {
        if(instance == null) {
            instance = new CurrentUser();
        }
        return instance;
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
}
