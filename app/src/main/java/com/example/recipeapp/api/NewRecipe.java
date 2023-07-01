package com.example.recipeapp.api;

import java.util.ArrayList;
import java.util.List;

public class NewRecipe {
    String recipe_name;
    int prep_time;
    int cook_time;
    int servings;
    int price;
    boolean isPrivate;
    List<RecipeIngredientV2> ingredients = new ArrayList<>();
    List<RecipeStepV2> steps;
    public NewRecipe(){

    }
    public void setInfo(String recipe_name, int prep_time, int cook_time, int servings,
                     int price, boolean isPrivate) {
        this.recipe_name = recipe_name;
        this.prep_time = prep_time;
        this.cook_time = cook_time;
        this.servings = servings;
        this.price = price;
        this.isPrivate = isPrivate;
    }
    public void setRecipeIngredients(ArrayList<RecipeIngredientV2> ingredientList){
        this.ingredients = ingredientList;
    }
    public void setRecipeSteps(ArrayList<RecipeStepV2> stepList){
        this.steps = stepList;
    }
    @Override
    public String toString() {
        return "NewRecipe{" +
                "recipe_name='" + recipe_name + '\'' +
                ", prep_time=" + prep_time +
                ", cook_time=" + cook_time +
                ", servings=" + servings +
                ", price=" + price +
                ", isPrivate=" + isPrivate +
                ", ingredients=" + ingredients.toString() +
                ", steps=" + steps +
                '}';
    }
}
