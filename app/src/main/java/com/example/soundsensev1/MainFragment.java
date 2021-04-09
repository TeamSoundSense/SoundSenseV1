package com.example.soundsensev1;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class MainFragment extends Fragment {

    private TextView welcomeTextView;
    private Button mainButton;

    private static FirebaseUser user;
    private static String userID;

    private static DatabaseReference sensorControlReference;
    private static DatabaseReference reference;
    private static DatabaseReference userReference;
    private static DatabaseReference inputSensorReference;
    private static DatabaseReference analogReference;
    private SharedPreferencesHelper spHelper;

    private boolean buttonOn;


    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    private static final String TAG = "MainActivity";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.main_fragment, container, false);

        Toolbar toolbar = root.findViewById(R.id.mainToolbar);
        getActivity().setActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Home");

        welcomeTextView = root.findViewById(R.id.welcomeTextView);
        mainButton = root.findViewById(R.id.mainButton);
        spHelper = new SharedPreferencesHelper(getActivity());


        //firebase authentication
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        if(user!=null) {
            userID = user.getUid();
        }

        //reference to firebase to retrieve input sensor data
        inputSensorReference = FirebaseDatabase.getInstance().getReference().child("Sensor");
        analogReference = FirebaseDatabase.getInstance().getReference().child("Sensor").child("Analog");

        //button settings
        userReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("sensorValues");
        sensorControlReference = FirebaseDatabase.getInstance().getReference().child("Device").child("ON&OFF");

        //controls for the button
        analogReference.setValue(0);

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
                Toast.makeText(getActivity(),"User info error!",Toast.LENGTH_LONG).show();
            }
        });

        return root;
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
                Toast.makeText(getActivity(), "Button error!", Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void displayWarning() {

        //retrieve sensorvalues for specific user from firebase
        //display warnings values in activity button
        analogReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int currentSensorValue = Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString());

                new CountDownTimer(2000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        if (currentSensorValue!=0){
                            mainButton.setText("Too loud!\n"+currentSensorValue);
                            mainButton.setBackgroundResource(R.drawable.circular_button_red);
                        }
                    }
                    public void onFinish() {
                        mainButton.setText("All Good!");
                        mainButton.setBackgroundResource(R.drawable.circular_button_green);
                    }
                }.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Warning error!", Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void buttonOFF(){
        mainButton.setText("Tap to\nturn\nON");
        mainButton.setBackgroundResource(R.drawable.circular_button);
        buttonOn = false;
    }

    protected void buttonON(){
        displayWarning();
        mainButton.setText("All good!");
        mainButton.setBackgroundResource(R.drawable.circular_button_green);
        buttonOn = true;
    }

    protected void storeUserSensorValues() {
        //reference to firebase to retrieve sensor data
        analogReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //timeStamp initialization
                calendar = Calendar.getInstance();
                dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                date = dateFormat.format(calendar.getTime());
                String dateString = "    \nTime: "+ date;

                String value = Objects.requireNonNull(snapshot.getValue()).toString();

                if(spHelper.getRecentSensorValue()==null){
                    spHelper.setRecentSensorValue("0");
                    Log.i("MainActivity", "recent value: " + spHelper.getRecentSensorValue());
                }

                //if sensor value isnt the same as recent value, upload the value to the firebase database
                if (!spHelper.getRecentSensorValue().equals(value) && Integer.parseInt(value)!=0) {
                    userReference.push().setValue("Volume Level: "+value+dateString);

                    //store recent sensor value in shared prefs
                    spHelper.setRecentSensorValue(value);
                    Log.i("MainActivity", "recent value: " + spHelper.getRecentSensorValue());

                    //start service to send notification
                    if(spHelper.getNotification()){
                        startService();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Sensor Value error!", Toast.LENGTH_LONG).show();
            }
        });

    }

    /*
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
     */

    //method to start foreground service for sending notifications
    protected void startService(){
        Intent serviceIntent = new Intent(getActivity(), MyService.class);
        ContextCompat.startForegroundService(getActivity(),serviceIntent);
    }

    protected void stopService(){
        Intent serviceIntent = new Intent(getActivity(), MyService.class);
    }
}
