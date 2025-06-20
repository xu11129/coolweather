package com.example.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.coolweather.WeatherActivity;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000; // 8小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;

        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return START_STICKY;
    }

    /**
     * 更新天气信息。
     */
    private void updateWeather() {
        SharedPreferences prefs = getSharedPreferences("default", MODE_PRIVATE);
        String weatherString = prefs.getString("weather", null);

        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            if (weather == null || !"ok".equals(weather.status)) {
                Log.e("AutoUpdateService", "Failed to parse cached weather data");
                return;
            }

            String cityId = weather.basic.weatherId;
            String key = "你的和风Key"; // 替换为你自己的 Key
            String weatherUrl = "https://api.heweather.net/s9/weather/now/" + cityId + "?key=" + key;

            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather updatedWeather = Utility.handleWeatherResponse(responseText);
                    if (updatedWeather != null && "ok".equals(updatedWeather.status)) {
                        SharedPreferences.Editor editor = getSharedPreferences("default", MODE_PRIVATE).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    } else {
                        Log.e("AutoUpdateService", "Failed to fetch updated weather: " + responseText);
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("AutoUpdateService", "Failed to request weather update", e);
                }
            });
        }
    }

    /**
     * 更新必应每日一图
     */
    private void updateBingPic() {
        String requestBingPic = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";

        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray imagesArray = jsonObject.getJSONArray("images");

                    if (imagesArray.length() > 0) {
                        JSONObject imageObject = imagesArray.getJSONObject(0);
                        String imageUrl = "https://www.bing.com" + imageObject.getString("url");

                        SharedPreferences.Editor editor = getSharedPreferences("default", MODE_PRIVATE).edit();
                        editor.putString("bing_pic", imageUrl);
                        editor.apply();
                    }
                } catch (JSONException e) {
                    Log.e("BingPic", "JSON 解析失败", e);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("BingPic", "Failed to request Bing picture", e);
            }
        });
    }
}