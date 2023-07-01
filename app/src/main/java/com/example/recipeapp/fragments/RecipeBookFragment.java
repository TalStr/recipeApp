package com.example.recipeapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipeapp.CurrentUser;
import com.example.recipeapp.R;
import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.api.RecipeInfo;
import com.example.recipeapp.customViews.AddRecipeDialogFragment;
import com.example.recipeapp.customViews.LoadingDialog;
import com.example.recipeapp.customViews.RecipeBoxLayout;
import com.example.recipeapp.databinding.FragmentRecipeBookBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeBookFragment extends Fragment {
    private FragmentRecipeBookBinding binding;
    private int userID;
    private int bookOwnerID;
    private String username;
    private ApiService apiService;
    LoadingDialog loadingDialog;
    List<RecipeInfo> recipes;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeBookBinding.inflate(inflater, container, false);
        apiService = ApiClient.getClient(getContext());
        loadingDialog = new LoadingDialog(getActivity());
        userID = CurrentUser.getInstance().getUserID();
        Bundle bundle = getArguments();
        if (bundle != null) {
            bookOwnerID = bundle.getInt("ownerID");
            username = bundle.getString("username");
        }

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Call<List<RecipeInfo>> call;
        if(userID == bookOwnerID){
            binding.addRecipe.setVisibility(View.VISIBLE);
            call = apiService.getUserRecipes(bookOwnerID);
        }
        else
            call = apiService.getUserPublicRecipes(bookOwnerID);
        call.enqueue(new Callback<List<RecipeInfo>>() {
            @Override
            public void onResponse(Call<List<RecipeInfo>> call, Response<List<RecipeInfo>> response) {
                if(response.isSuccessful()){
                    recipes = response.body();
                    displayRecipes(recipes, 15);
                }
                else{
                    Log.d("api", "Error response from server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<RecipeInfo>> call, Throwable t) {
                Log.e("api", "API call failed: " + t.getMessage());
            }
        });
        binding.addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddRecipeDialogFragment dialogFragment = new AddRecipeDialogFragment();
                dialogFragment.show(getParentFragmentManager(), "addRecipeDialog");
            }
        });

        binding.title.setText(username + "s\nRecipe Book");
        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                List<RecipeInfo> searchResults = nameSearch(s.toString());
                displayRecipes(searchResults, 15);
            }
        });
    }
    private List<RecipeInfo> nameSearch(String searchString){
        List<RecipeInfo> matchingRecipes = new ArrayList<>();
        for (RecipeInfo recipe : recipes) {
            if (recipe.recipe_name.toLowerCase().contains(searchString.toLowerCase())) {
                matchingRecipes.add(recipe);
            }
        }
        return matchingRecipes;
    }
    private void displayRecipes(List<RecipeInfo> recipes, int limit){
        binding.recipesContainer.removeAllViews();
        if(recipes.size() == 0){
            binding.recipesContainer.setVisibility(View.GONE);
            binding.noRecipesText.setVisibility(View.VISIBLE);
        }
        else{
            binding.noRecipesText.setVisibility(View.GONE);
            binding.recipesContainer.setVisibility(View.VISIBLE);
            for(RecipeInfo recipe: recipes){
                if(limit == 0)
                    break;
                binding.recipesContainer.addView(new RecipeBoxLayout(requireContext(), R.id.recipeBookFragment, loadingDialog, recipe));
                limit--;
            }
        }
    }
}