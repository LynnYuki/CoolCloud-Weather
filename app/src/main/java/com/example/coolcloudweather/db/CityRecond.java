package com.example.coolcloudweather.db;

import org.litepal.crud.DataSupport;


public class CityRecond  extends DataSupport{
    private int id;
    private String cityName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
