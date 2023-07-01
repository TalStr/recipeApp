package com.example.recipeapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.recipeapp.CurrentUser;
import com.example.recipeapp.R;
import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.api.FilterOptions;
import com.example.recipeapp.api.RecipeInfo;
import com.example.recipeapp.api.UserPublicInfo;
import com.example.recipeapp.customViews.LoadingDialog;
import com.example.recipeapp.customViews.RecipeBoxLayout;
import com.example.recipeapp.customViews.SortOptionsBox;
import com.example.recipeapp.databinding.FragmentRecipeSearchBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeSearchFragment extends Fragment {
    private FragmentRecipeSearchBinding binding;
    private ApiService apiService;
    int userID;
    List<RecipeInfo> recipes;
    private FilterOptions filterOptions;
    LoadingDialog loadingDialog;
    List<String> ingredients;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeSearchBinding.inflate(inflater, container, false);
        loadingDialog = new LoadingDialog(getActivity());
        apiService = ApiClient.getClient(getContext());
        userID = CurrentUser.getInstance().getUserID();
        filterOptions = new FilterOptions(new ArrayList<>(), 0, Integer.MAX_VALUE, new ArrayList<>(), new ArrayList<>());
        Call<List<RecipeInfo>> allRecipes = apiService.getAllPublicRecipes(userID);
        allRecipes.enqueue(new Callback<List<RecipeInfo>>() {
            @Override
            public void onResponse(Call<List<RecipeInfo>> call, Response<List<RecipeInfo>> response) {
                if(response.isSuccessful()){
                    recipes = response.body();
                    binding.loading.setVisibility(View.GONE);
                    binding.mainContent.setVisibility(View.VISIBLE);
                    Collections.sort(recipes, (r1, r2) -> Float.compare(r2.avg_rating, r1.avg_rating));
                    displayRecipes(recipes, 15);
                }
            }

            @Override
            public void onFailure(Call<List<RecipeInfo>> call, Throwable t) {
                Log.e("api", "get all recipes onFailure: Failed, status code: " + t.getMessage(), t);
            }
        });
        Call<List<String>> allIngredientsCall = apiService.getAllIngredients();
        allIngredientsCall.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if(response.isSuccessful()){
                    ingredients = response.body();
                    Log.d("api", ingredients.toString());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {

            }
        });
        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

        binding.sortOptions.setSortListener(new SortOptionsBox.SortListener() {
            @Override
            public void onSortSelected(String by, boolean state) {
                int order = state ? 1 : -1;
                switch (by) {
                    case "Rating":
                        Collections.sort(recipes, (r1, r2) -> order * Float.compare(r2.avg_rating, r1.avg_rating));
                        break;
                    case "Price":
                        Collections.sort(recipes, (r1, r2) -> order * Integer.compare(r1.price, r2.price));
                        break;
                    case "Time":
                        Collections.sort(recipes, (r1, r2) -> order * Integer.compare(r1.preptime + r1.cooktime, r2.preptime + r2.cooktime));
                        break;
                    default:
                        break;
                }
                displayRecipes(recipes, 15);
            }
        });
        binding.FilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FilterDialogFragment filterDialogFragment = new FilterDialogFragment(filterOptions);
