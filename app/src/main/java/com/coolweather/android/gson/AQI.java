package com.coolweather.android.gson;

/**
 * Created by FJQ on 2018/8/3.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
