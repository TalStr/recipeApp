package com.example.recipeapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.recipeapp.R;
import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.api.TakenInfo;
import com.example.recipeapp.databinding.FragmentLoginBinding;
import com.example.recipeapp.databinding.FragmentSignUpBinding;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpFragment extends Fragment {
    private FragmentSignUpBinding binding;

    private ToolTipsManager tooltipsManager;
    private ApiService apiService;
    boolean usernameTaken = false;
    TakenInfo takenInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        apiService = ApiClient.getClient(getContext());
        Call<TakenInfo> infoCall = apiService.getTakenInfo();
        infoCall.enqueue(new Callback<TakenInfo>() {
            @Override
            public void onResponse(Call<TakenInfo> call, Response<TakenInfo> response) {
                if(response.isSuccessful()){
                    takenInfo = response.body();
                    Log.d("api", takenInfo.toString());
                }
            }

            @Override
            public void onFailure(Call<TakenInfo> call, Throwable t) {

            }
        });
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tooltipsManager = new ToolTipsManager();
        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, String> signUpData = new HashMap<>();
                signUpData.put("username", binding.username.getText().toString());
                signUpData.put("email", binding.email.getText().toString());
                signUpData.put("password", binding.password.getText().toString());
                Call<Integer> signUpCall = apiService.signup(signUpData);
                signUpCall.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if(response.isSuccessful()){
                            int userID = response.body();
                            Bundle bundle = new Bundle();
                            bundle.putInt("userID", userID);
                            NavOptions navOptions = new NavOptions.Builder()
                                    .setPopUpTo(R.id.signUpFragment, true) // Clear the back stack up to the loginFragment (inclusive)
                                    .build();
                            Navigation.findNavController(view).navigate(R.id.action_signUp_to_home, bundle, navOptions);

                        }
                        else{
                            // user or email taken
                            Log.d("api", "bad info");
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        Log.e("api", t.getMessage());
                    }
                });


            }
        });

        ToolTip usernameTakenTip = new ToolTip.Builder(requireContext(), binding.username, binding.getRoot(), "Username Taken", ToolTip.POSITION_ABOVE).build();
        binding.username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(takenInfo.usernames.contains(s.toString())){
                    tooltipsManager.show(usernameTakenTip);
                }
                else
                    tooltipsManager.findAndDismiss(binding.username);
            }
        });
        ToolTip emailInvalidTip = new ToolTip.Builder(requireContext(), binding.email, binding.getRoot(), "This Email Address doesn't exist", ToolTip.POSITION_ABOVE).build();
        ToolTip emailTakenTip = new ToolTip.Builder(requireContext(), binding.email, binding.getRoot(), "There Is Already An Account Using This Email", ToolTip.POSITION_ABOVE).build();
        binding.email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(takenInfo.emails.contains(s.toString()))
                    tooltipsManager.show(emailTakenTip);
                else
                    tooltipsManager.findAndDismiss(binding.email);
            }
        });
        binding.email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String text = binding.email.getText().toString();
                if(!hasFocus && !isValidEmail(text))
                    tooltipsManager.show(emailInvalidTip);
                else if(!hasFocus && isValidEmail(text) && !takenInfo.emails.contains(text))
                    tooltipsManager.findAndDismiss(binding.email);
            }
        });
        binding.signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_signUp_to_login);
            }
        });

    }
    public boolean isValidEmail(String email){
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(regex);
    }
}