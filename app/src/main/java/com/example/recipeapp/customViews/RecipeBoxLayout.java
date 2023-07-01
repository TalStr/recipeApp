package com.example.recipeapp.customViews;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;

import com.example.recipeapp.R;
import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.api.Recipe;
import com.example.recipeapp.api.RecipeInfo;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeBoxLayout extends ConstraintLayout {
    ApiService apiService;
    public RecipeBoxLayout(Context context, int sourceFragmentID, LoadingDialog loadingDialog, RecipeInfo info) {
        super(context);
        int recipeID = info.recipe_id;
        apiService = ApiClient.getClient(context);
        inflate(context, R.layout.recipe_box, this);
        ((TextView)findViewById(R.id.recipeName)).setText(info.recipe_name);
        ((TextView)findViewById(R.id.author)).setText("Author\n"+info.author_name);
        if(info.avg_rating == -1){
            ((MaterialRatingBar)findViewById(R.id.rating_Bar)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.noRatingsYet)).setVisibility(View.VISIBLE);
        }
        else
            ((MaterialRatingBar)findViewById(R.id.rating_Bar)).setRating(info.avg_rating);
        ((TextView)findViewById(R.id.cookTime)).setText("Time\n"+ (info.preptime +info.cooktime) + " Minutes");
        ((TextView)findViewById(R.id.price)).setText("Price Group\n"+  (getResources().getStringArray(R.array.prices))[info.price-1]);
        ((Button)findViewById(R.id.viewRecipe)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();
                Call<Recipe> call = apiService.getFullRecipe(recipeID);
                call.enqueue(new Callback<Recipe>() {
                    @Override
                    public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                        if(response.isSuccessful()){
                            Recipe recipe = response.body();
                            Log.d("api", "Response received: " + info.toString());
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("recipe", recipe);
                            loadingDialog.dismissDialog();
                            if(sourceFragmentID == R.id.recipeBookFragment)
                                Navigation.findNavController(v).navigate(R.id.action_recipeBook_to_recipePage, bundle);
                            else
                                Navigation.findNavController(v).navigate(R.id.action_recipeSearchFragment_to_recipePageFragment, bundle);

                        } else {
                            Log.e("api", "Request failed: " + response.code() + ", " + response.message());
                            loadingDialog.dismissDialog();
                        }
                    }

                    @Override
                    public void onFailure(Call<Recipe> call, Throwable t) {
                        Log.e("api", "Request failed: " + t.getMessage(), t);
                        loadingDialog.dismissDialog();
                    }
                });
            }
        });
    }
}
