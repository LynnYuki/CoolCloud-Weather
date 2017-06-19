package com.example.coolcloudweather.gson;

import com.google.gson.annotations.SerializedName;



public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public  Update update;

    public class Update{
        public String loc;

    }


}
