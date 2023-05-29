package com.example.recipeapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.api.RecipeInfo;
import com.example.recipeapp.databinding.FragmentRecipeBookBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeBookFragment extends Fragment {
    private FragmentRecipeBookBinding binding;
    private DBHelper myDB;
    private int userID;
    private int bookOwnerID;
    private String username;
    private ApiService apiService;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeBookBinding.inflate(inflater, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            userID = CurrentUser.getInstance().getUserID();
            bookOwnerID = bundle.getInt("ownerID");
            username = bundle.getString("username");
        }

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        apiService = ApiClient.getClient(getContext());
        if(userID == bookOwnerID)
            binding.addRecipe.setVisibility(View.VISIBLE);
        binding.addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("userID", userID);
                Navigation.findNavController(v).navigate(R.id.action_recipeBook_to_addRecipe, bundle);
            }
        });

        binding.title.setText(username + "s\nRecipe Book");
        Call<List<RecipeInfo>> call = apiService.getUserRecipes(bookOwnerID);
        call.enqueue(new Callback<List<RecipeInfo>>() {
            @Override
            public void onResponse(Call<List<RecipeInfo>> call, Response<List<RecipeInfo>> response) {
                if(response.isSuccessful()){
                    List<RecipeInfo> recipes = response.body();
                    Log.d("api", recipes.toString());
                    for(RecipeInfo recipe : recipes){
                                    binding.containerLinearLayout.addView(new RecipeBoxLayout(getContext(), recipe));

                    }
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


//        LinkedList<RecipeInfo> recipes = myDB.getUsersRecipes(bookOwnerID);
//        String username = myDB.getUsernameById(bookOwnerID);
//        for (RecipeInfo recipe : recipes) {
//            binding.containerLinearLayout.addView(new RecipeBoxLayout(getContext(), bookOwnerID, username, recipe.recipeID, recipe.name,
//                    recipe.prepTime + recipe.cookTime, recipe.price, 1));
//        }

    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

}