//                filterDialogFragment.show(getChildFragmentManager(), "Filter");
                showFilterDialog();
                // After Dismissing the dialog
            }
        });
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
    private void displayRecipes(List<RecipeInfo> recipes, int limit){
        binding.mainContent.removeAllViews();
        for(RecipeInfo recipe: recipes){
            if(limit == 0)
                break;
            binding.mainContent.addView(new RecipeBoxLayout(requireContext(), R.id.recipeSearchFragment, loadingDialog, recipe));
            limit--;
        }
    }
    private void showFilterDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_dialog, null);
        AutoCompleteTextView inIngredient = dialogView.findViewById(R.id.iningredient);
        AutoCompleteTextView outIngredient = dialogView.findViewById(R.id.outingredient);

        //Set the current filter options in the dialog view
        LinearLayout toggleGroup = dialogView.findViewById(R.id.toggleGroup);
        if(filterOptions.priceFilter.size() < 5){
            for(int price : filterOptions.priceFilter)
                ((ToggleButton)toggleGroup.getChildAt(price-1)).setChecked(true);
        }
        if(filterOptions.minTime != 0)
            ((EditText)dialogView.findViewById(R.id.minTimeInput)).setText(Integer.toString(filterOptions.minTime));
        if(filterOptions.maxTime != Integer.MAX_VALUE)
            ((EditText)dialogView.findViewById(R.id.maxTimeInput)).setText(Integer.toString(filterOptions.maxTime));
        LinearLayout ingredientListIn = dialogView.findViewById(R.id.ingredientsIn);
        for (String ingredient : filterOptions.includeIngredients){
            View ingredientView = inflater.inflate(R.layout.ingredient_item, null);
            ((TextView)ingredientView.findViewById(R.id.ingredientName)).setText(ingredient);
            ingredientView.findViewById(R.id.deleteIngredient).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ingredients.add(((TextView)ingredientView.findViewById(R.id.ingredientName)).getText().toString());
                    outIngredient.setAdapter( new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, ingredients));
                    inIngredient.setAdapter( new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, ingredients));
                    ingredientListIn.removeView(ingredientView);
                }
            });
            ingredientListIn.addView(ingredientView);
        }
        LinearLayout ingredientListOut = dialogView.findViewById(R.id.ingredientsOut);
        for(String ingredient : filterOptions.excludeIngredients){
            View ingredientView = inflater.inflate(R.layout.ingredient_item, null);
            ((TextView)ingredientView.findViewById(R.id.ingredientName)).setText(ingredient);
            ingredientView.findViewById(R.id.deleteIngredient).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ingredients.add(((TextView)ingredientView.findViewById(R.id.ingredientName)).getText().toString());
                    outIngredient.setAdapter( new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, ingredients));
                    inIngredient.setAdapter( new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, ingredients));
                    ingredientListOut.removeView(ingredientView);
                }
            });
            ingredientListOut.addView(ingredientView);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, ingredients);
        outIngredient.setAdapter(adapter);
        inIngredient.setAdapter(adapter);
        outIngredient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout ingredientList = dialogView.findViewById(R.id.ingredientsOut);
                View ingredientView = inflater.inflate(R.layout.ingredient_item, null);
                ((TextView)ingredientView.findViewById(R.id.ingredientName)).setText(outIngredient.getText().toString());
                ingredientView.findViewById(R.id.deleteIngredient).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ingredients.add(((TextView)ingredientView.findViewById(R.id.ingredientName)).getText().toString());
                        outIngredient.setAdapter( new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, ingredients));
                        inIngredient.setAdapter( new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, ingredients));
                        ingredientList.removeView(ingredientView);
                    }
                });
                ingredientList.addView(ingredientView);

                ingredients.remove(outIngredient.getText().toString());
                outIngredient.setAdapter( new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, ingredients));
                inIngredient.setAdapter( new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, ingredients));
                outIngredient.setText("");
            }
        });
        inIngredient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout ingredientList = dialogView.findViewById(R.id.ingredientsIn);
                View ingredientView = inflater.inflate(R.layout.ingredient_item, null);
                ((TextView)ingredientView.findViewById(R.id.ingredientName)).setText(inIngredient.getText().toString());
                ingredientView.findViewById(R.id.deleteIngredient).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ingredients.add(((TextView)ingredientView.findViewById(R.id.ingredientName)).getText().toString());
                        outIngredient.setAdapter( new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, ingredients));
                        inIngredient.setAdapter( new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, ingredients));
                        ingredientList.removeView(ingredientView);
                    }
                });
                ingredientList.addView(ingredientView);

                ingredients.remove(inIngredient.getText().toString());
                inIngredient.setAdapter( new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, ingredients));
                outIngredient.setAdapter( new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, ingredients));

                inIngredient.setText("");

            }
        });
        builder.setView(dialogView)
                .setTitle("Filter Recipes")
                .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LinearLayout toggleGroup = dialogView.findViewById(R.id.toggleGroup);
                        filterOptions.priceFilter.clear();
                        for(int i = 0; i < toggleGroup.getChildCount(); i++){
                            if(((ToggleButton)toggleGroup.getChildAt(i)).isChecked())
                                filterOptions.priceFilter.add(i+1);
                        }
                        if(filterOptions.priceFilter.size() == 0)
                            filterOptions.priceFilter = IntStream.range(1,6).boxed().collect(Collectors.toList());
                        String minTime = ((EditText)dialogView.findViewById(R.id.minTimeInput)).getText().toString();
                        filterOptions.minTime = minTime.matches("\\d+") ? Integer.parseInt(minTime) : 0;
                        String maxTime = ((EditText)dialogView.findViewById(R.id.maxTimeInput)).getText().toString();
                        filterOptions.maxTime = (maxTime.matches("\\d+")  && Integer.parseInt(maxTime) > filterOptions.minTime)?
                                Integer.parseInt(maxTime) : Integer.MAX_VALUE;
                        LinearLayout inList = dialogView.findViewById(R.id.ingredientsIn);
                        filterOptions.includeIngredients.clear();
                        for (int i=0; i<inList.getChildCount(); i++)
                            filterOptions.includeIngredients.add(((TextView)inList.getChildAt(i).findViewById(R.id.ingredientName)).getText().toString());
                        LinearLayout outList = dialogView.findViewById(R.id.ingredientsOut);
                        filterOptions.excludeIngredients.clear();
                        for (int i=0; i<outList.getChildCount(); i++)
                            filterOptions.excludeIngredients.add(((TextView)outList.getChildAt(i).findViewById(R.id.ingredientName)).getText().toString());
                        Log.d("api", filterOptions.toString());
                        Call<List<RecipeInfo>> filteredRecipesCall = apiService.getFilteredRecipes(userID, filterOptions);
                        filteredRecipesCall.enqueue(new Callback<List<RecipeInfo>>() {
                            @Override
                            public void onResponse(Call<List<RecipeInfo>> call, Response<List<RecipeInfo>> response) {
                                recipes = response.body();
                                Collections.sort(recipes, (r1, r2) -> Float.compare(r2.avg_rating, r1.avg_rating));
                                displayRecipes(recipes, 15);
                                binding.searchBar.setText("");
                                Log.d("api", recipes.toString());
                            }

                            @Override
                            public void onFailure(Call<List<RecipeInfo>> call, Throwable t) {

                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
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
}