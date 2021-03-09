package com.example.soundsensev1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DataActivity extends AppCompatActivity {

    protected ListView sensorListView;
    protected List<String> sensorList;
    private DatabaseReference reference;
    private String userID;
    private TextView tv5;

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
        tv5 = findViewById(R.id.textView5);
        loadSensorValues();
    }

    protected void loadSensorValues() {

        reference = FirebaseDatabase.getInstance().getReference().child("Sensor");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.child("Analog").getValue().toString();
                tv5.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DataActivity.this, "Sensor Value error!", Toast.LENGTH_LONG).show();
            }
        });

        /*
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sensorList);
        sensorListView.setAdapter(adapter);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sensorList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    sensorList.add(snapshot1.getValue().toString());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DataActivity.this,"Sensor Value error!",Toast.LENGTH_LONG).show();
            }
        });

         */
    }

}
