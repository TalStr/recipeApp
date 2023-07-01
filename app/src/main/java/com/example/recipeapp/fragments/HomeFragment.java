package com.example.recipeapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipeapp.CurrentUser;
import com.example.recipeapp.R;
import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.api.UserDBitem;
import com.example.recipeapp.databinding.FragmentHomeBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    private int userID;
    private FragmentHomeBinding binding;
    private ApiService apiService;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        userID = CurrentUser.getInstance().getUserID();
        apiService = ApiClient.getClient(getContext());
        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.welcomeMsg.setText("Welcome " + CurrentUser.getInstance().getUsername());
        binding.profilePic.setImageBitmap(CurrentUser.getInstance().getProfilePic());
        binding.settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_home_to_accountManager);
            }
        });
        binding.recipeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_recipeSearchFragment);
            }
        });
        binding.friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_home_to_friendList);
            }
        });
        binding.recipeBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putInt("ownerID", userID);
                bundle.putString("username", CurrentUser.getInstance().getUsername());
                Navigation.findNavController(view).navigate(R.id.action_home_to_recipeBook, bundle);
            }
        });
        binding.userSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_userSearchFragment, bundle);
            }
        });
    }
}
