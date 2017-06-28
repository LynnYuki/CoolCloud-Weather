package com.example.coolcloudweather.gson;


public class AQI {

    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
        public String co;
        public String o3;
        public String pm10;
        public String so2;
        public String qlty;
    }
}
