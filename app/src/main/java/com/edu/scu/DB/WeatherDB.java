package com.edu.scu.DB;

//操作数据库的各种操作

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.edu.scu.Model.City;
import com.edu.scu.Model.County;
import com.edu.scu.Model.Province;

import java.util.ArrayList;
import java.util.List;

public class WeatherDB {
    private static final String DataBaseName="Weather";

    private static final int VERSION = 1;

    private SQLiteDatabase db;

    private static WeatherDB weatherDB;

    private WeatherDB(Context context){
        DataBaseHelper dataBaseHelper=new DataBaseHelper(context,DataBaseName,null,VERSION);

        db = dataBaseHelper.getWritableDatabase();
    }

    //单例模式
    public static WeatherDB getInstance(Context context){
        if (weatherDB ==null){
           weatherDB =new WeatherDB(context);
            return weatherDB;
        }
        return weatherDB;
    }

    //存数据
    public void saveProinve(Province province){
        if (province !=null){
            ContentValues values =new ContentValues();
            values.put("province_name",province.getProvince_name());
            values.put("province_id",province.getProvince_id());
            db.insert("Province",null,values);
        }
    }

    public void saveCity(City city){
        if (city != null){
            ContentValues values=new ContentValues();
            values.put("city_name",city.getCity_name());
            values.put("city_id",city.getCity_id());
            values.put("province_id",city.getProvince_id());
            db.insert("City",null,values);
        }
    }

    public void saveCounty(County county){
        if (county!=null){
            ContentValues values=new ContentValues();
            values.put("county_name",county.getCounty_name());
            values.put("county_id",county.getCounty_id());
            values.put("city_id",county.getCity_id());
            db.insert("County",null,values);
        }
    }

    //返回数据
    public List<Province> loadProvince(){
        List<Province> list=new ArrayList<Province>();
        //建立数据库查询cursor
        Cursor cursor=db.query("Province",null,null,null,null,null,null);
        //如果有数据的话
        if (cursor.moveToFirst()){
            do {
                Province province =new Province();
                province.setProvince_id(cursor.getString(cursor.getColumnIndex("province_id")));
                province.setProvince_name(cursor.getString(cursor.getColumnIndex("province_name")));
                list.add(province);
            }while (cursor.moveToNext());
        }
        return list;
    }

    public List<City> loadCity(String province_id){
        List<City> list =new ArrayList<City>();
        Cursor cursor=db.query("City",null,"province_id = ?",new String[]{province_id},null,null,null);
        if (cursor.moveToFirst()){
            do {
                City city=new City();
                city.setCity_id(cursor.getString(cursor.getColumnIndex("city_id")));
                city.setCity_name(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvince_id(province_id);
                list.add(city);
            }while (cursor.moveToNext());
        }
        return list;
    }

    public List<County> loadCounty(String city_id){
        List<County> list=new ArrayList<County>();
        Cursor cursor=db.query("County",null,"city_id = ?", new String[]{city_id},null,null,null);
        if (cursor.moveToFirst()){
            do {
                County county=new County();
                county.setCounty_id(cursor.getString(cursor.getColumnIndex("county_id")));
                county.setCounty_name(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCity_id(city_id);
                list.add(county);
            }while (cursor.moveToNext());

        }
        return list;
    }

}
