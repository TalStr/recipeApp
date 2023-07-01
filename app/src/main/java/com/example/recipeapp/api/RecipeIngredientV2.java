package com.example.recipeapp.api;

public class RecipeIngredientV2 {
    private String name;
    private String details;

    public RecipeIngredientV2(String name, String details) {
        this.name = name;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "RecipeIngredientV2{" +
                "name='" + name + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
