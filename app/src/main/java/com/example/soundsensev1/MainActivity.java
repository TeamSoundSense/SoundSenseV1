package com.example.soundsensev1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private Button mainButton;

    private FirebaseUser user;
    private String userID;

    private DatabaseReference sensorControlReference;
    private DatabaseReference reference;
    private DatabaseReference userReference;
    private DatabaseReference inputSensorReference;
    private SharedPreferencesHelper spHelper;

    private boolean buttonOn;

    //timer
    private static final long START_TIME_IN_MILLIS = 4000;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private boolean isTimerRunning;

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
        spHelper = new SharedPreferencesHelper(this);


        //firebase authentication
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        if(user!=null) {
            userID = user.getUid();
        }

        //reference to firebase to retrieve input sensor data
        inputSensorReference = FirebaseDatabase.getInstance().getReference().child("Sensor");

        //button settings
        userReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("sensorValues");
        sensorControlReference = FirebaseDatabase.getInstance().getReference().child("Device").child("ON&OFF");

        storeUserSensorValues();
        setButtonValue();

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

    protected void setButtonValue(){

        mainButton.setText("Connecting\n...");

        sensorControlReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //read the current firebase value of device and convert it to int
                String controlString = snapshot.getValue().toString();
                int controlInt = Integer.parseInt(controlString);
                Log.i("Settings","control switch: "+controlInt);

                //set the button to whatever the current firebase value is
                if(controlInt==0){
                    buttonOFF();

                }else{
                    buttonON();
                }

                //user can turn on or off using the button depending on the firebase value
                mainButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(buttonOn){
                            sensorControlReference.setValue(0);
                            buttonOFF();
                        }
                        else{
                            sensorControlReference.setValue(1);
                            buttonON();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Button error!", Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void displayWarning(String currentSensorValue) {
        //retrieve sensorvalues for specific user from firebase
        Log.i("main activity", "recent value from fb1: " + currentSensorValue);
        CountDownTimer myTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                mainButton.setText("Too loud!\n" + currentSensorValue);
                mainButton.setBackgroundResource(R.drawable.circular_button_red);
            }
            public void onFinish() {
                mainButton.setText("All good :)");
                mainButton.setBackgroundResource(R.drawable.circular_button_green);
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
            }
        };
        myTimer.start();

    }


    protected void buttonOFF(){
        mainButton.setText("Tap to\nturn\nON");
        mainButton.setBackgroundResource(R.drawable.circular_button);
        buttonOn = false;
    }

    protected void buttonON(){
        //displayWarning();
        mainButton.setText("All good :)");
        mainButton.setBackgroundResource(R.drawable.circular_button_green);
        buttonOn = true;
    }

    protected void storeUserSensorValues() {

        //reference to firebase to retrieve sensor data
        inputSensorReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.child("Analog").getValue().toString();
                if(spHelper.getRecentSensorValue()==null){
                    spHelper.setRecentSensorValue("0");
                    Log.i("Main activity", "recent value: " + spHelper.getRecentSensorValue());
                }

                //if sensor value isnt the same as recent value, upload the value to the firebase database
                if(spHelper.getRecentSensorValue().equals(value)==false) {
                    userReference.push().setValue(value);
                    //store recent sensor value in shared prefs
                    spHelper.setRecentSensorValue(value);
                    Log.i("Main activity", "recent value: " + spHelper.getRecentSensorValue());
                    displayWarning(value);
                    //start service to send notification
                    startService();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Sensor Value error!", Toast.LENGTH_LONG).show();
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

    //method to start foreground service for sending notifications
    protected void startService(){
        Intent serviceIntent = new Intent(this, MyService.class);
        ContextCompat.startForegroundService(this,serviceIntent);
    }

    protected void stopService(){
        Intent serviceIntent = new Intent(this, MyService.class);
    }


}