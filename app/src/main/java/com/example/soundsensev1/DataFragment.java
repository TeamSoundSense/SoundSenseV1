package com.example.soundsensev1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DataFragment extends Fragment {

    protected TextView helpText;
    protected ImageView questionMark;
    protected ListView sensorListView;
    private DatabaseReference inputSensorReference;
    private DatabaseReference userReference;
    private ArrayList<String> fbSensorValues = new ArrayList<>();

    private SharedPreferencesHelper spHelper;

    private Button deleteListButton;
    ArrayAdapter<String> adapter;

    private int option = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup)inflater.inflate(R.layout.data_fragment, container, false);

        Toolbar toolbar = root.findViewById(R.id.mainToolbar);
        getActivity().setActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Analysis");

        //shared preferences
        spHelper = new SharedPreferencesHelper(getActivity());

        //adapter
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_simple_list_item_1, fbSensorValues);

        //load sensor value
        sensorListView = root.findViewById(R.id.sensorListView);
        //reference to firebase for user sensor data
        userReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("sensorValues");
        //reference to firebase to retrieve input sensor data
        inputSensorReference = FirebaseDatabase.getInstance().getReference().child("Sensor");

        printUserSensorValues();

        helpText = root.findViewById(R.id.help_text);

        questionMark = root.findViewById(R.id.question_mark);
        questionMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //DataHelpDialogFragment dataHelpDialogFragment = new DataHelpDialogFragment();
                //dataHelpDialogFragment.show(getFragmentManager(), "DataHelpDialogFragment");

                if(option == 0){
                    helpText.setVisibility(View.VISIBLE);
                    option = 1;
                }
                else if (option == 1){
                    helpText.setVisibility(View.GONE);
                    option = 0;
                }
            }
        });

        deleteListButton = root.findViewById(R.id.deleteListButton);
        deleteListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userReference.removeValue();
                adapter.notifyDataSetChanged();
                adapter.clear();
            }
        });

        return root;
    }

    @Override
    public void onResume() {
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
                Toast.makeText(getActivity(), "Sensor Value error!", Toast.LENGTH_LONG).show();
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
        Intent serviceIntent = new Intent(getActivity(), MyService.class);
        ContextCompat.startForegroundService(getActivity(),serviceIntent);
    }

    protected void stopService(){
        Intent serviceIntent = new Intent(getActivity(), MyService.class);
    }
}
