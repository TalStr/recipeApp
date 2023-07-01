package com.example.recipeapp.customViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.recipeapp.R;
import com.example.recipeapp.api.RecipeIngredientV2;
import com.example.recipeapp.api.RecipeStepV2;

import java.util.ArrayList;

public class InstructionsListAdapter extends ArrayAdapter<RecipeStepV2> {
    private ArrayList<RecipeStepV2> stepList;
    public InstructionsListAdapter(Context context, ArrayList<RecipeStepV2> stepList){
        super(context, R.layout.ingredient_add_listitem, stepList);
        this.stepList = stepList;
    }
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        RecipeStepV2 step = getItem(position);

        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.step_add_listitem, parent, false);

        TextView stepNumber = convertView.findViewById(R.id.stepNumber);
        TextView stepInstructions = convertView.findViewById(R.id.stepInstructions);
        ImageView removeStep = convertView.findViewById(R.id.removeStep);

        stepNumber.setText("Step " + step.getStep());
        stepInstructions.setText(step.getInstructions());

        removeStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepList.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
}
