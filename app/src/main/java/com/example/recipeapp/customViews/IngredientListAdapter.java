package com.example.recipeapp.customViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.recipeapp.R;
import com.example.recipeapp.api.RecipeIngredientV2;

import java.util.ArrayList;
import java.util.List;

public class IngredientListAdapter extends ArrayAdapter<RecipeIngredientV2> {
    private List<RecipeIngredientV2> ingredientList;
    public IngredientListAdapter(Context context, ArrayList<RecipeIngredientV2> ingredientList){
        super(context, R.layout.ingredient_add_listitem, ingredientList);
        this.ingredientList = ingredientList;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        RecipeIngredientV2 ingredient = getItem(position);

        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ingredient_add_listitem, parent, false);

        TextView ingredientName = convertView.findViewById(R.id.ingredientName);
        TextView ingredientDetails = convertView.findViewById(R.id.ingredientDetails);
        ImageView removeIngredient = convertView.findViewById(R.id.removeIngredient);

        ingredientName.setText(ingredient.getName());
        ingredientDetails.setText(ingredient.getDetails());

        removeIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredientList.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
}
