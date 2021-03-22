package com.example.soundsensev1;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);   //same code used to open file
    }

    public void setUserLogIn(Boolean login) {
        SharedPreferences.Editor edt = sharedPreferences.edit();
        edt.putBoolean("activity_executed", login);
        edt.commit();
    }

    public Boolean isUserLoggedIn(){
        return sharedPreferences.getBoolean("activity_executed",false);
    }

    public void setRecentSensorValue(String value){
        SharedPreferences.Editor edt = sharedPreferences.edit();
        edt.putString("recent_sensor_value", value);
        edt.commit();
    }

    public String getRecentSensorValue(){
        return sharedPreferences.getString("recent_sensor_value",null);
    }






}
