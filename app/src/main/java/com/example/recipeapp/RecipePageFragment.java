package com.example.recipeapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.api.Instruction;
import com.example.recipeapp.api.RecipeInfo;
import com.example.recipeapp.api.RecipeIngredient;
import com.example.recipeapp.api.ReviewInfo;
import com.example.recipeapp.databinding.FragmentRecipePageBinding;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipePageFragment extends Fragment {
    private FragmentRecipePageBinding binding;


    int userID;
    int recipeID;
    ApiService apiService;
    RecipeInfo info;
    private AtomicInteger apiCallsCompleted = new AtomicInteger(0);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipePageBinding.inflate(inflater, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            userID = CurrentUser.getInstance().getUserID();
            recipeID = bundle.getInt("recipeID");
        }
        Log.e("api", userID + ", " + recipeID);
        apiService = ApiClient.getClient(getContext());
        Call<RecipeInfo> call = apiService.getRecipe(recipeID);
        call.enqueue(new Callback<RecipeInfo>() {
            @Override
            public void onResponse(Call<RecipeInfo> call, Response<RecipeInfo> response) {
                info = response.body();
                binding.recipeName.setText(info.recipe_name);
                binding.authorName.setText("Author\n" + info.author_name);
                //Rating
                binding.averageRating.setRating(info.avg_rating);
                binding.recipePrepTime.setText("Prep Time: " + info.preptime);
                binding.recipeCookTime.setText("Cook Time: " + info.cooktime);
                binding.recipeServings.setText("Servings: " + info.servings);
                binding.recipePrice.setText("Price: " + (getResources().getStringArray(R.array.prices))[info.price - 1]);
                if (info.user_id == userID) {
                    binding.addReview.setVisibility(View.GONE);
                }
                setReviews();
                setRecipeIngredients();
            }

            @Override
            public void onFailure(Call<RecipeInfo> call, Throwable t) {

            }
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.addReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.review_dialog, null);

                final MaterialRatingBar input1 = dialogView.findViewById(R.id.rating);
                final EditText input2 = dialogView.findViewById(R.id.review);

                builder.setView(dialogView)
                        .setTitle("Your Popup Title")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int inputValue1 = (int)input1.getRating();
                                String inputValue2 = input2.getText().toString();

                                HashMap<String, Object> reviewInfo = new HashMap<>();
                                reviewInfo.put("reviewer_id", userID);
                                reviewInfo.put("rating", inputValue1);
                                reviewInfo.put("comment", inputValue2);
                                Log.d("api", reviewInfo.toString());
                                Call<Void> call = apiService.reviewRecipe(recipeID, reviewInfo);
                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if(response.isSuccessful())
                                            Log.d("api", "Success");
                                        else
                                            Log.e("api", "fail: " + response.code());
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.e("api", "onFail: " + t.getMessage(), t);
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
    private void setReviews(){
        Call<List<ReviewInfo>> reviews = apiService.getReviews(recipeID);
        reviews.enqueue(new Callback<List<ReviewInfo>>() {
            @Override
            public void onResponse(Call<List<ReviewInfo>> call, Response<List<ReviewInfo>> response) {
                List<ReviewInfo> reviews = response.body();
                for(ReviewInfo review : reviews){
                    // Inflate the layout
                    View reviewView = getLayoutInflater().inflate(R.layout.review_template, null);

                    // Set the data
                    // Assuming your review_template layout has TextViews with ids reviewerName, reviewRating and reviewText
                    TextView reviewerName = reviewView.findViewById(R.id.username);
                    MaterialRatingBar reviewRating = reviewView.findViewById(R.id.rating);
                    TextView reviewText = reviewView.findViewById(R.id.comment);

                    reviewerName.setText(review.username);
                    reviewRating.setRating(review.stars);
                    reviewText.setText(review.comment);

                    // Add the inflated layout to mainContent
                    binding.mainContent.addView(reviewView);
                    if(review.user_id == userID)
                        binding.addReview.setVisibility(View.GONE);
                }
                incrementApiCallsCompleted();
            }

            @Override
            public void onFailure(Call<List<ReviewInfo>> call, Throwable t) {

            }
        });

    }
    private void setRecipeIngredients(){
        Call<List<RecipeIngredient>> call = apiService.getRecipeIngredients(recipeID);
        call.enqueue(new Callback<List<RecipeIngredient>>() {
            @Override
            public void onResponse(Call<List<RecipeIngredient>> call, Response<List<RecipeIngredient>> response) {
                if(response.isSuccessful()){
                    Log.d("api", "Ingredients onResponse: Success");
                    List<RecipeIngredient> ingredients = response.body();
                    String unit = "";
                    String text;
                    for (RecipeIngredient ingredient : ingredients) {
                        if(ingredient.unit != 0){
                            unit = (getResources().getStringArray(R.array.Units))[ingredient.unit];
                            text = ingredient.quantity + " " + unit + " Of " + ingredient.ingredient_name;
                        }
                        else
                            text = ingredient.quantity + " " + ingredient.ingredient_name;

                        TextView textView = new TextView(getContext());
                        textView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                        textView.setPadding(dpToPx(32), dpToPx(8), dpToPx(16), dpToPx(8));
                        textView.setText(text);
                        binding.ingredients.addView(textView);
                    }
                    setRecipeInstructions();
                    incrementApiCallsCompleted();

                }
                else{
                    Log.e("api", "Ingredients onResponse: Failed, status code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<RecipeIngredient>> call, Throwable t) {
                incrementApiCallsCompleted();
                Log.e("api", "Ingredients onFailure: " + t.getMessage(), t);
            }
        });
    }

    private void setRecipeInstructions(){
        Call<List<Instruction>> call = apiService.getRecipeInstructions(recipeID);
        call.enqueue(new Callback<List<Instruction>>() {
            @Override
            public void onResponse(Call<List<Instruction>> call, Response<List<Instruction>> response) {
                List<Instruction> instructions = response.body();
                for(Instruction instruction : instructions)
                {
                    LinearLayout instructionLayout = new LinearLayout(getContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 0, 0, dpToPx(16)); // set bottom margin to 16dp
                    instructionLayout.setOrientation(LinearLayout.HORIZONTAL);
                    instructionLayout.setLayoutParams(layoutParams);

                    TextView step = new TextView(getContext());
                    step.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    step.setPadding(dpToPx(32),0, 0, 0);

                    step.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)); // set weight to 1
                    step.setText(instruction.step + ". ");
                    instructionLayout.addView(step);

                    TextView instructionText = new TextView(getContext());
                    instructionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    instructionText.setLayoutParams(new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.0f)); // set weight to 1
                    instructionText.setText(instruction.description);

                    instructionLayout.addView(instructionText);
                    layoutParams.setMargins(0, 0, 0, dpToPx(16)); // set bottom margin to 16dp
                    instructionLayout.setLayoutParams(layoutParams);
                    binding.instructions.addView(instructionLayout);
                }
                incrementApiCallsCompleted();

            }

            @Override
            public void onFailure(Call<List<Instruction>> call, Throwable t) {
                incrementApiCallsCompleted();
            }
        });
    }
    private void incrementApiCallsCompleted() {
        if (apiCallsCompleted.incrementAndGet() == 3) {
            binding.progressBar.setVisibility(View.GONE);
            binding.mainContent.setVisibility(View.VISIBLE);
        }
    }

}