package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.databinding.FragmentLoginBinding;

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

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, String> loginData = new HashMap<>();
                loginData.put("username", binding.username.getText().toString());
                loginData.put("password", binding.password.getText().toString());
                Call<Integer> loginCall = apiService.login(loginData);
                loginCall.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if(response.isSuccessful()){
                            Log.d("api", "should work");
                            int userID = response.body();
                            CurrentUser.getInstance().setUserID(userID);
                            CurrentUser.getInstance().setUsername(binding.username.getText().toString());
                            NavOptions navOptions = new NavOptions.Builder()
                                    .setPopUpTo(R.id.loginFragment, true) // Clear the back stack up to the loginFragment (inclusive)
                                    .build();
                            Navigation.findNavController(view).navigate(R.id.action_login_to_home, null, navOptions);
                        }
                        else{
                            Log.d("api", "bad info");

                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.e("api", t.getMessage());
                    }
                });




//                String name = binding.username.getText().toString();
//                String pass = binding.password.getText().toString();
//                if (myDB.validLoginInfo(name, pass)) {
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("userID", myDB.getIDbyUsername(name));
//                    NavOptions navOptions = new NavOptions.Builder()
//                            .setPopUpTo(R.id.loginFragment, true) // Clear the back stack up to the loginFragment (inclusive)
//                            .build();
//                    Navigation.findNavController(view).navigate(R.id.action_login_to_home, bundle, navOptions);
//                }
//
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
