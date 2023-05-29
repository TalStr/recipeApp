package com.example.recipeapp.api;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://18.198.127.203:3000/";
    private static Retrofit retrofit = null;

    public static ApiService getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

            retrofit = builder.client(httpClient.build()).build();
        }
        return retrofit.create(ApiService.class);
    }
}
