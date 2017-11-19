package com.edu.scu.Adapter;

//每个时间点的天气
public class HourlyWeather {
    // 预测时间
    private String date;
    //温度
    private String type;
    //风力
    private String wind;

    public HourlyWeather(String date, String type, String wind) {
        this.date = date;
        this.type = type;
        this.wind = wind;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }
}
