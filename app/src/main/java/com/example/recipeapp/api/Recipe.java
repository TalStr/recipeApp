package com.example.recipeapp.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Recipe {
    @SerializedName("info")
    RecipeInfo info;
    @SerializedName("ingredients")
    List<RecipeIngredient> ingredients;
    @SerializedName("instructions")
    List<Instruction> instructions;
    @SerializedName("reviews")
    List<ReviewInfo> reviews;
}
