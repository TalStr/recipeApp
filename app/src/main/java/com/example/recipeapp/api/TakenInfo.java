package com.example.recipeapp.api;

import java.util.List;

public class TakenInfo {
    public List<String> usernames;
    public List<String> emails;
    public TakenInfo(List<String> usernames, List<String> emails) {
        this.usernames = usernames;
        this.emails = emails;
    }

    @Override
    public String toString() {
        return "TakenInfo{" +
                "usernames=" + usernames +
                ", emails=" + emails +
                '}';
    }

}
