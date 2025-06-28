package com.example.coolweather;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coolweather.R;
import com.example.coolweather.db.FavoriteCity;

import java.util.List;

public class FavoriteCityAdapter extends BaseAdapter {
    private Context context;
    private List<FavoriteCity> dataList;

    public FavoriteCityAdapter(Context context, List<FavoriteCity> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.favorite_item, parent, false);
            holder = new ViewHolder();
            holder.cityName = convertView.findViewById(R.id.city_name);
            holder.temperature = convertView.findViewById(R.id.temperature);
            holder.weatherIcon = convertView.findViewById(R.id.weather_icon);
            holder.weatherBg = convertView.findViewById(R.id.weather_bg);
            holder.deleteBtn = convertView.findViewById(R.id.delete_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final FavoriteCity city = dataList.get(position);
        holder.cityName.setText(city.getCityName());
        holder.temperature.setText(city.getTemperature());

        // 设置天气图标
        setWeatherIcon(holder.weatherIcon, city.getWeatherInfo());

        // 设置背景图片
        setWeatherBackground(holder.weatherBg, city.getWeatherInfo());

        // 删除按钮点击事件
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city.delete();
                dataList.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private void setWeatherIcon(ImageView iconView, String weatherInfo) {
        switch (weatherInfo) {
            case "晴":
                iconView.setImageResource(R.drawable.ic_sunny);
                break;
            case "多云":
                iconView.setImageResource(R.drawable.ic_cloudy);
                break;
            case "阴":
                iconView.setImageResource(R.drawable.ic_overcast);
                break;
            case "雨":
                iconView.setImageResource(R.drawable.ic_rain);
                break;
            case "雪":
                iconView.setImageResource(R.drawable.ic_snow);
                break;
            default:
                iconView.setImageResource(R.drawable.ic_default);
                break;
        }
    }

    private void setWeatherBackground(ImageView bgView, String weatherInfo) {
        switch (weatherInfo) {
            case "晴":
                bgView.setImageResource(R.drawable.bg_sunny);
                break;
            case "多云":
                bgView.setImageResource(R.drawable.bg_cloudy);
                break;
            case "阴":
                bgView.setImageResource(R.drawable.bg_overcast);
                break;
            case "雨":
                bgView.setImageResource(R.drawable.bg_rain);
                break;
            case "雪":
                bgView.setImageResource(R.drawable.bg_snow);
                break;
            default:
                bgView.setImageResource(R.drawable.bg_default);
                break;
        }
    }

    static class ViewHolder {
        TextView cityName;
        TextView temperature;
        ImageView weatherIcon;
        ImageView weatherBg;
        ImageView deleteBtn;
    }
}
