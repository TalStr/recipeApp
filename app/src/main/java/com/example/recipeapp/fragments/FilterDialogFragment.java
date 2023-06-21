package com.example.recipeapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.recipeapp.R;
import com.example.recipeapp.api.FilterOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class FilterDialogFragment extends DialogFragment {

    private FilterOptions filterOptions;

    public FilterDialogFragment(FilterOptions filterOptions) {
        this.filterOptions = filterOptions;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.filter_dialog, null);

        List<String> ingredients = new ArrayList<>();
        ingredients.add("Chicken");
        ingredients.add("Beef");
        ingredients.add("Pork");
        ingredients.add("Fish");
        ingredients.add("Eggs");
        ingredients.add("Milk");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_activated_1, ingredients);

        AutoCompleteTextView outIngredient = dialogView.findViewById(R.id.outingredient);
        outIngredient.setAdapter(adapter);
        AutoCompleteTextView inIngredient = dialogView.findViewById(R.id.iningredient);
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
                                filterOptions.priceFilter.add(i);
                        }
                        filterOptions.minTime = Integer.parseInt(((EditText)dialogView.findViewById(R.id.minTimeInput)).getText().toString());
                        filterOptions.maxTime = Integer.parseInt(((EditText)dialogView.findViewById(R.id.maxTimeInput)).getText().toString());
                        LinearLayout inList = dialogView.findViewById(R.id.ingredientsIn);
                        for (int i=0; i<inList.getChildCount(); i++)
                            filterOptions.includeIngredients.add(((TextView)inList.getChildAt(i).findViewById(R.id.ingredientName)).getText().toString());
                        LinearLayout outList = dialogView.findViewById(R.id.ingredientsOut);
                        for (int i=0; i<outList.getChildCount(); i++)
                            filterOptions.excludeIngredients.add(((TextView)outList.getChildAt(i).findViewById(R.id.ingredientName)).getText().toString());
                        Log.d("api", filterOptions.toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        //dialog.setOnDismissListener();
        return builder.create();
    }
}
