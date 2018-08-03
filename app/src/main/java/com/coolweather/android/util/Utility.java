package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.Country;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by FJQ on 2018/8/3.
 */

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     * @param reponse
     * @return
     */
    public static boolean handleProvinceReponse(String reponse){
        if (!TextUtils.isEmpty(reponse)){
            try {
                JSONArray provinces = new JSONArray(reponse);
                for (int i=0;i<provinces.length();i++){
                    JSONObject provinceObject = (JSONObject) provinces.get(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     * @param reponse
     * @return
     */
    public static boolean handleCityReponse(String reponse,int provinceId){
        if (!TextUtils.isEmpty(reponse)){
            try {
                JSONArray cities = new JSONArray(reponse);
                for (int i=0;i<cities.length();i++){
                    JSONObject cityObject = (JSONObject) cities.get(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     * @param reponse
     * @return
     */
    public static boolean handleCountryReponse(String reponse,int cityId){
        if (!TextUtils.isEmpty(reponse)){
            try {
                JSONArray countries = new JSONArray(reponse);
                for (int i=0;i<countries.length();i++){
                    JSONObject countryObject = (JSONObject) countries.get(i);
                    Country country = new Country();
                    country.setCountryName(countryObject.getString("name"));
                    country.setWeatherId(countryObject.getString("weather_id"));
                    country.setCityId(cityId);
                    country.save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    /**
     * 将服务器返回的json数据解析成Weather实体类
     * @param reponse
     * @return
     */
    public static Weather handleWeatherResponse(String reponse){
        try {
            JSONObject jsonObject = new JSONObject(reponse);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
