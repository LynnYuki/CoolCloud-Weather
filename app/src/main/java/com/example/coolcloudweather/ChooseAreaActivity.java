package com.example.coolcloudweather;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.coolcloudweather.util.HttpUtil;
import com.example.coolcloudweather.R;
import com.example.coolcloudweather.db.CityRecond;
import com.example.coolcloudweather.gson.City;
import com.example.coolcloudweather.util.Utility;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.ContextMenu.*;

public class ChooseAreaActivity extends BaseActivity {
    private EditText searchText;
    private Button searchButton;
    private Button delteall;
    private ListView listView;
    private ListView listViewRecond;
    private List<String> cityList = new ArrayList<>();
    private List<String> recondList = new ArrayList<>();
    private List<Integer>delerecondList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> recondAdapter;
    int selectPosition = 0;

    @Override
    public void initView() {
        setContentView(R.layout.activity_choose_area);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("选择地区");
        }

        searchText = (EditText) findViewById(R.id.search_text);
        searchButton = (Button) findViewById(R.id.search_button);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cityList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getNetworkInfo() != null && getNetworkInfo().isAvailable()) {
                    String cityName = cityList.get(position);
                    MainActivity.actionStart(ChooseAreaActivity.this, cityName);
                    finish();
                } else {
                    showShort("当前没有网络");
                }
            }
        });
        delteall = (Button) findViewById(R.id.deleteall_button);
        listViewRecond = (ListView) findViewById(R.id.list_view_recond);
        recondAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recondList);
        listViewRecond.setAdapter(recondAdapter);

        listViewRecond.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getNetworkInfo() != null && getNetworkInfo().isAvailable()) {
                    String cityName = recondList.get(position);
                    MainActivity.actionStart(ChooseAreaActivity.this, cityName);
                    finish();
                } else {
                    showShort("当前没有网络");
                }
            }
        });
        showRecond();

        //设置搜索记录长按删除功能

        listViewRecond.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                menu.add(0, 0, 0, "删除记录");

            }
        });

        listViewRecond.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                selectPosition = position;
                listViewRecond.showContextMenu();
                Log.d("TAF4","点击位置"+position);
                return true;

            }

        });
    }

    //长按菜单响应函数
    public boolean onContextItemSelected(MenuItem item){

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()){
            case 0:
                //从数据库查询数据id放入list
                List<CityRecond> list = DataSupport.select("id").find(CityRecond.class);
                for (CityRecond recond:list){
                    delerecondList.add(recond.getId());
                    }
                //删除listview之中item对应id的数据
                DataSupport.delete(CityRecond.class, delerecondList.get(Integer.parseInt(String.valueOf(selectPosition))));
                recondList.remove(selectPosition);
                recondAdapter.notifyDataSetChanged();
                Log.d("TAG","删除数据是"+delerecondList.get(Integer.parseInt(String.valueOf(selectPosition))));
                showShort("删除成功");

                return true;
            default:

        }
        return super.onContextItemSelected(item);
    }








    @Override
    public void initData() {
        LitePal.getDatabase();

    }

    @Override
    public void initListener() {
        searchButton.setOnClickListener(this);
        delteall.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_button:
                showSearchResult();
                break;
            case R.id.deleteall_button:
                deleteall();
                break;
            default:
        }
    }

    public void showSearchResult(){
        String cityName = searchText.getText().toString();
        if (!TextUtils.isEmpty(cityName)){
            // 如果不为空，则进行查询
            if (getNetworkInfo() != null && getNetworkInfo().isAvailable()){
                //从和风数据源查找城市
                String address = "https://api.heweather.com/v5/search?city=" + cityName + "&key=bc0418b57b2d4918819d3974ac1285d9";
                requestData(address);
            }else{
                showShort("当前网络无连接");
            }
        }else{
            showShort("请输入城市名称");
        }

    }



    public void requestData(String address){
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showShort("请求数据失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                final City city = Utility.handleCityResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showCity(city);
                    }
                });
            }
        });
    }

    //显示搜索到的城市
    public void showCity(City city){
        cityList.clear();
        if ("ok".equals(city.status) && city != null){
            cityList.add(city.basic.city);
            solveSearchRecond(city.basic.city);
            adapter.notifyDataSetChanged();
            listView.setSelection(0);

        }else{
            showShort("未找到该城市");
        }
    }

    /**
     * 保存城市名称 -> 数据库
     */
    public void solveSearchRecond(String cityName){
        CityRecond cityRecond = new CityRecond();
        cityRecond.setCityName(cityName);
        cityRecond.save();
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

    /**
     * 显示搜索记录
     */
    public void showRecond(){
        recondList.clear();
        List<CityRecond> list = DataSupport.select("cityName").find(CityRecond.class);
        for (CityRecond recond:list){
            recondList.add(recond.getCityName());
        }
        recondAdapter.notifyDataSetChanged();
        listViewRecond.setSelection(0);
    }

    /**
     * 清空搜索记录
     */

    public void deleteall(){

        List<CityRecond> list = DataSupport.select("cityName").find(CityRecond.class);
        for (CityRecond recond:list){
            recondList.add(recond.getCityName());
        }
        if(!recondList.isEmpty()){
            DataSupport.deleteAll(CityRecond.class);
            recondList.clear();
            recondAdapter.notifyDataSetChanged();
            showShort("清除成功");
        }else
            {
                showShort("没有搜索记录");
            }

        }


}
