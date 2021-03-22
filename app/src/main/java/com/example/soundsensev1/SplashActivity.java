package com.example.soundsensev1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    //this activity determines whether the user is already sign in or not.
    //if signed in they are redirected to the mainActivity
    //if they aren't signed in they are redirected to the loginActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(this);

        if (spHelper.isUserLoggedIn()==true) {
            goToMainActivity();
        }else {
            goToLoginActivity();
        }

    }

    protected void goToMainActivity(){
        Intent intent = new Intent (this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    protected void goToLoginActivity(){
        Intent intent = new Intent (this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}