package com.example.recipeapp.customViews;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;

import com.example.recipeapp.CurrentUser;
import com.example.recipeapp.R;
import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.api.UserPublicInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserBoxLayout extends ConstraintLayout {
    public UserBoxLayout(Context context, int sourceFragmentID, UserPublicInfo profileInfo) {
        super(context);
        int userID = CurrentUser.getInstance().getUserID();
        inflate(context, R.layout.user_box, this);
        ApiService apiService = ApiClient.getClient(getContext());
        ((ImageView)findViewById(R.id.profilepic)).setImageBitmap(profileInfo.getProfilePic());
        ((TextView)findViewById(R.id.username)).setText(profileInfo.username);
        if(profileInfo.following)
            ((Button)findViewById(R.id.followButton)).setText("Unfollow");
        ((Button)findViewById(R.id.followButton)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profileInfo.following){
                    Call<Void> unfollowProfile = apiService.unfollow(userID, profileInfo.user_id);
                    unfollowProfile.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.isSuccessful()){
                                profileInfo.following = false;
                                ((Button)findViewById(R.id.followButton)).setText("Follow");

                            }
                            else
                                Log.e("api", "unfollow onResponse: Failed, status code: " + response.code());
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("api", "unfollow onFailure: " + t.getMessage(), t);

                        }
                    });
                }
                else{
                    Call<Void> followProfile = apiService.follow(userID, profileInfo.user_id);
                    followProfile.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.isSuccessful()){
                                profileInfo.following = true;
                                ((Button)findViewById(R.id.followButton)).setText("unFollow");

                            }
                            else
                                Log.e("api", "follow onResponse: Failed, status code: " + response.code() + userID + ", " + profileInfo.user_id);
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("api", "follow onFailure: " + t.getMessage(), t);

                        }
                    });
                }
            }
        });
        ((Button)findViewById(R.id.bookButton)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("ownerID", profileInfo.user_id);
                bundle.putString("username", profileInfo.username);
                int actionID;
                if (sourceFragmentID == R.id.userSearchFragment)
                    actionID = R.id.action_userSearchFragment_to_recipeBookFragment;
                else
                    actionID = R.id.action_followingList_to_recipeBook;
                Log.d("api", actionID + ", " + bundle.toString());
                Navigation.findNavController(v).navigate(actionID, bundle);
            }
        });


    }
}
