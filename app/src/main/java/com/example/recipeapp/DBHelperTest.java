package com.example.recipeapp;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DBHelperTest {
    private static DBHelperTest instance;
    private ApiRequest apiRequest;
    private Gson gson;

    private DBHelperTest(Context context, String baseUrl) {
        apiRequest = new ApiRequest(baseUrl);
        gson = new Gson();
    }

    public static synchronized DBHelperTest getInstance(Context context, String baseUrl) {
        if (instance == null) {
            instance = new DBHelperTest(context.getApplicationContext(), baseUrl);
        }
        return instance;
    }

    public void isUsernameTaken(String username, final OnUsernameTakenListener listener) {
        apiRequest.get("/users/username/" + username, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String responseBody = response.body().string();
                boolean usernameTaken = gson.fromJson(responseBody, Boolean.class);
                listener.onResponse(usernameTaken);
            }
        });
    }

    // Add other methods to interact with your API

    public interface OnUsernameTakenListener {
        void onResponse(boolean usernameTaken);
    }

    public void areCredentialsValid(String username, String password, final OnCredentialsValidListener listener) {
    }
    public interface OnCredentialsValidListener {
        void onResponse(boolean credentialsValid);
    }


}
