package com.example.coolweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.FavoriteCityAdapter;
import com.example.coolweather.db.FavoriteCity;
import com.example.coolweather.util.Utility;

import org.litepal.LitePal;

import java.util.List;

public class FavoriteCitiesActivity extends AppCompatActivity {

    private ListView favoriteListView;
    private List<FavoriteCity> favoriteCityList;
    private FavoriteCityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_cities);

        favoriteListView = findViewById(R.id.favorite_list);
        TextView addBtn = findViewById(R.id.add_button);

        loadFavoriteCities();

        // 设置列表点击事件
        favoriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FavoriteCity city = favoriteCityList.get(position);
                Intent intent = new Intent(FavoriteCitiesActivity.this, WeatherActivity.class);
                intent.putExtra("weather_id", city.getWeatherId());
                startActivity(intent);
                finish();
            }
        });

        // 设置长按删除事件
        favoriteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteCity(position);
                return true;
            }
        });

        // 添加按钮点击事件
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoriteCitiesActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadFavoriteCities() {
        favoriteCityList = LitePal.findAll(FavoriteCity.class);
        adapter = new FavoriteCityAdapter(this, favoriteCityList);
        favoriteListView.setAdapter(adapter);
    }

    private void deleteCity(final int position) {
        final FavoriteCity city = favoriteCityList.get(position);

        // 如果只剩一个城市，删除后返回该城市天气页面
        if (favoriteCityList.size() == 1) {
            city.delete();
            Intent intent = new Intent(this, WeatherActivity.class);
            intent.putExtra("weather_id", city.getWeatherId());
            startActivity(intent);
            finish();
            return;
        }

        // 删除城市
        city.delete();
        favoriteCityList.remove(position);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "已删除" + city.getCityName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteCities();
    }
}
