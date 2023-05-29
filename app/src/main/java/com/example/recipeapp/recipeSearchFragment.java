package com.example.recipeapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.api.FilterOptions;
import com.example.recipeapp.api.RecipeInfo;
import com.example.recipeapp.databinding.FragmentRecipeSearchBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class recipeSearchFragment extends Fragment {
    private FragmentRecipeSearchBinding binding;
    private ApiService apiService;
    private List<RecipeInfo> recipes;
    private FilterOptions filterOptions;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeSearchBinding.inflate(inflater, container, false);
        apiService = ApiClient.getClient(getContext());
        filterOptions = new FilterOptions(0, new ArrayList<>(), -1, -1);
        Call<List<RecipeInfo>> allRecipes = apiService.getAllRecipes();
        allRecipes.enqueue(new Callback<List<RecipeInfo>>() {
            @Override
            public void onResponse(Call<List<RecipeInfo>> call, Response<List<RecipeInfo>> response) {
                if(response.isSuccessful()){
                    recipes = response.body();
                    binding.loading.setVisibility(View.GONE);
                    binding.mainContent.setVisibility(View.VISIBLE);
                    displayRecipes(recipes, 15);
                }
            }

            @Override
            public void onFailure(Call<List<RecipeInfo>> call, Throwable t) {
                Log.e("api", "get all recipes onFailure: Failed, status code: " + t.getMessage(), t);
            }
        });
        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        binding.FilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterDialogFragment filterDialogFragment = new FilterDialogFragment(filterOptions);
                filterDialogFragment.show(getChildFragmentManager(), "Filter");
            }
        });
    }
    private void displayRecipes(List<RecipeInfo> recipes, int limit){
        binding.mainContent.removeAllViews();
        for(RecipeInfo recipe: recipes){
            if(limit == 0)
                break;
            binding.mainContent.addView(new RecipeBoxLayout(getContext(), recipe));
            limit--;
        }
    }
}