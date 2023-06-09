package com.example.recipeapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipeapp.CurrentUser;
import com.example.recipeapp.MainActivity;
import com.example.recipeapp.R;
import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.api.LoginResponse;
import com.example.recipeapp.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = ApiClient.getClient(getContext());
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> loginData = new HashMap<>();
                loginData.put("username", binding.username.getText().toString());
                loginData.put("password", binding.password.getText().toString());
                Call<LoginResponse> loginCall = apiService.login(loginData);
                loginCall.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if(response.isSuccessful()){
                            binding.incorrectInfo.setVisibility(View.INVISIBLE);
                            LoginResponse loginResponse = response.body();
                            CurrentUser.getInstance().setInfo(loginResponse.getUserId(), loginResponse.getUsername(), loginResponse.getProfilePic());
                            if(binding.rememberMe.isChecked()){
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("userID", loginResponse.getUserId());
                                editor.putString("username", loginResponse.getUsername());
                                editor.putString("profilePic", loginResponse.getProfilePic());
                                editor.apply();

                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(new OnCompleteListener<String>() {
                                            @Override
                                            public void onComplete(@NonNull Task<String> task) {
                                                if (!task.isSuccessful()) {
                                                    Log.e("FirebaseMessaging", "Failed to retrieve token");
                                                    return;
                                                }

                                                String token = task.getResult();
                                                Call<Void> bindUser = apiService.bindDevice(token, loginResponse.getUserId());
                                                bindUser.enqueue(new Callback<Void>() {
                                                    @Override
                                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                                        if (response.isSuccessful()) {
                                                            Log.d("API", "User binding successful");

                                                            NavOptions navOptions = new NavOptions.Builder()
                                                                    .setPopUpTo(R.id.loginFragment, true)
                                                                    .build();
                                                            Navigation.findNavController(view).navigate(R.id.action_login_to_home, null, navOptions);
                                                        } else {
                                                            Log.e("API", "User binding failed: " + response.message());
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<Void> call, Throwable t) {
                                                        Log.e("API", "User binding request failed: " + t.getMessage());
                                                    }
                                                });
                                            }
                                        });

                            }
                            else{
                                NavOptions navOptions = new NavOptions.Builder()
                                        .setPopUpTo(R.id.loginFragment, true)
                                        .build();
                                Navigation.findNavController(view).navigate(R.id.action_login_to_home, null, navOptions);
                            }
                        }
                        else{
                            Log.d("api", "incorrent Info");
                            binding.incorrectInfo.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Log.e("api", t.getMessage());
                    }
                });
            }
        });

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_login_to_signUp);
            }
        });

    }
}
