package com.example.recipeapp.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.recipeapp.CurrentUser;
import com.example.recipeapp.MainActivity;
import com.example.recipeapp.R;
import com.example.recipeapp.api.ApiClient;
import com.example.recipeapp.api.ApiService;
import com.example.recipeapp.databinding.FragmentAccountManagerBinding;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountManagerFragment extends Fragment {
    FragmentAccountManagerBinding binding;
    ApiService apiService;
    int userID;
    Bitmap bitmap;
    ActivityResultLauncher<Intent> launcher=
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
                if(result.getResultCode()==RESULT_OK){
                    Uri uri=result.getData().getData();
                    try{
                        InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(bitmap != null){
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                        byte[] bytes = byteArrayOutputStream.toByteArray();
                        final String base64Image = Base64.encodeToString(bytes, Base64.DEFAULT);

                        HashMap<String, String> map = new HashMap<>();
                        map.put("picture", base64Image);
                        Call<Void> call = apiService.setProfilePic(userID, map);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Log.d("api", "Upload successful. Response code: " + response.code());
                                    CurrentUser.getInstance().setProfilePic(base64Image);
                                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE).edit();
                                    editor.putString("profilePic", base64Image);
                                    editor.apply();
                                    binding.profilePic.setImageBitmap(CurrentUser.getInstance().getProfilePic());
                                } else {
                                    Log.e("api", "Upload failed. Response code: " + response.code() + ", error message: " + response.errorBody().toString());
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e("api", "Upload failed due to network error or exception: " + t.getMessage());
                            }
                        });

                    }
                } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                    Log.e("api", ImagePicker.Companion.getError(result.getData()));
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountManagerBinding.inflate(inflater, container, false);
        apiService = ApiClient.getClient(getContext());
        userID = CurrentUser.getInstance().getUserID();
        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.profilePic.setImageBitmap(CurrentUser.getInstance().getProfilePic());
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Logout", "Logout clicked");

                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("FirebaseMessaging", "Failed to retrieve token");
                                    return;
                                }

                                String token = task.getResult();
                                Call<Void> unbindDevice = apiService.unbindDevice(token);
                                unbindDevice.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        Log.d("Logout", "unbindDevice onResponse");

                                        if(response.isSuccessful()){
                                            Log.d("Logout", "unbindDevice successful");

                                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putInt("userID", -1);
                                            editor.putString("username", null);
                                            editor.putString("profilePic", null);
                                            editor.apply();

                                            NavController navController = Navigation.findNavController(view);
                                            navController.popBackStack(R.id.nav_graph, true);
                                            navController.navigate(R.id.loginFragment);
                                        } else {
                                            Log.e("Logout", "unbindDevice failed: " + response.message());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Log.e("Logout", "unbindDevice request failed: " + t.getMessage());
                                    }
                                });
                            }
                        });
            }
        });
        binding.changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ImagePicker.with(requireActivity())
                        .galleryOnly()
                        .crop()
                        .cropSquare()
                        .maxResultSize(512, 512, true)
                        .createIntent();
                launcher.launch(intent);
            }
        });
    }
}