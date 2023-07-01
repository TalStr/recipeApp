package com.example.recipeapp.customViews;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.example.recipeapp.R;
import com.example.recipeapp.api.RecipeIngredientV2;
import com.example.recipeapp.databinding.FragmentAddRecipeTwoBinding;

import java.util.ArrayList;

public class AddRecipeTwoFragment extends Fragment {
    private FragmentAddRecipeTwoBinding binding;
    final ArrayList<RecipeIngredientV2> ingredients = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddRecipeTwoBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);  // You should call the super method first
        IngredientListAdapter adapter = new IngredientListAdapter(getContext(), ingredients);
        binding.ingredientList.setAdapter(adapter);
        binding.addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredientName = binding.ingredientName.getText().toString().trim();
                String ingredientDetails = binding.ingredientDetails.getText().toString().trim();
                if (ingredientName.isEmpty() || ingredientDetails.isEmpty()) {
                    if (ingredientName.isEmpty())
                        binding.ingredientNameField.setError("required*");
                    if (ingredientDetails.isEmpty())
                        binding.ingredientDetailsField.setError("required*");
                } else {
                    ingredients.add(new RecipeIngredientV2(ingredientName, ingredientDetails));
                    binding.ingredientNameField.setError(null);
                    binding.ingredientDetailsField.setError(null);
                    binding.ingredientName.setText("");
                    binding.ingredientDetails.setText("");
                    adapter.notifyDataSetChanged();
                }
            }
        });
        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddRecipeDialogFragment parent = ((AddRecipeDialogFragment) getParentFragment());
                        parent.setRecipeIngredients(ingredients);
                        AddRecipeThreeFragment nextFragment = new AddRecipeThreeFragment();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.dialog_content, nextFragment)
                                .commit();
                    }
                });
            }
        });
    }
}