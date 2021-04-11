package com.example.soundsensev1;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener{

    /*
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
     */

    private Calendar calendar;
    private SharedPreferencesHelper spHelper;

    private static final int POS_CLOSE = 0;
    private static final int POS_BUTTON = 1;
    private static final int POS_MY_PROFILE = 2;
    private static final int POS_DATA = 3;
    private static final int POS_SETTINGS = 4;
    private static final int POS_LOGOUT = 6;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    private static DatabaseReference userCountReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userCountReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("thresholdCounts");

        spHelper = new SharedPreferencesHelper(this);

        //toolbar settings
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SoundSense");

        resetThresholdCounts();

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withDragDistance(180)
                .withRootViewScale(0.75f)
                .withRootViewElevation(25)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();
        
        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_CLOSE),
                createItemFor(POS_BUTTON).setChecked(true),
                createItemFor(POS_MY_PROFILE),
                createItemFor(POS_DATA),
                createItemFor(POS_SETTINGS),
                new SpaceItem(260),
                createItemFor(POS_LOGOUT)
        ));

        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.drawer_list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_BUTTON);

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_baseline_menu_24);
        toolbar.setOverflowIcon(drawable);

    }

    private DrawerItem createItemFor(int position){
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.white))
                .withTextTint(color(R.color.white))
                .withSelectedIconTint(color(R.color.white))
                .withSelectedTextTint(color(R.color.white));
    }

    @ColorInt
    private int color(@ColorRes int res){
        return ContextCompat.getColor(this, res);
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.id_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.id_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++){
            int id = ta.getResourceId(i, 0);
            if (id != 0){
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }

        ta.recycle();
        return icons;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onItemSelected(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (position == POS_BUTTON){
            MainFragment mainFragment = new MainFragment();
            transaction.replace(R.id.container, mainFragment);
        }

        else if (position == POS_MY_PROFILE){
            ProfileFragment profileFragment = new ProfileFragment();
            transaction.replace(R.id.container, profileFragment);
        }

        else if (position == POS_DATA){
            DataFragment dataFragment = new DataFragment();
            transaction.replace(R.id.container, dataFragment);
        }

        else if (position == POS_SETTINGS){
            SettingFragment settingFragment = new SettingFragment();
            transaction.replace(R.id.container, settingFragment);
        }

        else if (position == POS_LOGOUT){
            FirebaseAuth.getInstance().signOut();
            //edit shared preferences to set activity_executed to false
            spHelper.setUserLogIn(false);
            goToLoginActivity();
        }

        slidingRootNav.closeMenu();
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected void goToLoginActivity(){
        Intent intent = new Intent (this,LoginActivity.class);
        startActivity(intent);
    }

    public void resetThresholdCounts(){
        calendar = Calendar.getInstance();
        int checkSecond = calendar.get(Calendar.SECOND);
        int checkHour = calendar.get(Calendar.MINUTE);
        int checkDay = calendar.get(Calendar.HOUR_OF_DAY);
        Intent intent = new Intent();
        if(checkSecond == 0){
            userCountReference.child("minuteCount").setValue(0);
            spHelper.setMinuteThresholdCount(0);
            intent.putExtra("minuteTV","0");
        }

        if(checkHour == 0){
            userCountReference.child("hourCount").setValue(0);
            spHelper.setHourlyThresholdCount(0);
            intent.putExtra("hourTV","0");
        }

        if(checkDay == 0){
            userCountReference.child("dayCount").setValue(0);
            spHelper.setDailyThresholdCount(0);
            intent.putExtra("dayTV","0");
        }

        refreshThresholdCounts(1000);
    }

    private void refreshThresholdCounts(int milliseconds) {

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                resetThresholdCounts();
            }
        };

        handler.postDelayed(runnable, milliseconds);



        /*
        //minute timers
        int millisInAMinute = 60000;
        long time = System.currentTimeMillis();
        long timerCount = millisInAMinute - (time % millisInAMinute);
        Log.i("countseconds", "Initial timer count: " + String.valueOf(timerCount));
        Intent intent = new Intent();
        CountDownTimer timer2 = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                userCountReference.child("minuteCount").setValue(0);
                spHelper.setMinuteThresholdCount(0);
                intent.putExtra("minuteTV","0");
                //minuteCountTV.setText("0");
                Log.i("countseconds", "Minute count reset: 60000");
                this.start();
            }
        };

        CountDownTimer timer1 = new CountDownTimer(timerCount, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                userCountReference.child("minuteCount").setValue(0);
                spHelper.setMinuteThresholdCount(0);
                //minuteCountTV.setText("0");
                intent.putExtra("minuteTV","0");
                Log.i("countseconds", "Hour count reset: ");
                timer2.start();
            }
        }.start();

        //hour timers

        int millisInAnHour = 3600000;
        long timerCount2 = millisInAnHour - (time % millisInAnHour);

        CountDownTimer timer4 = new CountDownTimer(3600000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                userCountReference.child("hourCount").setValue(0);
                spHelper.setHourlyThresholdCount(0);
                //minuteCountTV.setText("0");
                intent.putExtra("hourTV","0");
                Log.i("countseconds", "Hour count reset: ");
                this.start();
            }
        }.start();

        CountDownTimer timer3 = new CountDownTimer(timerCount2, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                userCountReference.child("hourCount").setValue(0);
                spHelper.setHourlyThresholdCount(0);
                //minuteCountTV.setText("0");
                intent.putExtra("hourTV","0");
                Log.i("countseconds", "Hour count reset: ");
                timer4.start();

            }
        }.start();

        //Day timers

        int millisInADay = 86400000;
        long timerCount3 = millisInADay - (time % millisInADay);

        CountDownTimer timer6 = new CountDownTimer(3600000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                userCountReference.child("dayCount").setValue(0);
                spHelper.setDailyThresholdCount(0);
                //minuteCountTV.setText("0");
                intent.putExtra("dayTV","0");
                Log.i("countseconds", "Day count reset: ");
                this.start();
            }
        }.start();

        CountDownTimer timer5 = new CountDownTimer(timerCount3, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                userCountReference.child("dayCount").setValue(0);
                spHelper.setDailyThresholdCount(0);
                //minuteCountTV.setText("0");
                intent.putExtra("dayTV","0");
                Log.i("countseconds", "Day count reset: ");
                timer6.start();
            }
        }.start();

         */
    }
    /*

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
                Toast.makeText(MainActivity.this, "Warning error!", Toast.LENGTH_LONG).show();
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
    */
}