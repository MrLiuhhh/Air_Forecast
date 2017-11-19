package com.edu.scu.airprot;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.edu.scu.Adapter.HourlyWeather;
import com.edu.scu.Adapter.WeatherAdapter;
import com.edu.scu.Util.HttpCallbackListener;
import com.edu.scu.Util.HttpUtil;
import com.edu.scu.Util.Utility;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class WeatherActivity extends Activity implements View.OnClickListener{
    private LinearLayout weatherInfoLayout;
    private Button switch_city_button;
    private Button refresh_weather_button;
    private TextView city_name_text;
    private TextView weather_wendu_text;
    private TextView weather_shidu_text;
    private TextView quality;
    private TextView date;
    private TextView pm25;
    private TextView pm10;
    private TextView ganmao;

    private ListView listView;

    public static List<HourlyWeather> weatherList=new ArrayList<HourlyWeather>();

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        weatherInfoLayout= (LinearLayout) findViewById(R.id.weather_info_layout);
        switch_city_button= (Button) findViewById(R.id.switch_city);
        refresh_weather_button= (Button) findViewById(R.id.refresh_weather);
        city_name_text= (TextView) findViewById(R.id.city_name);
        weather_wendu_text= (TextView) findViewById(R.id.wendu);
        weather_shidu_text= (TextView) findViewById(R.id.shidu);
        quality= (TextView) findViewById(R.id.quality);
        date= (TextView) findViewById(R.id.date);
        pm25= (TextView) findViewById(R.id.pm25);
        pm10= (TextView) findViewById(R.id.pm10);
        ganmao= (TextView) findViewById(R.id.ganmao);
        listView= (ListView) findViewById(R.id.forcast);
        preferences= PreferenceManager.getDefaultSharedPreferences(this);

        String county_name =getIntent().getStringExtra("county_name");
        if (!TextUtils.isEmpty(county_name)){
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("county_name",county_name);
            Log.d("status-------", "queryFromServer: "+preferences.getString("county_name",""));
            editor.commit();
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            city_name_text.setVisibility(View.INVISIBLE);
            queryFromServer(county_name);
        }else {
            county_name = preferences.getString("county_name","");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            city_name_text.setVisibility(View.INVISIBLE);

            queryFromServer(county_name);
        }
        switch_city_button.setOnClickListener(this);
        refresh_weather_button.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:
                Intent intent=new Intent(this,ChooseCityActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                String county_name =preferences.getString("county_name","");
                if (!TextUtils.isEmpty(county_name)){
                    queryFromServer(county_name);
                    Toast.makeText(WeatherActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }

    private void queryFromServer(String county_name) {
        try {
            String hh="http://www.sojson.com/open/api/weather/json.shtml?city="+county_name;
            //String hhh=new String(county_name.getBytes("UTF-8"),"iso-8859-1");

            HttpUtil.sendHttpRequest(hh , new HttpCallbackListener() {
                @Override
                public void onSuccess(String response) {
                    Log.d("JSON-------", "queryFromServer: "+response);
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showWeather() {


        city_name_text.setText(preferences.getString("city",""));
        weather_wendu_text.setText("温度：" + preferences.getString("wendu",""));
        Log.d("WENDU-------", "queryFromServer: "+preferences.getString("wendu",""));
        weather_shidu_text.setText("湿度：" + preferences.getString("shidu", ""));
        quality.setText("空气质量： " + preferences.getString("quality", ""));
        date.setText("日期： " + preferences.getString("date", ""));
        pm25.setText("PM2.5： " + preferences.getFloat("pm25",0));
        pm10.setText("PM10: "+preferences.getFloat("pm10", 0));
        ganmao.setText(preferences.getString("ganmao",""));

        WeatherAdapter adapter =new WeatherAdapter(this,R.layout.hourly_forcast,weatherList);

        listView.setAdapter(adapter);

        city_name_text.setVisibility(View.VISIBLE);
        weatherInfoLayout.setVisibility(View.VISIBLE);
        //listView.setVisibility(View.VISIBLE);
    }

}
