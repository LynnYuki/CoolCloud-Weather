package com.example.coolcloudweather;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BASE",getClass().getSimpleName());
        initView();
        initData();
        initListener();
    }


    // 初始化控件
    public abstract void initView();

    // 初始化数据
    public abstract void initData();

    // 初始化监听器
    public abstract void initListener();


    // 判断是否有网络
    public NetworkInfo getNetworkInfo(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    // Toast简单包装函数
    public void showShort(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
