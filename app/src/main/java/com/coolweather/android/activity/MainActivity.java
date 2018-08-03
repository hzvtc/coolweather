package com.coolweather.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.coolweather.android.R;

/**
 * mipmap-ldpi 120dpi-160dpi 0.75
 mipmap-mdpi 160dpi-240dpi 1
 mipmap-hdpi 240dpi-320dpi 1.5
 mipmap-xhdpi 320dpi-480dpi 2
 mipmap-xxhdpi 480dpi-640dpi 3
 mipmap-xxxhdpi 640dpi- 4
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if (weatherString!=null){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
