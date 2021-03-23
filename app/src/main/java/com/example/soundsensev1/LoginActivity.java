package com.example.soundsensev1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class  LoginActivity extends AppCompatActivity {

    private TextView register,forgotPassword;
    private EditText editTextEmail,editTextPassword;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private ProgressBar loginProgressBar;

    private SharedPreferencesHelper spHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initializing components
        editTextEmail = findViewById(R.id.editTextLoginEmail);
        editTextPassword = findViewById(R.id.editTextLoginPassword);
        loginButton = findViewById(R.id.loginButton);
        register = findViewById(R.id.textViewRegister);
        forgotPassword = findViewById(R.id.textViewfForgotPassword);
        loginProgressBar = findViewById(R.id.loginProgressBar);

        spHelper = new SharedPreferencesHelper(this);
        mAuth = FirebaseAuth.getInstance();

        //login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        //Register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegistrationActivity();
            }
        });

        //forgot password button
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPasswordActivity();
            }
        });

    }

    protected void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //check if fields are empty
        if(email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        //check if email is valid
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid email.");
            editTextEmail.requestFocus();
            return;
        }
        //check if password length is sufficient
        if(password.length() < 6){
            editTextPassword.setError("Password must be at least 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        loginProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    //edit shared preferences to set activity_executed to true
                    spHelper.setUserLogIn(true);
                    //redirect to main activity
                    goToMainActivity();

                }else{
                    Toast.makeText(LoginActivity.this,"Failed to login! Please check your credentials. ",Toast.LENGTH_LONG).show();
                }
                loginProgressBar.setVisibility(View.GONE);
            }
        });

    }

    protected void goToRegistrationActivity(){
        Intent intent = new Intent (this,RegistrationActivity.class);
        startActivity(intent);
    }

    protected void goToMainActivity(){
        Intent intent = new Intent (this,MainActivity.class);
        startActivity(intent);
    }

    protected void goToPasswordActivity(){
        Intent intent = new Intent (this,PasswordActivity.class);
        startActivity(intent);
    }


}