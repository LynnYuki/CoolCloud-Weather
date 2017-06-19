package com.example.coolcloudweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.example.coolcloudweather.gson.Weather;
import com.example.coolcloudweather.util.HttpUtil;
import com.example.coolcloudweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences pref  = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isUpdateTime = pref.getBoolean("isUpdateTime", true);
        if (isUpdateTime == true){
            updateWeather();
            AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
            int autoUpdateTime = pref.getInt("autoUpdateTime", 60);
            int anHour = autoUpdateTime * 60 * 1000; // 这是 60 分钟的毫秒数
            long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
            Intent i = new Intent(this, AutoUpdateService.class);
            PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
            manager.cancel(pi);
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新信息
     */
    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String cityName = prefs.getString("cityName", null);
        if(cityName != null){
            String address = "https://api.heweather.com/v5/weather?city=" + cityName + "&key=901b054f35e2424696f9fb0f9fbe2eba";
            HttpUtil.sendOkHttpRequest(address, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)){
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("weatherResponse", responseText);
                        editor.apply();
                    }

                }
            });
        }
    }


}
