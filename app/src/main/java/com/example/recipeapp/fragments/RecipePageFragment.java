package com.example.recipeapp.fragments;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.recipeapp.CurrentUser;
import com.example.recipeapp.R;
import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.api.Instruction;
import com.example.recipeapp.api.Recipe;
import com.example.recipeapp.api.RecipeInfo;
import com.example.recipeapp.api.RecipeIngredient;
import com.example.recipeapp.api.ReviewInfo;
import com.example.recipeapp.customViews.LoadingDialog;
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
    Recipe info;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipePageBinding.inflate(inflater, container, false);
        userID = CurrentUser.getInstance().getUserID();
        Bundle bundle = getArguments();
        if (bundle != null) {
            info = (Recipe) bundle.getSerializable("recipe");
            recipeID = info.getInfo().recipe_id;
            setInfo(info.getInfo());
            setIngredients(info.getIngredients());
            setInstructions(info.getInstructions());
            setReviews(info.getReviews());
        }
        apiService = ApiClient.getClient(getContext());
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
                        .setTitle("Add a review")
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
    private void setInfo(RecipeInfo info){
        binding.recipeName.setText(info.recipe_name);
        binding.authorName.setText("Author\n" + info.author_name);
        binding.averageRating.setRating(info.avg_rating);
        binding.recipePrepTime.setText("Prep Time: " + info.preptime);
        binding.recipeCookTime.setText("Cook Time: " + info.cooktime);
        binding.recipeServings.setText("Servings: " + info.servings);
        binding.recipePrice.setText("Price: " + (getResources().getStringArray(R.array.prices))[info.price - 1]);
        if (info.user_id == userID)
            binding.addReview.setVisibility(View.GONE);
    }
    private void setIngredients(List<RecipeIngredient> ingredients){
        if(ingredients == null) return;
        String unit = "";
        String text;
        for (RecipeIngredient ingredient : ingredients) {
            text=ingredient.description;
            TextView textView = new TextView(getContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setPadding(dpToPx(32), dpToPx(8), dpToPx(16), dpToPx(8));
            textView.setText(text);
            binding.ingredients.addView(textView);
        }
    }
    private void setInstructions(List<Instruction> instructions){
        if(instructions == null) return;
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
    }
    private void setReviews(List<ReviewInfo> reviews){
        if(reviews == null) return;
        for(ReviewInfo review : reviews){
            View reviewView = getLayoutInflater().inflate(R.layout.review_template, null);
            TextView reviewerName = reviewView.findViewById(R.id.username);
            MaterialRatingBar reviewRating = reviewView.findViewById(R.id.rating);
            TextView reviewText = reviewView.findViewById(R.id.comment);
            ImageView profile = reviewView.findViewById(R.id.profilepic1);
            profile.setImageBitmap(review.getProfilePic());
            reviewerName.setText(review.username);
            reviewRating.setRating(review.stars);
            reviewText.setText(review.comment);
            binding.mainContent.addView(reviewView);
            if(review.user_id == userID)
                binding.addReview.setVisibility(View.GONE);
        }
    }
}