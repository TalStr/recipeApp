package com.example.recipeapp;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;

import com.example.recipeapp.api.RecipeInfo;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class RecipeBoxLayout extends ConstraintLayout {

    public RecipeBoxLayout(Context context, RecipeInfo info) {
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
                Navigation.findNavController(v).navigate(R.id.action_recipeBook_to_recipePage, bundle);

            }
        });
    }
}
