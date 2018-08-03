package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by FJQ on 2018/8/3.
 */

public class Forecast {
    public String date;
    @SerializedName("tmp")
    public Temerature temerature;
    @SerializedName("cond")
    public More more;
    public class Temerature{
        public String max;
        public String min;
    }

    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
