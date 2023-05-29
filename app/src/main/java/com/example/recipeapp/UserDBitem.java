package com.example.recipeapp;

public class UserDBitem {
    int user_id;
    String username, email, password, profilepic;
    public UserDBitem(){
        this.user_id =-1;
    }
    public UserDBitem(int userID, String username, String profilePic){
        this.user_id = userID;
        this.username = username;
        this.profilepic = profilePic;
    }

    public UserDBitem(int userID, String username, String email, String password, String profilePic) {
        this.user_id = userID;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilepic = profilePic;
    }
    public void copy(UserDBitem origin){
        this.user_id = origin.user_id;
        this.username = origin.username;
        this.email = origin.email;
        this.password = origin.password;
        this.profilepic = origin.profilepic;
    }
}
