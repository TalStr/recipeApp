package com.example.recipeapp.api;

public class RecipeInfo {
    public int recipe_id;
    public int user_id;
    public String recipe_name;
    public int preptime;
    public int cooktime;
    public int servings;
    public String image;
    public int price;
    public int privacy_level;
    public String creation_datetime;
    public String author_name;
    public float avg_rating;
    public int reviewCount;

    public RecipeInfo(int recipe_id, int user_id, String recipe_name, int preptime, int cooktime, int servings,
                      String image, int price, int privacy_level, String creation_datetime, String author_name,
                      float avg_rating, int reviewCount) {
        this.recipe_id = recipe_id;
        this.user_id = user_id;
        this.recipe_name = recipe_name;
        this.preptime = preptime;
        this.cooktime = cooktime;
        this.servings = servings;
        this.image = image;
        this.price = price;
        this.privacy_level = privacy_level;
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
                ", image='" + image + '\'' +
                ", price=" + price +
                ", privacy_level=" + privacy_level +
                ", creation_datetime='" + creation_datetime + '\'' +
                ", author_name='" + author_name + '\'' +
                ", avg_rating=" + avg_rating +
                ", reviewCount=" + reviewCount +
                '}';
    }
}
