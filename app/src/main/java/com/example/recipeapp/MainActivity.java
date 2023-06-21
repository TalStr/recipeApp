package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getClient(this);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("notifs", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d("notifs", "Token: " + token);

                        // Check if it's the first time the app has been opened
                        if (isFirstTime()) {
                            sendRegistrationToServer(token);
                        }
                    }
                });

    }
    private boolean isFirstTime() {
        SharedPreferences preferences = getSharedPreferences("com.example.recipeapp", MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.apply();
        }
        return !ranBefore;
    }
    private void sendRegistrationToServer(String token) {
        Log.d("api", "Sending registration to server with token: " + token);

        Call<Void> newDevice = apiService.addDevice(token);
        newDevice.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("api", "Registration request successful");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("api", "Registration request failed: " + t.getMessage());
            }
        });
    }

}