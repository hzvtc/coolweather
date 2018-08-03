package com.coolweather.android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
    }
}
