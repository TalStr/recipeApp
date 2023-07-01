package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
    public static final String SHARED_PREFS = "sharedPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getClient(this);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        int userID = sharedPreferences.getInt("userID", -1);
        String username = sharedPreferences.getString("username", null);
        String profilepic = sharedPreferences.getString("profilePic", null);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);

        if(navHostFragment != null){
            new Handler().post(() -> {
                NavController navController = navHostFragment.getNavController();
                NavInflater navInflater = navController.getNavInflater();
                NavGraph graph = navInflater.inflate(R.navigation.nav_graph);
                if(userID != -1 && username != null && profilepic != null){
                    CurrentUser.getInstance().setInfo(userID, username, profilepic);
                    graph.setStartDestination(R.id.homeFragment);
                }
                else
                    graph.setStartDestination(R.id.loginFragment);
                navController.setGraph(graph);
            });
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("notifs", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        // Check if it's the first time the app has been opened
                        if (isFirstTime())
                            sendRegistrationToServer(token);
                    }
                });
    }
    private boolean isFirstTime() {
        SharedPreferences preferences = getSharedPreferences("com.example.recipeapp", MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.apply();
        }
        return !ranBefore;
    }
    private void sendRegistrationToServer(String token) {
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