package com.example.recipeapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.transition.Visibility;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.databinding.FragmentAddRecipeBinding;
import com.example.recipeapp.databinding.FragmentRecipeBookBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddRecipeFragment extends Fragment {
    private FragmentAddRecipeBinding binding;
    DBHelper myDB;
    private int userID;
    private int ingredientCount;
    private int instructionCount;
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddRecipeBinding.inflate(inflater, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            userID = bundle.getInt("userID");
        }
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        myDB = DBHelper.getInstance(getContext());
        apiService = ApiClient.getClient(getContext());
        binding.addIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = new LinearLayout(requireContext());
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayout.setPadding(0,dpToPx(5),0,dpToPx(5));

                EditText editTextIngredient = new EditText(getContext());
                editTextIngredient.setLayoutParams(new LinearLayout.LayoutParams(0, dpToPx(55), 0.4f));
                editTextIngredient.setPadding(dpToPx(10), 0, 0, 0);
                editTextIngredient.setHint("Ingredient Name");
                editTextIngredient.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.textinput1));
                ((LinearLayout.LayoutParams) editTextIngredient.getLayoutParams()).setMargins(dpToPx(12), 0, 0, 0);

                EditText editTextQuantity = new EditText(getContext());
                editTextQuantity.setLayoutParams(new LinearLayout.LayoutParams(0, dpToPx(55), 0.3f));
                editTextQuantity.setPadding(dpToPx(10), 0, 0, 0);
                editTextQuantity.setHint("Quantity");
                editTextQuantity.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.textinput1));
                ((LinearLayout.LayoutParams) editTextQuantity.getLayoutParams()).setMargins(dpToPx(12), 0, dpToPx(10), 0);

                Spinner spinner = new Spinner(getContext());
                LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(0, dpToPx(55),0.3f);
                spinner.setLayoutParams(spinnerParams);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                        R.array.Units, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.spinner_background_with_arrow));


                FloatingActionButton deleteButton = new FloatingActionButton(requireContext());
                deleteButton.setId(View.generateViewId());
                deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                deleteButton.setCompatElevation(4);
                deleteButton.setSize(FloatingActionButton.SIZE_MINI);
                deleteButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.delete_red)));
                deleteButton.setImageResource(R.drawable.baseline_remove_24);
                deleteButton.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), PorterDuff.Mode.SRC_IN);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewGroup parentLayout = (ViewGroup) linearLayout.getParent();
                        parentLayout.removeView(linearLayout);
                    }
                });

                ((LinearLayout.LayoutParams) deleteButton.getLayoutParams()).setMarginEnd(dpToPx(12));

                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.addView(editTextIngredient);
                linearLayout.addView(editTextQuantity);
                linearLayout.addView(spinner);
                linearLayout.addView(deleteButton);

                binding.ingredients.addView(linearLayout);
            }
        });

        binding.addInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create LinearLayout
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setGravity(Gravity.CENTER_VERTICAL);
                linearLayout.setPadding(16, 8, 16, 8);

                TextView numberTextView = new TextView(getContext());
                numberTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                numberTextView.setText(String.valueOf(++instructionCount));
                numberTextView.setGravity(Gravity.CENTER_VERTICAL);
                numberTextView.setPadding(16, 0, 16, 0);
                linearLayout.addView(numberTextView);

                EditText instructionText = new EditText(getContext());
                instructionText.setId(View.generateViewId());
                instructionText.setHint("Instructions");
                instructionText.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                instructionText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                instructionText.setGravity(Gravity.TOP | Gravity.START);
                instructionText.setMinLines(1);
                instructionText.setMaxLines(10);
                instructionText.setVerticalScrollBarEnabled(true);
                instructionText.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
                instructionText.setBackgroundResource(R.drawable.textinput1);
                int marginHorizontal = dpToPx(12);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(marginHorizontal, 0, marginHorizontal, 0);
                instructionText.setLayoutParams(layoutParams);

                FloatingActionButton deleteButton = new FloatingActionButton(getContext());
                deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                deleteButton.setElevation(4f);
                deleteButton.setSize(FloatingActionButton.SIZE_MINI);
                deleteButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.delete_red)));
                deleteButton.setImageResource(R.drawable.baseline_remove_24);

                linearLayout.addView(instructionText);
                linearLayout.addView(deleteButton);

                binding.instructions.addView(linearLayout);
                binding.removeInstruction.setVisibility(View.VISIBLE);
            }
        });
        binding.removeInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                binding.instructions.removeViewAt(--instructionCount);
                if(instructionCount == 0){
                    binding.removeInstruction.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String name = binding.recipeNameInput.getText().toString();
//                int prepTime = Integer.parseInt(binding.prepTimeInput.getText().toString());
//                int cookTime = Integer.parseInt(binding.cookTimeInput.getText().toString());
//                int servings = Integer.parseInt(binding.servingsInput.getText().toString());
//                int price = selected.getText().toString().length();
//                selected = (RadioButton)binding.getRoot().findViewById(binding.privacy.getCheckedRadioButtonId());
//                int privacy = binding.privacy.indexOfChild(selected);
//
//                int recipeID = myDB.addRecipe(userID, name, prepTime, cookTime, servings, null, price, privacy);

                HashMap<String, Object> recipeData = new HashMap<>();
                recipeData.put("recipe_name", binding.recipeNameInput.getText().toString());
                recipeData.put("preptime", Integer.parseInt(binding.prepTimeInput.getText().toString()));
                recipeData.put("cooktime", Integer.parseInt(binding.cookTimeInput.getText().toString()));
                recipeData.put("servings", Integer.parseInt(binding.servingsInput.getText().toString()));
                recipeData.put("image", "");
                RadioButton selected = (RadioButton)binding.getRoot().findViewById(binding.cost.getCheckedRadioButtonId());
                recipeData.put("price", selected.getText().toString().length());
                selected = (RadioButton)binding.getRoot().findViewById(binding.privacy.getCheckedRadioButtonId());
                recipeData.put("privacy_level", binding.privacy.indexOfChild(selected));

                Call<Integer> call = apiService.createNewRecipe(userID, recipeData);
                call.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if (response.isSuccessful()) {
                            int recipeID = response.body();
                            Log.d("api", "Received recipe ID: " + recipeID);
                            //Recipe created

                            //create list of ingredients in the recipe
                            int count = binding.ingredients.getChildCount();
                            List<String> ingredientNames = new ArrayList<>();
                            LinearLayout ingredientRow;
                            for(int i = 0; i < count; i++) {
                                ingredientRow =(LinearLayout)binding.ingredients.getChildAt(i);
                                ingredientNames.add(((EditText)ingredientRow.getChildAt(0)).getText().toString());
                            }
                            //add all of them to the recipes ingredients
                            HashMap<String, List<String>> ingredientData = new HashMap<>();
                            ingredientData.put("ingredientNames", ingredientNames);
                            Call<HashMap<String, List<Integer>>> ingredientsCall = apiService.getOrCreateIngredients(ingredientData);
                            ingredientsCall.enqueue(new Callback<HashMap<String,List<Integer>>>() {
                                @Override
                                public void onResponse(Call<HashMap<String,List<Integer>>> call, Response<HashMap<String,List<Integer>>> response) {
                                    if (response.isSuccessful()) {
                                        List<Integer> ingredientIds = response.body().get("ingredient_ids");
                                        Log.d("api", "Ingredient Ids: " + ingredientIds.toString());
                                        double quantity;
                                        int unit;
                                        List<HashMap<String, Object>> ingredients = new ArrayList<>();
                                        for (int i = 0; i < count; i++) {
                                            LinearLayout ingredientRow = (LinearLayout)binding.ingredients.getChildAt(i);
                                            quantity = Double.parseDouble(((EditText) ingredientRow.getChildAt(1)).getText().toString());
                                            unit = ((Spinner) ingredientRow.getChildAt(2)).getSelectedItemPosition();
                                            HashMap<String, Object> ingredient = new HashMap<>();
                                            ingredient.put("ingredient_id", ingredientIds.get(i));
                                            ingredient.put("quantity", quantity);
                                            ingredient.put("unit", unit);
                                            ingredients.add(ingredient);

                                        }
                                        Log.d("api", "Ingredients list: " + ingredients.toString());
                                        Call<Void> addIngredients = apiService.addRecipeIngredients(recipeID, ingredients);
                                        addIngredients.enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                Log.d("api", "Ingredients added successfully");

                                            }

                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {
                                                Log.e("api", "Error adding recipe ingredients: " + t.getMessage());
                                            }
                                        });

                                    } else {
                                        // Handle error
                                    }
                                }

                                @Override
                                public void onFailure(Call<HashMap<String,List<Integer>>> call, Throwable t) {
                                    Log.e("api", "Error getting or creating ingredients: " + t.getMessage());
                                }
                            });

                            List<HashMap<String, Object>> instructions = new ArrayList<>();
                            int step;
                            String text;
                            LinearLayout instructionRow;
                            for(int i = 0; i < instructionCount; i++) {
                                instructionRow = (LinearLayout)binding.instructions.getChildAt(i);
                                step = Integer.parseInt(((TextView)instructionRow.getChildAt(0)).getText().toString());
                                text = ((EditText)instructionRow.getChildAt(1)).getText().toString();

                                HashMap<String, Object> instruction = new HashMap<>();
                                instruction.put("step", step);
                                instruction.put("description", text);

                                instructions.add(instruction);
                            }

                            // Make API call to add instructions for the recipe
                            Call<Void> addInstructions = apiService.addRecipeInstructions(recipeID, instructions);
                            addInstructions.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    // Handle success
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Log.e("api", "Error adding recipe instructions: " + t.getMessage());
                                }
                            });
                            Navigation.findNavController(v).navigateUp();

                        } else {
                            Log.d("API", "Error creating new recipe: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.e("api", "Error creating new recipe: " + t.getMessage());
                    }
                });


