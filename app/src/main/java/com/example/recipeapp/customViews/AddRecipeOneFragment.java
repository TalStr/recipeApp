package com.example.recipeapp.customViews;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.recipeapp.R;
import com.example.recipeapp.api.NewRecipe;
import com.example.recipeapp.databinding.FragmentAddRecipeOneBinding;

public class AddRecipeOneFragment extends Fragment {
    private FragmentAddRecipeOneBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddRecipeOneBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);  // You should call the super method first
        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String recipeName = binding.recipeNameTextField.getEditText().getText().toString();
                String servings = binding.servingsTextField.getEditText().getText().toString();
                String prepTime = binding.prepTimeTextField.getEditText().getText().toString();
                String cookTime = binding.cookTimeTextField.getEditText().getText().toString();
                RadioButton price = (RadioButton)binding.getRoot().findViewById(binding.priceRadioGroup.getCheckedRadioButtonId());
                int priceGroup = price.getText().toString().length();
                boolean isPrivate = binding.privacySwitch.isChecked();

                if (recipeName.isEmpty() || servings.isEmpty() || prepTime.isEmpty() || cookTime.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                AddRecipeDialogFragment parentFragment = (AddRecipeDialogFragment) getParentFragment();
                if (parentFragment != null)
                    parentFragment.setRecipeInfo(recipeName,Integer.parseInt(prepTime),Integer.parseInt(cookTime),
                            Integer.parseInt(servings),priceGroup,isPrivate);

                AddRecipeTwoFragment nextFragment = new AddRecipeTwoFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.dialog_content, nextFragment)
                        .commit();
            }
        });
    }
}