package com.example.soundsensev1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private TextView welcomeTextView;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toolbar settings
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SoundSense");

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_baseline_menu_24);
        toolbar.setOverflowIcon(drawable);

        //login button
        loginButton = findViewById(R.id.button1);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });

        welcomeTextView = findViewById(R.id.welcomeTextView);

        //firebase authentication

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        //get user info from firebase

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String name = userProfile.name;

                    welcomeTextView.setText("Welcome, "+ name +"!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,"User info error!",Toast.LENGTH_LONG).show();

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.dataItem:
                goToDataActivity();
                return true;
            case R.id.settingsItem:
                goToSettingsActivity();
                return true;
            case R.id.profileItem:
                goToProfileActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void goToProfileActivity(){
        Intent intent = new Intent (this,ProfileActivity.class);
        startActivity(intent);
    }

    protected void goToSettingsActivity(){
        Intent intent = new Intent (this,SettingsActivity.class);
        startActivity(intent);
    }

    protected void goToDataActivity(){
        Intent intent = new Intent (this,DataActivity.class);
        startActivity(intent);
    }

    protected void goToLoginActivity(){
        Intent intent = new Intent (this,LoginActivity.class);
        startActivity(intent);
    }







}