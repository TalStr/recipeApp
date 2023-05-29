package com.example.recipeapp.api;

import com.example.recipeapp.UserDBitem;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.HashMap;
import java.util.List;

public interface ApiService {
    @POST("/login")
    Call<Integer> login(@Body HashMap<String, String> loginData);
    @POST("/signup")
    Call<Integer> signup(@Body HashMap<String, String> signupData);
    @GET("/users/{user_id}/recipes")
    Call<List<RecipeInfo>> getUserRecipes(@Path("user_id") int userID);
    @GET("/users/{user_id}")
    Call<UserDBitem> getUserInfo(@Path("user_id") int userID);
    @GET("/users")
    Call<List<UserPublicInfo>> getAllUsers();
    @POST("/users/{user_id}/recipes")
    Call<Integer> createNewRecipe(@Path("user_id") int user_id, @Body HashMap<String, Object> recipeData);
    @POST("/recipes/{recipe_id}/ingredients")
    Call<Void> addRecipeIngredients(@Path("recipe_id") int recipeId, @Body List<HashMap<String, Object>> ingredients);
    @POST("/ingredients/batch")
    Call<HashMap<String, List<Integer>>> getOrCreateIngredients(@Body HashMap<String, List<String>> ingredientNames);
    @POST("/instructions/{recipe_id}")
    Call<Void> addRecipeInstructions(@Path("recipe_id") int recipeId, @Body List<HashMap<String, Object>> instructions);
    @GET("/recipes/{recipe_id}")
    Call<RecipeInfo> getRecipe(@Path("recipe_id") int recipeID);
    @GET("/recipes/{recipe_id}/ingredients")
    Call<List<RecipeIngredient>> getRecipeIngredients(@Path("recipe_id") int recipeID);
    @GET("/recipes/{recipe_id}/instructions")
    Call<List<Instruction>> getRecipeInstructions(@Path("recipe_id") int recipeID);
    @POST("/follow/{follower_id}/{following_id}")
    Call<Void> follow(@Path("follower_id") int followerId, @Path("following_id") int followingId);
    @DELETE("/unfollow/{follower_id}/{following_id}")
    Call<Void> unfollow(@Path("follower_id") int followerId, @Path("following_id") int followingId);
    @GET("/users/following/{user_id}")
    Call<List<UserPublicInfo>> getFollowing(@Path("user_id") int userID);
    @GET("/following/{user_id}")
    Call<List<UserPublicInfo>> getUserSearch(@Path("user_id") int userID);
    @POST("/recipes/{recipe_id}/reviews")
    Call<Void> reviewRecipe(@Path("recipe_id") int recipeID, @Body HashMap<String, Object> reviewInfo);
    @GET("/recipes/{recipe_id}/reviews")
    Call<List<ReviewInfo>> getReviews(@Path("recipe_id") int recipeID);
    @GET("/recipes/{recipe_id}/reviews/{reviewer_id}")
    Call<ReviewInfo> getUserReview(@Path("recipe_id") int recipeID, @Path("reviewer_id") int userID);
    @GET("/recipes")
    Call<List<RecipeInfo>> getAllRecipes();
}
