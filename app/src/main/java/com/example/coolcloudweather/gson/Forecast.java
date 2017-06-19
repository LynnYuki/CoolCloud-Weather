package com.example.coolcloudweather.gson;

import com.google.gson.annotations.SerializedName;


public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt_d")
        public String info;

        @SerializedName("code_d")
        public int code;
    }

    public class Temperature{
        public String max;
        public String min;
    }

}
