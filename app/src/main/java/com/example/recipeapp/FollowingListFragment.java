package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.api.UserPublicInfo;
import com.example.recipeapp.databinding.FragmentFollowingListBinding;
import com.example.recipeapp.databinding.FragmentHomeBinding;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowingListFragment extends Fragment {
    private FragmentFollowingListBinding binding;
    private int userID;
    private  DBHelper myDB;
    private ApiService apiService;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFollowingListBinding.inflate(inflater, container, false);
        apiService = ApiClient.getClient(getContext());
        Bundle bundle = getArguments();
        if (bundle != null) {
            userID = bundle.getInt("userID");
        }

        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Call<List<UserPublicInfo>> call = apiService.getFollowing(userID);
        call.enqueue(new Callback<List<UserPublicInfo>>() {
            @Override
            public void onResponse(Call<List<UserPublicInfo>> call, Response<List<UserPublicInfo>> response) {
                if(response.isSuccessful()){
                    List<UserPublicInfo> users = response.body();
                    Log.d("api", users.toString());
                    for (UserPublicInfo user : users) {
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

//        LinkedList<UserDBitem> users = myDB.getFriends(userID);
//        for (UserDBitem user : users) {
//            binding.following.addView(new UserBoxLayout(getContext(), R.id.followingListFragment, userID, user.user_id,
//                    user.username, user.profilepic, myDB));
//        }

    }
}