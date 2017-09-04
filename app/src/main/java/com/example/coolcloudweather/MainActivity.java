package com.example.coolcloudweather;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.coolcloudweather.R;
import com.example.coolcloudweather.gson.Forecast;
import com.example.coolcloudweather.gson.Hourly;
import com.example.coolcloudweather.gson.Weather;
import com.example.coolcloudweather.service.AutoUpdateService;
import com.example.coolcloudweather.util.HttpUtil;
import com.example.coolcloudweather.util.TaskKiller;
import com.example.coolcloudweather.util.Time;
import com.example.coolcloudweather.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity{

    private static int SIGN_NO_INTERNET = 0;
    private static int SIGN_ALARMS = 1;
    private static String infoText = "无";
    private ScrollView weatherLayout;

    private CoordinatorLayout mainLayout;


    private TextView titleCity;

    private FloatingActionButton fab_button;
    List<String> permissionList  = new ArrayList<>();

    // 以下是 weather_now的控件

    private TextView degreeText;

    private TextView weatherInfoText;

    private RelativeLayout weaherNowLayout;

    private TextView updateTimeText;

    //以下是weather_alarms的控件
    private TextView title;

    private TextView type;

    private TextView level;

    private TextView stat;

    private TextView txt;

    private LinearLayout alarm;

    // 以下是 weather_hour的控件
    private TextView hourTime;

    private  TextView hourText;

    private TextView hourDegree;

    private List<Hour> hourList = new ArrayList<>();

    private RecyclerView recyclerView;

    private  HourAdapter hourAdapter;


    // 以下是 weather_aqi的控件
    private TextView aqiText;

    private TextView pm25Text;

    private TextView coText;

    private TextView o3Text;

    private TextView pm10Text;

    private TextView so2Text;

    private TextView qlty;

    // 以下是 weather_forecast的控件
    private LinearLayout forecastLayout;

    // 以下是 weather_suggestion的控件
    private TextView carWashText;

    private TextView sportText;

    private TextView comfortText;

    private TextView uvText;

    private TextView clothesText;

    private TextView coldText;

    private Button carWashBtn;

    private Button sportBtn;

    private Button comfortBtn;

    private Button uvBtn;

    private Button clothesBtn;

    private Button coldBtn;

    private String carWashInfo;
    private String carWashSign;

    private String sportInfo;
    private String sportSign;

    private String comfortInfo;
    private String comfortSign;

    private String uvInfo;
    private String uvSign;

    private String clothesInfo;
    private String clothesSign;

    private String coldInfo;
    private String coldSign;



    public SwipeRefreshLayout swipeRefresh;
    private long triggerAtTimefirst = 0;

    // 定位
    public LocationClient mlocationClient;
    public static String currentPosition = "";


    @Override
    public void initView() {
        setContentView(R.layout.acticity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // 初始化各种控件
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
        mainLayout = (CoordinatorLayout) findViewById(R.id.main_layout);
        fab_button = (FloatingActionButton) findViewById(R.id.fab_button);

        // weather_now
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        weaherNowLayout = (RelativeLayout)findViewById(R.id.weather_now_layout);
        updateTimeText = (TextView)findViewById(R.id.update_time_text);

        // weather_hour
        hourDegree = (TextView)findViewById(R.id.hour_degree);
        hourText = (TextView)findViewById(R.id.hour_text);
        hourTime = (TextView)findViewById(R.id.hout_time);

        recyclerView = (RecyclerView)findViewById(R.id.weather_hourly);
        hourAdapter = new HourAdapter(hourList);

        //加载小时预报布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(hourAdapter);



        // weather_aqi
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        coText = (TextView)findViewById(R.id.co_text);
        o3Text = (TextView)findViewById(R.id.o3_text);
        pm10Text = (TextView)findViewById(R.id.pm10_text);
        so2Text = (TextView)findViewById(R.id.so2_text);
        qlty = (TextView)findViewById(R.id.qlty);

        // weather_suggestion
        comfortText = (TextView)findViewById(R.id.comfort_text);
        carWashText = (TextView)findViewById(R.id.car_wash_text);
        sportText = (TextView)findViewById(R.id.sport_text);
        uvText = (TextView)findViewById(R.id.uv_text);
        clothesText = (TextView)findViewById(R.id.clothes_text);
        coldText = (TextView)findViewById(R.id.cold_text);
        comfortBtn = (Button)findViewById(R.id.comfort_button);
        carWashBtn = (Button)findViewById(R.id.car_wash_button);
        sportBtn = (Button)findViewById(R.id.sport_button);
        uvBtn = (Button)findViewById(R.id.uv_button);
        clothesBtn = (Button)findViewById(R.id.clothes_button);
        coldBtn = (Button)findViewById(R.id.cold_button);

        // 定位权限初始化
        mlocationClient = new LocationClient(getApplicationContext());
        mlocationClient.registerLocationListener(new MyLocationListener());

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //一次性申请所有运行时权限
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }

        swipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
         //设置刷新进度条颜色
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent));


    }

    /**
     * 权限申请处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    for (int result:grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            // 如果存在某个权限没有处理
                            finish();
                            return;
                        }
                    }
                }else{
                    // 发生未知错误
                    Toast.makeText(this, "权限申请出现未知错误", Toast.LENGTH_SHORT).show();
                        //finish();
                }
                break;
            default:
        }
    }



    @Override
    public void initListener() {
        comfortBtn.setOnClickListener(this);
        carWashBtn.setOnClickListener(this);
        sportBtn.setOnClickListener(this);
        uvBtn.setOnClickListener(this);
        clothesBtn.setOnClickListener(this);
        coldBtn.setOnClickListener(this);
        fab_button.setOnClickListener(this);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (getNetworkInfo() == null){
                    Snackbar.make(swipeRefresh, "当前无网络，无法刷新 %>_<% ",Snackbar.LENGTH_LONG).setAction("去设置网络", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //设置网络链接
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                        }
                    }).show();
                    //刷新结束隐藏进度条
                    swipeRefresh.setRefreshing(false);
                }else{
                    //刷新加载天气信息
                    showAnimationAlpha(weaherNowLayout);
                }
            }
        });
    }

    @Override
    public void initData() {
        String cityName = getIntent().getStringExtra("cityName");
        String currentPosition = getIntent().getStringExtra("currentPosition");
        if (!TextUtils.isEmpty(cityName)){
            requestWeather(cityName);
        }else if(!TextUtils.isEmpty(currentPosition)){
            requestWeather(currentPosition);
        }
        else{
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String weatherString = prefs.getString("weatherResponse", null);        // weather 保存API 返回的字符串
            if (weatherString != null){
                // 有缓存时直接解析天气数据
                Weather weather = Utility.handleWeatherResponse(weatherString);
                showWeatherInfo(weather);
                mainLayout.setVisibility(View.VISIBLE);
            }else {
                // 无缓存时通过定位向服务器查询数据
                if (getNetworkInfo() != null && getNetworkInfo().isAvailable()){
                    // 查询完之后显示 coordinatorLayout.setVisibility(View.VISIBLE);
                    LocationClientOption option = new LocationClientOption();
                    //返回定位地址信息
                    option.setIsNeedAddress(true);
                    mlocationClient.setLocOption(option);
                    mlocationClient.start();
                }else{
                    showDialog("当前无网络，请打开网络",SIGN_NO_INTERNET);
                }

            }
            Intent intent = new Intent(MainActivity.this, AutoUpdateService.class);
            startService(intent);
        }

    }

    /**
     * 显示设置网络或者显示天气警告信息对话框
     */
    public void showDialog(String info, final int SIGN){
        final AlertDialog.Builder alertDialog  = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setMessage(info);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (SIGN == SIGN_NO_INTERNET){
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                    TaskKiller.dropAllAcitivty();
                }

                if (SIGN == SIGN_ALARMS){
                    alertDialog.setCancelable(true);
                }

            }
        });
        alertDialog.show();
    }


    /**
     * 用来自动定位,显示第一次的天气信息
     */
    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            currentPosition = bdLocation.getCity();
            if (currentPosition != null){
                requestWeather(currentPosition);//传入获得的城市名称
                showShort(currentPosition + " 定位成功");
            }else{
                showShort("定位错误，可能没有获取到定位权限，请打开定位权限后重新下打开此应用");
        }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }


    /**
     * 根据城市地点请求城市天气信息
     */
    public void requestWeather(final String cityName){

        String address = "https://free-api.heweather.com/v5/weather?city=" + cityName + "&key=8b4bef1d66164634b4ff3188f2189ce4";
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showShort("数据源服务器错误，获取天气信息失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                            editor.putString("weatherResponse", responseText);
                            editor.putString("cityName",cityName);
                            editor.apply();
                            showWeatherInfo(weather);
                            Log.d("TAG","数据状态"+weather.status);
                        }else{
                            showShort("获取天气信息失败");
                            Log.d("TAG","数据状态"+weather.status);

                        }
                    }
                });

            }
        });
        swipeRefresh.setRefreshing(false);


    }

    /**
     * 显示天气信息
     */

    private void showWeatherInfo(Weather weather){

        String cityName = weather.basic.cityName;
        String degree = weather.now.temperature ;
        String weatherInfo = weather.now.more.info;
        String updateTime = weather.basic.update.loc;

        titleCity.setText(cityName);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        updateTimeText.setText("数据更新时间: " + updateTime.split(" ")[1]);


        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList){
            // 将未来几天的天气添加到视图中
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.weather_forecast_item, forecastLayout, false);
            TextView dateText = (TextView)view.findViewById(R.id.data_text);
            TextView infoText = (TextView)view.findViewById(R.id.info_text);
            TextView maxMinText = (TextView)view.findViewById(R.id.max_min_text);
            ImageView weatherPic = (ImageView)view.findViewById(R.id.weather_pic);

            // 动态获取天气资源id
            String weatherCode = "weather_"+forecast.more.code;
            int resId = getResources().getIdentifier(weatherCode, "drawable", this.getPackageName());
            if (resId != 0){
                weatherPic.setImageResource(resId);
            }

            //解析获得的时间转化为星期数
            dateText.setText(Time.parseTime(forecast.date));
            infoText.setText(forecast.more.info);
            maxMinText.setText(forecast.temperature.max +"°"+ " ～ " + forecast.temperature.min+"°" );
            forecastLayout.addView(view);
        }
        //加载小时预报数据到hourList
        hourList.clear();
        for (Hourly hourly:weather.hourlyList){
            Hour hour = new Hour();
            hour.setDegree(hourly.tmp + "°" );
            hour.setText(hourly.cond.txt);
            hour.setTime(hourly.date.split(" ")[1]);
            hourList.add(hour);
        }

        hourAdapter.notifyDataSetChanged();


        // weather_aqi 空气质量

        if(weather.aqi !=null) {

            if (weather.aqi.city.aqi != null) {
                aqiText.setText(weather.aqi.city.aqi);
            } else {
                matchText(aqiText);
            }

            if (weather.aqi.city.pm25 != null) {
                pm25Text.setText(weather.aqi.city.pm25);
            } else {
                matchText(pm25Text);
            }

            if (weather.aqi.city.co != null) {
                coText.setText(weather.aqi.city.co);
            } else {
                matchText(coText);
            }

            if (weather.aqi.city.o3 != null) {
                o3Text.setText(weather.aqi.city.o3);
            } else {
                matchText(o3Text);
            }

            if (weather.aqi.city.pm10 != null) {
                pm10Text.setText(weather.aqi.city.pm10);
            } else {
                matchText(pm10Text);
            }

            if (weather.aqi.city.so2 != null) {
                so2Text.setText(weather.aqi.city.so2);
            } else {
                matchText(so2Text);
            }
            if (weather.aqi.city.qlty != null) {
                qlty.setText(weather.aqi.city.qlty);
            } else {
                matchText(qlty);
            }
        }else{
                matchText(aqiText);
                matchText(pm25Text);
                matchText(coText);
                matchText(o3Text);
                matchText(pm10Text);
                matchText(so2Text);
                matchText(qlty);
            }

        //设置字体显示粗体。
        aqiText.getPaint().setFakeBoldText(true);
        pm25Text.getPaint().setFakeBoldText(true);
        coText.getPaint().setFakeBoldText(true);
        o3Text.getPaint().setFakeBoldText(true);
        pm10Text.getPaint().setFakeBoldText(true);
        so2Text.getPaint().setFakeBoldText(true);

        // 舒适指数

        comfortSign = weather.suggestion.comfort.sign;
        carWashSign = weather.suggestion.carWash.sign;
        sportSign = weather.suggestion.sport.sign;
        uvSign = weather.suggestion.uv.sign;
        clothesSign = weather.suggestion.clothes.sign;
        coldSign = weather.suggestion.cold.sign;


        comfortText.setText(comfortSign);
        comfortText.getPaint().setFakeBoldText(true);
        carWashText.setText(carWashSign);
        carWashText.getPaint().setFakeBoldText(true);
        sportText.setText(sportSign);
        sportText.getPaint().setFakeBoldText(true);
        uvText.setText(uvSign);
        uvText.getPaint().setFakeBoldText(true);
        clothesText.setText(clothesSign);
        clothesText.getPaint().setFakeBoldText(true);
        coldText.setText(coldSign);
        coldText.getPaint().setFakeBoldText(true);

        comfortInfo = weather.suggestion.comfort.info;
        carWashInfo = weather.suggestion.carWash.info;
        sportInfo = weather.suggestion.sport.info;
        uvInfo = weather.suggestion.uv.info;
        clothesInfo = weather.suggestion.clothes.info;
        coldInfo = weather.suggestion.cold.info;

        weatherLayout.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.VISIBLE);




    }

    public void matchText(TextView txt){
        txt.setText(infoText);

    }

    /**
     * 停止定位
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mlocationClient.stop();
    }

    /**
     * 添加 actionbar 菜单项
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * 菜单点击事件响应
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                // 跳转到设置界面
                Intent intent1 = new Intent(this, SettingActivity.class);
                intent1.putExtra("weather_title","设置");
                startActivity(intent1);
                break;
            case R.id.night_model:
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = pref.edit();
                boolean isNight = pref.getBoolean("isNight", false);
                if (isNight){
                    // 如果已经是夜间模式
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    recreate();
                    editor.putBoolean("isNight", false);
                    editor.apply();
                }else{
                    // 如果是日间模式
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    recreate();
                    editor.putBoolean("isNight", true);
                    editor.apply();
                }
                break;
        }
        return true;
    }

    /**
     * 对 back 键监听，如果连续点击两次 back 键的时间差 < 2s ,则退出所有程序
     */
    @Override
    public void onBackPressed() {
        long triggerAtTimeSecond = triggerAtTimefirst;
        triggerAtTimefirst = SystemClock.elapsedRealtime();
        if (triggerAtTimefirst - triggerAtTimeSecond <= 2000){
            TaskKiller.dropAllAcitivty();
        }else{
            showShort("请再点击 Back 键, 确认退出");
        }

    }

    @Override
    public void onClick(View v) {
        // 通过 SuggestionInfoActivity 中的静态方法直接传值
        switch (v.getId()){
            case R.id.fab_button:
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                startActivity(intent);
                break;
            case R.id.comfort_button:
                SuggestionInfoActivity.actionStart(this, comfortInfo,comfortSign,"舒适度指数");
                break;
            case R.id.car_wash_button:
                SuggestionInfoActivity.actionStart(this, carWashInfo,carWashSign,"洗车指数");
                break;
            case R.id.sport_button:
                SuggestionInfoActivity.actionStart(this, sportInfo,sportSign,"运动指数");
                break;
            case R.id.cold_button:
                SuggestionInfoActivity.actionStart(this, coldInfo,coldSign,"感冒指数");
                break;
            case R.id.clothes_button:
                SuggestionInfoActivity.actionStart(this, clothesInfo,clothesSign,"穿衣指数");
                break;
            case R.id.uv_button:
                SuggestionInfoActivity.actionStart(this, uvInfo,uvSign,"紫外线指数");
                break;
            default:
                break;
        }
    }

    /**
     * 下拉刷新的渐变动画
     */
    private void showAnimationAlpha(final View view){
        Animation alpha = AnimationUtils.loadAnimation(MainActivity.this,R.anim.alpha_before);
        view.startAnimation(alpha);
        alpha.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                String cityName = prefs.getString("cityName", null);
                requestWeather(cityName);
                Animation alpha = AnimationUtils.loadAnimation(MainActivity.this,R.anim.alpha_after);
                view.startAnimation(alpha);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 城市名称获取
     */
    public static void actionStart(Context context ,String cityName){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("cityName", cityName);
        context.startActivity(intent);
    }

}
