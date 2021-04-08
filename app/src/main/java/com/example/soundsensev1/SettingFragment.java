package com.example.soundsensev1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingFragment extends Fragment {

    private DatabaseReference sensorControlReference;
    private Switch controlSwitch;
    private Switch controlSwitch2;
    private SharedPreferencesHelper spHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.setting_fragment, container, false);

        Toolbar toolbar = root.findViewById(R.id.mainToolbar);
        getActivity().setActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Settings");

        spHelper = new SharedPreferencesHelper(getActivity());

        //control switch initialization
        controlSwitch = root.findViewById(R.id.controlSwitch);
        controlSwitch2 = root.findViewById(R.id.controlSwitch2);
        sensorControlReference = FirebaseDatabase.getInstance().getReference().child("Device").child("ON&OFF");

        setControlSwitchValue(); // Device ON/OFF switch
        setControlSwitch2Value(); //Notifications ON/OFF switch


        return root;
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
                Toast.makeText(getActivity(), "Control Switch error!", Toast.LENGTH_LONG).show();
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
