package com.example.coolcloudweather;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.coolcloudweather.R;

public class AboutApplicationActivity extends BaseActivity {

    private Button github;
    private Button blog;
    @Override
    public void initView() {

        setContentView(R.layout.activity_about_application);

        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("关于天气");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        github = (Button)findViewById(R.id.github);
        blog = (Button)findViewById(R.id.blog);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        github.setOnClickListener(this);
        blog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        switch (v.getId()){
            case R.id.github:
                intent.setData(Uri.parse("http://Github.com/LynnYuki"));
                break;
            case R.id.blog:
                intent.setData(Uri.parse("https://lynnyuki.github.io"));
                break;
        }
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
