package com.example.soundsensev1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference sensorControlReference;
    private Switch controlSwitch;
    private Switch controlSwitch2;
    private SharedPreferencesHelper spHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spHelper = new SharedPreferencesHelper(this);

        //toolbar settings
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //control switch initialization
        controlSwitch = findViewById(R.id.controlSwitch);
        controlSwitch2 = findViewById(R.id.controlSwitch2);
        sensorControlReference = FirebaseDatabase.getInstance().getReference().child("Device").child("ON&OFF");

        setControlSwitchValue(); // Device ON/OFF switch
        setControlSwitch2Value(); //Notifications ON/OFF switch


    }

    protected void setControlSwitchValue(){
        sensorControlReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //read the current firebase value of device and convert it to int
                String controlString = snapshot.getValue().toString();
                int controlInt = Integer.parseInt(controlString);
                Log.i("Settings","control switch: "+controlInt);
                //set the switch to whatever the current firebase value is
                if(controlInt==0){
                    controlSwitch.setChecked(false);
                }else{
                    controlSwitch.setChecked(true);
                }

                //when user activates control switch, set firebase value to 1 which will allow sensor to send values
                //otherwise it will be turned off and it wont send values

                controlSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(controlSwitch.isChecked()){
                            sensorControlReference.setValue(1);
                        }
                        else{
                            sensorControlReference.setValue(0);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsActivity.this, "Control Switch error!", Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void setControlSwitch2Value(){
        if(spHelper.getNotification()==false){
            controlSwitch2.setChecked(false);
        }else{
            controlSwitch2.setChecked(true);
        }


        controlSwitch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(controlSwitch2.isChecked()){
                    spHelper.setNotification(true);
                }
                else{
                    spHelper.setNotification(false);
                }
            }
        });
    }
}