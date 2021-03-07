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
import com.google.firebase.auth.FirebaseAuth;

public class PasswordActivity extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;
    private ProgressBar passwordProgressBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        emailEditText = findViewById(R.id.editTextEmailPassword);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        passwordProgressBar = findViewById(R.id.passwordProgressBar);

        auth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

    }

    protected void resetPassword(){
        String email = emailEditText.getText().toString().trim();

        //check if email field is empty
        if(email.isEmpty()){
            emailEditText.setError("Email is required!");
            emailEditText.requestFocus();
            return;
        }

        //check if email is valid
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Please provide valid email.");
            emailEditText.requestFocus();
            return;
        }

        passwordProgressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PasswordActivity.this,"Check your email to reset your password", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(PasswordActivity.this,"Error occured! Email not sent", Toast.LENGTH_LONG).show();
                }
                passwordProgressBar.setVisibility(View.GONE);
                goToLoginActivity();
            }
        });
    }

    protected void goToLoginActivity(){
        Intent intent = new Intent (this,LoginActivity.class);
        startActivity(intent);
    }
}