package com.example.coolcloudweather;


import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.example.coolcloudweather.util.BitmapUtils;

public class SplashActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this,R.layout.activity_splash,null);
        setContentView(view);
        Bitmap bitmap = BitmapUtils.readBitMap(this,R.drawable.ic_splash);
        ImageView im = (ImageView) findViewById(R.id.iv_splash);
        im.setImageBitmap(bitmap);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f,1.0f);
        alphaAnimation .setDuration(3000);
        view.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public  void onAnimationStart(Animation animation){

            }

            @Override
            public  void onAnimationEnd(Animation animation){
                Intent intent = new Intent( SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
            @Override
            public  void onAnimationRepeat(Animation animation){

            }
        });

    }
}
