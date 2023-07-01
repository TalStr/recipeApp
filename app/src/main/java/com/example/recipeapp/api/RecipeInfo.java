package com.example.recipeapp.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RecipeInfo implements Serializable {
    @SerializedName("recipe_id")
    public int recipe_id;
    @SerializedName("user_id")
    public int user_id;
    @SerializedName("recipe_name")
    public String recipe_name;
    @SerializedName("preptime")
    public int preptime;
    @SerializedName("cooktime")
    public int cooktime;
    @SerializedName("servings")
    public int servings;
    @SerializedName("price")
    public int price;
    @SerializedName("private")
    public boolean isPrivate;
    @SerializedName("creation_datetime")
    public String creation_datetime;
    @SerializedName("author_name")
    public String author_name;
    @SerializedName("avg_rating")
    public float avg_rating;
    @SerializedName("reviewCount")
    public int reviewCount;

    public RecipeInfo(int recipe_id, int user_id, String recipe_name, int preptime, int cooktime, int servings,
                      int price, boolean isPrivate, String creation_datetime, String author_name,
                      float avg_rating, int reviewCount) {
        this.recipe_id = recipe_id;
        this.user_id = user_id;
        this.recipe_name = recipe_name;
        this.preptime = preptime;
        this.cooktime = cooktime;
        this.servings = servings;
        this.price = price;
        this.isPrivate = isPrivate;
        this.creation_datetime = creation_datetime;
        this.author_name = author_name;
        this.avg_rating = avg_rating;
        this.reviewCount = reviewCount;
    }
    @Override
    public String toString() {
        return "RecipeInfo{" +
                "recipe_id=" + recipe_id +
                ", user_id=" + user_id +
                ", recipe_name='" + recipe_name + '\'' +
                ", preptime=" + preptime +
                ", cooktime=" + cooktime +
                ", servings=" + servings +
                ", price=" + price +
                ", privacy_level=" + isPrivate +
                ", creation_datetime='" + creation_datetime + '\'' +
                ", author_name='" + author_name + '\'' +
                ", avg_rating=" + avg_rating +
                ", reviewCount=" + reviewCount +
                '}';
    }
}
