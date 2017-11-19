package com.edu.scu.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.edu.scu.Adapter.HourlyWeather;
import com.edu.scu.DB.WeatherDB;
import com.edu.scu.Model.City;
import com.edu.scu.Model.County;
import com.edu.scu.Model.Province;
import com.edu.scu.airprot.WeatherActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//处理反回数据
public class Utility {
    //处理返回的省级数据
    public static boolean handleProvinceResponse(WeatherDB weatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvince =response.split(",");
            if (allProvince !=null && allProvince.length > 0){
                for (String p : allProvince){
                    String[] array =p.split("\\|");
                    Province province=new Province();
                    province.setProvince_id(array[0]);
                    province.setProvince_name(array[1]);
                    weatherDB.saveProinve(province);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCityResponse(WeatherDB weatherDB,String response,String provinceId){
        if (!TextUtils.isEmpty(response)){
            String[] allCity = response.split(",");
            if (allCity!=null && allCity.length >0){
                for (String c : allCity ){
                    String[] array =c.split("\\|");
                    City city=new City();
                    city.setCity_id(array[0]);
                    city.setCity_name(array[1]);
                    city.setProvince_id(provinceId);
                    weatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(WeatherDB weatherDB,String respose,String cityId){
        if (!TextUtils.isEmpty(respose)){
            String[] allCounty =respose.split(",");
            if (allCounty != null && allCounty.length>0){
                for (String c: allCounty){
                    String[] array =c.split("\\|");
                    County county=new County();
                    county.setCounty_id(array[0]);
                    county.setCounty_name(array[1]);
                    county.setCity_id(cityId);
                    weatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }


    //处理服务器返回的数据
    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            int status = jsonObject.getInt("status");

            Log.d("status-------", "queryFromServer: "+status);
            String city=jsonObject.getString("city");//城市
            String message=jsonObject.getString("message");//f返回数据OK
            String today_date=jsonObject.getString("date");
            JSONObject data = (JSONObject) jsonObject.get("data");//所有数据
            String shidu = data.getString("shidu");//湿度
            float pm25 = data.getInt("pm25");//pm2.5
            float pm10 = data.getInt("pm10");//pm10
            String today_wendu = data.getString("wendu");//温度
            String ganmao = data.getString("ganmao");//提示感冒
            String quality=data.getString("quality");//空气质量

            JSONArray forecast = data.getJSONArray("forecast");//未来5天天气预报
            for (int i = 0; i < forecast.length(); i++){
                Log.d("预报数组的长度", "handleWeatherResponse: "+forecast.length());
                JSONObject day = (JSONObject) forecast.get(i);
                //String fengxiang =day.getString("fx");//风向
                String fengli=day.getString("fl");//风力
                //String high=day.getString("high");//最高温度
                String type=day.getString("type");//天气情况
                //String low=day.getString("low");//最低温度
                String date=day.getString("date");//日期
                HourlyWeather weather=new HourlyWeather(date,type,fengli);
                WeatherActivity.weatherList.add(weather);
                Log.d("weatherList的长度-------", String.valueOf(WeatherActivity.weatherList.size()));
            }
            Log.d("weatherList后的长度-------", String.valueOf(WeatherActivity.weatherList.size()));
            Log.d("进入1-------", "保存天气信息 ");
            saveWeatherInfo(context,status,city,today_date,shidu,pm25,pm10,today_wendu,ganmao,quality);
            Log.d("进入2-------", "保存天气信息 ");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveWeatherInfo(Context context,int status, String city, String today_date,
                                        String shidu, float pm25, float pm10, String today_wendu,
                                        String ganmao,String quality) {

        Log.d("进入-------", "保存天气信息 ");
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("city",city);
        editor.putString("date",today_date);
        editor.putString("shidu",shidu);
        editor.putFloat("pm25",pm25);
        editor.putFloat("pm10",pm10);
        editor.putString("wendu",today_wendu);
        editor.putString("ganmao",ganmao);
        editor.putString("quality",quality);
        editor.putInt("status",status);
        editor.commit();


    }
}
