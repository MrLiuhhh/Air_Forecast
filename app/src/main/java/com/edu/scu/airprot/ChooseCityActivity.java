package com.edu.scu.airprot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.scu.DB.WeatherDB;
import com.edu.scu.Model.City;
import com.edu.scu.Model.County;
import com.edu.scu.Model.Province;
import com.edu.scu.Util.HttpCallbackListener;
import com.edu.scu.Util.HttpUtil;
import com.edu.scu.Util.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChooseCityActivity extends AppCompatActivity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;

    private TextView titletextView;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private WeatherDB weatherDB;

    private List<String> datalist =new ArrayList<String>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;

    private int currentLevel;

    private boolean isFromWeatherActivity;

    private SharedPreferences preferences;


    private StringBuilder response;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    titletextView.setText(response);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //判断是否来自weatheractivity
        isFromWeatherActivity=getIntent().getBooleanExtra(
                "form_weather_activity",false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getBoolean("city_selected",false) && isFromWeatherActivity){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);


        //填充数据
        titletextView= (TextView) findViewById(R.id.title_text);
        listView= (ListView) findViewById(R.id.list_view);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,datalist);
        listView.setAdapter(adapter);
        weatherDB =WeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCites();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity =cityList.get(position);
                    queryCounties();
                }else if (currentLevel == LEVEL_COUNTY){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("city_selected",true);
                    editor.commit();
                    WeatherActivity.weatherList.clear();
                    String county_name = countyList.get(position).getCounty_name();
                    Intent intent =new Intent(ChooseCityActivity.this,WeatherActivity.class);
                    intent.putExtra("county_name",county_name);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProcinces();

    }

    private void queryProcinces() {
        provinceList = weatherDB.loadProvince();
        if (provinceList.size() > 0 ){
            datalist.clear();
            for (Province p: provinceList){
                datalist.add(p.getProvince_name());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titletextView.setText("中国");
            currentLevel = LEVEL_PROVINCE;

        }else {
            queryFromServer(null,"province");
        }
    }

    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city"+code +".xml";
        }else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onSuccess(String response) {
                boolean result =false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(weatherDB,response);
                }else if ("city".equals(type)){
                    result=Utility.handleCityResponse(weatherDB,response,selectedProvince.getProvince_id());
                }else if ("county".equals(type)){
                    result=Utility.handleCountyResponse(weatherDB,response,selectedCity.getCity_id());
                }
                if (result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProcinces();
                            }else if ("city".equals(type)){
                                queryCites();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChooseCityActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void queryCounties() {
        countyList = weatherDB.loadCounty(selectedCity.getCity_id());
        if (countyList.size() > 0){
            datalist.clear();
            for (County c:countyList){
                datalist.add(c.getCounty_name());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titletextView.setText(selectedCity.getCity_name());
            currentLevel = LEVEL_COUNTY;
        }else {
            queryFromServer(selectedCity.getCity_id(),"county");
        }
    }

    private void queryCites() {
        cityList =weatherDB.loadCity(selectedProvince.getProvince_id());
        if (cityList.size() > 0 ){
            datalist.clear();
            for (City c:cityList){
                datalist.add(c.getCity_name());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titletextView.setText(selectedProvince.getProvince_name());
            currentLevel = LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.getProvince_id(),"city");
        }

    }

    private void sendHttpResponse(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                try {
                    URL url=new URL(s);
                    connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(8000);
                    connection.setConnectTimeout(8000);
                    connection.connect();

                    InputStream in =connection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in,"utf-8"));
                    response=new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        response.append(line);
                    }

                    Message msg=new Message();
                    msg.what=1;
                    handler.sendMessage(msg);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY){
            queryCites();
        }else if (currentLevel == LEVEL_CITY){
            queryProcinces();
        }else {
            if (isFromWeatherActivity){
                Intent intent = new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null){
            progressDialog =new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
