package com.example.soundsensev1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DataActivity extends AppCompatActivity {

    protected ListView sensorListView;
    private DatabaseReference inputSensorReference;
    private DatabaseReference userReference;
    private ArrayList<String> fbSensorValues = new ArrayList<>();

    private SharedPreferencesHelper spHelper;

    private Button deleteListButton;
    ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        //toolbar settings
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Analysis");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //shared preferences
        spHelper = new SharedPreferencesHelper(this);

        //adapter
        adapter = new ArrayAdapter<String>(this, R.layout.custom_simple_list_item_1, fbSensorValues);

        //load sensor value
        sensorListView = findViewById(R.id.sensorListView);
        //reference to firebase for user sensor data
        userReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("sensorValues");
        //reference to firebase to retrieve input sensor data
        inputSensorReference = FirebaseDatabase.getInstance().getReference().child("Sensor");

        printUserSensorValues();

        deleteListButton = findViewById(R.id.deleteListButton);
        deleteListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userReference.removeValue();
                adapter.notifyDataSetChanged();
                adapter.clear();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    protected void storeUserSensorValues() {

        //reference to firebase to retrieve sensor data
        inputSensorReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.child("Analog").getValue().toString();
                if(spHelper.getRecentSensorValue()==null){
                    spHelper.setRecentSensorValue("0");
                    Log.i("data activity", "recent value: " + spHelper.getRecentSensorValue());
                }

                //if sensor value isnt the same as recent value, upload the value to the firebase database
                if(spHelper.getRecentSensorValue().equals(value)==false) {
                    userReference.push().setValue(value);
                    //store recent sensor value in shared prefs
                    spHelper.setRecentSensorValue(value);
                    Log.i("data activity", "recent value: " + spHelper.getRecentSensorValue());
                    //start service to send notification
                    startService();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DataActivity.this, "Sensor Value error!", Toast.LENGTH_LONG).show();
            }
        });

    }

    protected void printUserSensorValues(){

        //retrieve sensorvalues for specific user from firebase
        //display these values in a list view
        userReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String currentSensorValue = snapshot.getValue(String.class);
                Log.i("data activity", "recent value from fb: " + currentSensorValue);
                    fbSensorValues.add(currentSensorValue);
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        sensorListView.setAdapter(adapter);
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
