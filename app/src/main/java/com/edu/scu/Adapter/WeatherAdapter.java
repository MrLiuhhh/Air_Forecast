package com.edu.scu.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.edu.scu.airprot.R;

import java.util.List;

public class WeatherAdapter extends ArrayAdapter<HourlyWeather> {
    private int resourceId;

    public WeatherAdapter(Context context, int resource, List<HourlyWeather> objects) {
        super(context, resource, objects);
        this.resourceId=resource;
    }

    @NonNull
    @Override
    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {
        HourlyWeather weather=getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);
        TextView date_text= (TextView) view.findViewById(R.id.forcast_date);
        TextView type_text= (TextView) view.findViewById(R.id.forcast_type);
        TextView wind_text= (TextView) view.findViewById(R.id.forcast_wind);

        date_text.setText(weather.getDate());
        type_text.setText(weather.getType());
        wind_text.setText(weather.getWind());
        return view;
    }
}
