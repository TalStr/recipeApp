package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.databinding.ActivityLoginBinding;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {
    private DBHelper myDB;
    private ActivityLoginBinding binding;
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myDB = DBHelper.getInstance(this);
        apiService = ApiClient.getClient(getApplicationContext());

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
                            int userID = response.body();
                        }
                        else{
                            //ivalid login info
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {

                    }
                });

//                String name = binding.username.getText().toString();
//                String pass = binding.password.getText().toString();
//                if(myDB.validLoginInfo(name, pass)){
//                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                    intent.putExtra("userID", myDB.getIDbyUsername(name));
//                    startActivity(intent);
//                }

            }
        });
        interface RequestLogin{
            @POST("/login")
            Call<Boolean> login(@Body HashMap<String, String> loginData);
        }





        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}