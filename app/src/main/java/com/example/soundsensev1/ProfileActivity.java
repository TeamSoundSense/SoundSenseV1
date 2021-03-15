package com.example.soundsensev1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editEmail;
    private Button saveButton;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //toolbar settings
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initializing editTexts and button
        editName = findViewById(R.id.editProfileName);
        editEmail = findViewById(R.id.editProfileEmail);
        saveButton = findViewById(R.id.saveButton);

        //By default have the editTexts not be editable by user
        editName.setEnabled(false);
        editEmail.setEnabled(false);

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
                    String email = userProfile.email;

                    editName.setText(name);
                    editEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this,"User info error!",Toast.LENGTH_LONG).show();

            }
        });

        exitEditMode();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.editItem:
                enterEditMode();
                return true;

            case R.id.logoutItem:
                logoutUser();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void enterEditMode(){
        //reveal the save button
        saveButton.setVisibility(View.VISIBLE);
        //when editMode is active, allow user to input in editTexts
        //By default have the editTexts not be editable by user
        editName.setEnabled(true);
        editEmail.setEnabled(true);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG);
                exitEditMode();
            }
        });
    }

    protected void exitEditMode(){
        //when we exit editMode, we return to displayMode
        //make editTexts uneditable
        //hide the save button
        editName.setEnabled(false);
        editEmail.setEnabled(false);
        saveButton.setVisibility(View.INVISIBLE);
    }

    protected void logoutUser(){
        FirebaseAuth.getInstance().signOut();
        //edit shared preferences to set activity_executed to false
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        edt.putBoolean("activity_executed", false);
        edt.commit();
        goToLoginActivity();
    }

    protected void goToLoginActivity(){
        Intent intent = new Intent (this,LoginActivity.class);
        startActivity(intent);
    }
}