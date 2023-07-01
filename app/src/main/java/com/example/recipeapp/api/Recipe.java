package com.example.recipeapp.api;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Recipe implements Serializable {

    @SerializedName("info")
    RecipeInfo info;
    @SerializedName("ingredients")
    List<RecipeIngredient> ingredients;
    @SerializedName("instructions")
    List<Instruction> instructions;
    @SerializedName("reviews")
    List<ReviewInfo> reviews;
    public RecipeInfo getInfo() {
        return info;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public List<ReviewInfo> getReviews() {
        return reviews;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "info=" + info +
                ", ingredients=" + ingredients +
                ", instructions=" + instructions +
                ", reviews=" + reviews +
                '}';
    }
}
