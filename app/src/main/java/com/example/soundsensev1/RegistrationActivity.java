package com.example.soundsensev1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity {

    private Button registerButton;
    private EditText editTextName,editTextEmail,editTextPassword;
    private ProgressBar registerProgressBar;

    private FirebaseAuth mAuth;

    private FirebaseHelper fHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //mAuth = FirebaseAuth.getInstance();

        fHelper = new FirebaseHelper(this);

        //initializing components
        registerButton = findViewById(R.id.registerButton);
        editTextName = findViewById(R.id.editTextCreateName);
        editTextEmail = findViewById(R.id.editTextCreateEmail);
        editTextPassword = findViewById(R.id.editTextCreatePassword);
        registerProgressBar = findViewById(R.id.registerProgressBar);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }

    private void registerUser() {

        //convert user inputs to strings
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //check if fields are empty
        if(name.isEmpty()){
            editTextName.setError("Name is required!");
            editTextName.requestFocus();
            return;
        }

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

        //add user to firebase and check if task has been completed
        registerProgressBar.setVisibility(View.VISIBLE);

        if (fHelper.createFirebaseUser(name,email,password)){
            goToLoginActivity();
        }
        registerProgressBar.setVisibility(View.GONE);

    }

    protected void goToLoginActivity(){
        Intent intent = new Intent (this,LoginActivity.class);
        startActivity(intent);
    }
}