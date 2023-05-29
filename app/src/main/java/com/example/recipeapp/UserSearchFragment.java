package com.example.recipeapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.api.UserPublicInfo;
import com.example.recipeapp.databinding.FragmentHomeBinding;
import com.example.recipeapp.databinding.FragmentUserSearchBinding;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSearchFragment extends Fragment {

    private int userID;
    private FragmentUserSearchBinding binding;
    ApiService apiService;
    List<UserPublicInfo> users;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserSearchBinding.inflate(inflater, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            userID = CurrentUser.getInstance().getUserID();
        }
        apiService = ApiClient.getClient(getContext());
        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Call<List<UserPublicInfo>> call = apiService.getUserSearch(userID);
        call.enqueue(new Callback<List<UserPublicInfo>>() {
            @Override
            public void onResponse(Call<List<UserPublicInfo>> call, Response<List<UserPublicInfo>> response) {
                if(response.isSuccessful()){
                    users = response.body();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.results.setVisibility(View.VISIBLE);
                    displayUsers(users, 15); // Display initial list of users
                }
                else
                    Log.e("api", "get all users onResponse: Failed, status code: " + response.code());
            }

            @Override
            public void onFailure(Call<List<UserPublicInfo>> call, Throwable t) {
                Log.e("api", "get all users onFailure: " + t.getMessage(), t);
            }
        });

        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                List<UserPublicInfo> filteredUsers = filterUsers(s.toString());
                displayUsers(filteredUsers, 15);
            }
        });
    }

    private List<UserPublicInfo> filterUsers(String query) {
        List<UserPublicInfo> filteredUsers = new ArrayList<>();
        for (UserPublicInfo user : users) {
            if (user.username.toLowerCase().contains(query.toLowerCase())) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }

    private void displayUsers(List<UserPublicInfo> usersToDisplay, int limit) {
        binding.results.removeAllViews();
        int count = 0;
        for (UserPublicInfo user : usersToDisplay) {
            if (count >= limit) {
                break;
            }
            binding.results.addView(new UserBoxLayout(getContext(), R.id.userSearchFragment, user));
            count++;
        }
    }
}