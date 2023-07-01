package com.example.recipeapp.customViews;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.recipeapp.CurrentUser;
import com.example.recipeapp.R;
import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.api.NewRecipe;
import com.example.recipeapp.api.Recipe;
import com.example.recipeapp.api.RecipeIngredientV2;
import com.example.recipeapp.api.RecipeStepV2;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddRecipeDialogFragment extends DialogFragment {
    private NewRecipe recipeData = new NewRecipe();
    private int userID;
    private ApiService apiService;

    public interface RecipeDataListener {
        void onRecipeDataChanged(NewRecipe recipeData);
    }
    public void updateRecipeData(NewRecipe recipeData) {
        recipeData = recipeData;
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
    public void showFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.dialog_content, fragment)
                .commit();
    }
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        userID = CurrentUser.getInstance().getUserID();
        apiService = ApiClient.getClient(getContext());
        return inflater.inflate(R.layout.fragment_add_recipe_dialog, container, false);
    }
    // In your onCreateView or onViewCreated method, you can start by showing the first Fragment
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        showFragment(new AddRecipeOneFragment());
    }
    public void setRecipeInfo(String recipe_name, int prep_time, int cook_time, int servings,
                              int price, boolean isPrivate){
        recipeData.setInfo(recipe_name,prep_time,cook_time,servings,price,isPrivate);
    }
    public void setRecipeIngredients(ArrayList<RecipeIngredientV2> ingredientList){
        recipeData.setRecipeIngredients(ingredientList);
    }
    public void setRecipeSteps(ArrayList<RecipeStepV2> stepList){
        recipeData.setRecipeSteps(stepList);
    }
    public void recipeString(){
        Log.d("new recipe", recipeData.toString());
    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Call<Void> createRecipe = apiService.addFullRecipe(userID, recipeData);
        createRecipe.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}
