package com.example.soundsensev1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private Button saveButton;

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
        editPassword = findViewById(R.id.editProfilePassword);
        saveButton = findViewById(R.id.saveButton);

        //By default have the editTexts not be editable by user
        editName.setEnabled(false);
        editEmail.setEnabled(false);
        editPassword.setEnabled(false);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.editMode:
                enterEditMode();
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
        editPassword.setEnabled(true);

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
        editPassword.setEnabled(false);
        saveButton.setVisibility(View.INVISIBLE);
    }
}