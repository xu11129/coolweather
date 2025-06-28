package com.example.coolweather;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.core.view.GravityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.coolweather.db.FavoriteCity;
import com.example.coolweather.gson.Forecast;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.service.AutoUpdateService;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private Button navButton;

    private TextView titleCity;

    private TextView titleUpdateTime;

    private TextView degreeText;

    private TextView weatherInfoText;

    private LinearLayout forecastLayout;

    private TextView aqiText;

    private TextView pm25Text;

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView bingPicImg;

    private String mWeatherId;

    private Button myCitiesBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        // 初始化各控件
        bingPicImg = findViewById(R.id.bing_pic_img);
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);

        String weatherId = getIntent().getStringExtra("weather_id");

// 如果有缓存，并且是同一个城市的天气，就先展示缓存
        if (weatherString != null && weatherId != null) {
            Weather cachedWeather = Utility.handleWeatherResponse(weatherString);
            if (cachedWeather != null && cachedWeather.basic.weatherId.equals(weatherId)) {
                showWeatherInfo(cachedWeather); // 先展示缓存
                loadBingPic(); // 后台加载图片
                requestWeather(weatherId); // 后台刷新最新天气
                return; // 返回，不再执行下面的 requestWeather
            }
        }

// 没有缓存或者缓存不是当前城市，就去服务器请求
        if (weatherId != null) {
            requestWeather(weatherId);
        }

        swipeRefresh.setOnRefreshListener(() -> requestWeather(mWeatherId));

        navButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        myCitiesBtn = findViewById(R.id.my_cities_btn);
        myCitiesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoriteCitiesActivity.class);
            startActivity(intent);
        });
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(WeatherActivity.this)
                    .load(bingPic)
                    .override(1080, 1920) // 根据设备分辨率调整
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(bingPicImg);
        } else {
            loadBingPic();
        }
    }

    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(() -> {
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                        mWeatherId = weather.basic.weatherId;
                        showWeatherInfo(weather);
                    } else {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                    if (swipeRefresh != null) {
                        swipeRefresh.setRefreshing(false);
                    }
                    loadBingPic();
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    if (swipeRefresh != null) {
                        swipeRefresh.setRefreshing(false);
                    }
                    loadBingPic();
                });
            }
        });
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";

        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    // 获取响应内容
                    String responseBody = response.body().string();

                    // 使用 JSONObject 解析
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray imagesArray = jsonObject.getJSONArray("images");

                    if (imagesArray.length() > 0) {
                        JSONObject imageObject = imagesArray.getJSONObject(0);
                        String imageUrl = "https://www.bing.com" + imageObject.getString("url");

                        // 保存图片 URL 到 SharedPreferences
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("bing_pic", imageUrl);
                        editor.apply();

                        // 主线程加载图片
                        runOnUiThread(() -> {
                            Log.d("BingPic", "Loading image: " + imageUrl);
                            Glide.with(WeatherActivity.this)
                                    .load(imageUrl)
                                    .error(android.R.drawable.ic_dialog_alert)   // 错误提示图
                                    .into(bingPicImg);
                        });
                    } else {
                        Log.e("BingPic", "未找到图片数据");
                    }

                } catch (JSONException e) {
                    Log.e("BingPic", "JSON 解析失败", e);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("BingPic", "请求必应图片失败", e);
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        if (weather == null) return;

        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;

        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max + "℃");
            minText.setText(forecast.temperature.min + "℃");
            forecastLayout.addView(view);
        }

        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }

        comfortText.setText("舒适度：" + weather.suggestion.comfort.info);
        carWashText.setText("洗车指数：" + weather.suggestion.carWash.info);
        sportText.setText("运行建议：" + weather.suggestion.sport.info);

        weatherLayout.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
        // 更新收藏城市信息
        updateFavoriteCity(weather);
    }
    private void updateFavoriteCity(Weather weather) {
        FavoriteCity currentCity = LitePal.where("isCurrent = ?", "1").findFirst(FavoriteCity.class);

        if (currentCity != null) {
            // 更新当前城市信息
            currentCity.setTemperature(weather.now.temperature);
            currentCity.setWeatherInfo(weather.now.more.info);
            currentCity.save();
        } else {
            // 添加当前城市到收藏列表
            currentCity = new FavoriteCity();
            currentCity.setCityName(weather.basic.cityName);
            currentCity.setWeatherId(weather.basic.weatherId);
            currentCity.setTemperature(weather.now.temperature);
            currentCity.setWeatherInfo(weather.now.more.info);
            currentCity.setIsCurrent(1);
            currentCity.save();
        }
    }
}