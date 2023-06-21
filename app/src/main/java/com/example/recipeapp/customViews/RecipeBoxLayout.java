package com.example.recipeapp.customViews;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;

import com.example.recipeapp.R;
import com.example.recipeapp.api.RecipeInfo;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RecipeBoxLayout extends ConstraintLayout {

    public RecipeBoxLayout(Context context, int sourceFragmentID, RecipeInfo info) {
        super(context);
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
                Bundle bundle = new Bundle();
                bundle.putInt("recipeID", info.recipe_id);
                if(sourceFragmentID == R.id.recipeBookFragment)
                    Navigation.findNavController(v).navigate(R.id.action_recipeBook_to_recipePage, bundle);
                else
                    Navigation.findNavController(v).navigate(R.id.action_recipeSearchFragment_to_recipePageFragment, bundle);
            }
        });
    }
}
