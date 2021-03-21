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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private Button mainButton;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    private int button_color;

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

        welcomeTextView = findViewById(R.id.welcomeTextView);
        mainButton = findViewById(R.id.mainButton);


        //firebase authentication
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        if(user!=null) {
            userID = user.getUid();
        }

        //button settings
        button_color = 0;
        mainButton.setText("Tap to\nturn\ngreen");
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button_color==0){
                    mainButton.setText("Tap to\nturn\nred");
                    mainButton.setBackgroundResource(R.drawable.circular_button_green);
                    button_color=1;
                }
                else if(button_color==1){
                    mainButton.setText("Tap to\nturn\ngrey");
                    mainButton.setBackgroundResource(R.drawable.circular_button_red);
                    button_color=2;
                }
                else {
                    mainButton.setText("Tap to\nturn\ngreen");
                    mainButton.setBackgroundResource(R.drawable.circular_button);
                    button_color=0;
                }
            }
        });

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