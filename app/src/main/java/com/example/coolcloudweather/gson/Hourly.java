package com.example.coolcloudweather.gson;

import com.google.gson.annotations.SerializedName;


public class Hourly {

    public Cond cond;

    public class Cond{

        public String code;

        public String txt;

    }

    public String date;

    public String tmp;
}
