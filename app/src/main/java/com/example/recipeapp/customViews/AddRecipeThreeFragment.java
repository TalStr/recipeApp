package com.example.recipeapp.customViews;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipeapp.R;
import com.example.recipeapp.api.RecipeIngredientV2;
import com.example.recipeapp.api.RecipeStepV2;
import com.example.recipeapp.databinding.FragmentAddRecipeThreeBinding;

import java.util.ArrayList;

public class AddRecipeThreeFragment extends Fragment {
    private FragmentAddRecipeThreeBinding binding;
    ArrayList<RecipeStepV2> steps = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddRecipeThreeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        InstructionsListAdapter adapter = new InstructionsListAdapter(getContext(), steps);
        binding.stepsList.setAdapter(adapter);
        binding.addStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stepInfo = binding.stepInfo.getText().toString().trim();
                if (stepInfo.isEmpty()) {
                    binding.stepInfoField.setError("Required");
                } else {
                    steps.add(new RecipeStepV2(binding.stepsList.getAdapter().getCount()+1, stepInfo));
                    binding.stepInfoField.setError(null);
                    binding.stepInfo.setText("");
                    adapter.notifyDataSetChanged();
                }
            }
        });
        binding.finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AddRecipeDialogFragment) getParentFragment()).setRecipeSteps(steps);
                DialogFragment dialogFragment = (DialogFragment) getParentFragment();
                if(dialogFragment != null) {
                    dialogFragment.dismiss();
                }
            }
        });

    }
}