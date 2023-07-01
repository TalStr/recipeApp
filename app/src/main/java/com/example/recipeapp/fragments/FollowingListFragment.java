package com.example.recipeapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipeapp.CurrentUser;
import com.example.recipeapp.R;
import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.api.UserPublicInfo;
import com.example.recipeapp.customViews.UserBoxLayout;
import com.example.recipeapp.databinding.FragmentFollowingListBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingListFragment extends Fragment {
    private FragmentFollowingListBinding binding;
    private int userID;
    private ApiService apiService;
    List<UserPublicInfo> following;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFollowingListBinding.inflate(inflater, container, false);
        apiService = ApiClient.getClient(getContext());
        userID = CurrentUser.getInstance().getUserID();

        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Call<List<UserPublicInfo>> call = apiService.getFollowing(userID);
        call.enqueue(new Callback<List<UserPublicInfo>>() {
            @Override
            public void onResponse(Call<List<UserPublicInfo>> call, Response<List<UserPublicInfo>> response) {
                if(response.isSuccessful()){
                    following = response.body();
                    for (UserPublicInfo user : following) {
                        binding.following.addView(new UserBoxLayout(getContext(), R.id.followingListFragment, user));
                    }
                }

                else
                    Log.e("api", "get following onResponse: Failed, status code: " + response.code());
            }

            @Override
            public void onFailure(Call<List<UserPublicInfo>> call, Throwable t) {
                Log.e("api", "get following onFailure: " + t.getMessage(), t);

            }
        });

    }
}