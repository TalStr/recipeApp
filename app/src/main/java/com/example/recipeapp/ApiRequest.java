package com.example.recipeapp;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ApiRequest {
    private OkHttpClient client;
    private String baseUrl;

    public ApiRequest(String baseUrl) {
        this.client = new OkHttpClient();
        this.baseUrl = baseUrl;
    }

    public void get(String path, Callback callback) {
        HttpUrl url = HttpUrl.parse(baseUrl + path);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    // Add methods for other HTTP verbs (POST, PUT, DELETE) as needed
}
