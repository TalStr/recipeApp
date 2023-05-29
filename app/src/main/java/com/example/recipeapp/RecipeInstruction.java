package com.example.recipeapp;

public class RecipeInstruction {
    int recipeID;
    int step;
    String desc;

    public RecipeInstruction(int recipeID, int step, String desc) {
        this.recipeID = recipeID;
        this.step = step;
        this.desc = desc;
    }
}
