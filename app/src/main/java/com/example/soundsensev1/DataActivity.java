package com.example.soundsensev1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
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
    private DatabaseReference sensorReference;
    private DatabaseReference userReference;
    private DatabaseReference userSensorReference;
    private String userID;
    private TextView tv5;
    private ArrayList<String> inputSensorValues = new ArrayList<>();
    private ArrayList<String> fbSensorValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        //toolbar settings
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Analysis");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //load sensor value
        sensorListView = findViewById(R.id.sensorListView);
        userReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("sensorValues");
        storeUserSensorValues();
        printUserSensorValues();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    protected void storeUserSensorValues() {

        //reference to firebase to retrieve sensor data
        sensorReference = FirebaseDatabase.getInstance().getReference().child("Sensor");
        sensorReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.child("Analog").getValue().toString();
                //add sensor value to an arraylist
                inputSensorValues.add(value);
                //upload array list to firebase database
                userReference.setValue(inputSensorValues);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DataActivity.this, "Sensor Value error!", Toast.LENGTH_LONG).show();
            }
        });

    }

    protected void printUserSensorValues(){

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fbSensorValues);

        //retrieve sensorvalues for specific user from firebase
        //display these values in a list view
        userReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String currentSensorValue = snapshot.getValue(String.class);
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


}
