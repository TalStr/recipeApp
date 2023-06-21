package com.example.recipeapp.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ApiService {
    @POST("/login")
    Call<LoginResponse> login(@Body HashMap<String, String> loginData);
    @POST("/signup")
    Call<Integer> signup(@Body HashMap<String, String> signupData);
    @GET("/users/{user_id}/recipes")
    Call<List<RecipeInfo>> getUserRecipes(@Path("user_id") int userID);
    @GET("/users/{user_id}")
    Call<UserDBitem> getUserInfo(@Path("user_id") int userID);
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
    @POST("/recipes/filtered")
    Call<List<RecipeInfo>> getFilteredRecipes(@Body FilterOptions filter);
    @GET("/ingredients")
    Call<List<String>> getAllIngredients();
    @GET("/takeninfo")
    Call<TakenInfo> getTakenInfo();
    @POST("/profilepic/{user_id}")
    Call<Void> setProfilePic(@Path("user_id") int userID, @Body Map<String, String> body);
    @GET("/profilepic/{user_id}")
    Call<String> getProfilePic(@Path("user_id") int userID);
    @POST("/devices/new/{device_token}")
    Call<Void> addDevice(@Path("device_token") String token);
    @POST("/devices/bind/{device_token}/{user_id}")
    Call<Void> bindDevice(@Path("device_token") String token, @Path("user_id") int userID);
    @POST("/devices/unbind/{device_token}")
    Call<Void> unbindDevice(@Path("device_token") String token);
    @GET("/recipe/{recipe_id}/info")
    Call<Recipe> getFullRecipe(@Path("recipe_id") int recipeID);
}