//                int count = binding.ingredients.getChildCount();
//                int ingredientID;
//                double quantity;
//                int unit;
//                LinearLayout ingredientRow;
//                for (int i = 0; i < count; i++) {
//                    ingredientRow = (LinearLayout)binding.ingredients.getChildAt(i);
//                    ingredientID = myDB.getIngredientID(((EditText)ingredientRow.getChildAt(0)).getText().toString());
//                    if(ingredientID == -1){
//                        ingredientID = myDB.addIngredient(((EditText)ingredientRow.getChildAt(0)).getText().toString());
//                    }
//                    quantity = Double.parseDouble(((EditText)ingredientRow.getChildAt(1)).getText().toString());
//                    unit = ((Spinner)ingredientRow.getChildAt(2)).getSelectedItemPosition();
//                    myDB.addIngredientToRecipe(recipeID,ingredientID,quantity,unit);
//                }


//                int step;
//                String text;
//                LinearLayout instructionRow;
//                for(int i = 0; i < instructionCount; i++)
//                {
//                    instructionRow = (LinearLayout)binding.instructions.getChildAt(i);
//                    step = Integer.parseInt(((TextView)instructionRow.getChildAt(0)).getText().toString());
//                    text = ((EditText)instructionRow.getChildAt(1)).getText().toString();
//                    myDB.addInstructionToRecipe(recipeID, step, text);
//                }
            }
        });
    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

}