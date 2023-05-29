package com.example.recipeapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.recipeapp.databinding.ActivitySignupBinding;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

public class SignupActivity extends AppCompatActivity {
    private DBHelper myDB;
    private ActivitySignupBinding binding;
    private ToolTipsManager tooltipsManager;

    boolean usernameShort = false;
    boolean usernameTaken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        tooltipsManager = new ToolTipsManager();
        myDB = DBHelper.getInstance(getApplicationContext());
        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.username.getText().toString();
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString();
                boolean success = myDB.insertUser(username,email,password);
                if(success)
                {
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(SignupActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });

        binding.signUpTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_login_to_signUp);
            }
        });
        ToolTip usernameTakenTip = new ToolTip.Builder(SignupActivity.this, binding.username, binding.getRoot(), "Username Taken", ToolTip.POSITION_ABOVE).build();
        ToolTip usernameShortTip = new ToolTip.Builder(SignupActivity.this, binding.username, binding.getRoot(), "Username Must Be At least 6 Characters Long", ToolTip.POSITION_ABOVE).build();
        binding.username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (myDB.usernameTaken(s.toString()) && !usernameTaken) {
                    tooltipsManager.show(usernameTakenTip);
                    usernameTaken = true;
                }
                else {
                    tooltipsManager.findAndDismiss(binding.username);
                    usernameTaken=false;
                }
            }
        });
        binding.username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String text = binding.username.getText().toString();
                    if(text.length() < 6 && text.length() != 0)
                        tooltipsManager.show(usernameShortTip);
                }
            }
        });
        ToolTip emailInvalidTip = new ToolTip.Builder(SignupActivity.this, binding.email, binding.getRoot(), "This Email Address doesn't exist", ToolTip.POSITION_ABOVE).build();
        ToolTip emailTakenTip = new ToolTip.Builder(SignupActivity.this, binding.email, binding.getRoot(), "There Is Already An Account Using This Email", ToolTip.POSITION_ABOVE).build();

        binding.email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String text = binding.email.getText().toString();
                if(hasFocus)
                    tooltipsManager.findAndDismiss(binding.email);
                else
                {
                    if(!isValidEmail(text) && text.length() > 0){
                        tooltipsManager.show(emailInvalidTip);
                    }
                    if(myDB.emailTaken(binding.email.getText().toString()))
                        tooltipsManager.show(emailTakenTip);
                }
            }
        });

    }
    public boolean isValidEmail(String email){
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(regex);
    }
}