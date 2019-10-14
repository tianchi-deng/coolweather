package com.coolweather.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.gson.AQI;
import com.coolweather.android.gson.Lifestyle;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    private Button navButton;
    private ScrollView weatherLayout;
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
    public SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        swipeRefresh=findViewById(R.id.swip_refresh);
        bingPicImg=findViewById(R.id.bing_pic_img);
        weatherLayout=findViewById(R.id.weather_layout);
        titleCity=findViewById(R.id.title_city);
        titleUpdateTime=findViewById(R.id.title_update_time);
        degreeText=findViewById(R.id.degree_text);
        weatherInfoText=findViewById(R.id.weather_info_text);
        forecastLayout=findViewById(R.id.forecast_layout);
        aqiText=findViewById(R.id.aqi_text);
        pm25Text=findViewById(R.id.pm25_text);
        comfortText=findViewById(R.id.comfort_text);
        carWashText=findViewById(R.id.car_wash_text);
        sportText=findViewById(R.id.sport_text);
        drawerLayout=findViewById(R.id.drawer_layout);
        navButton=findViewById(R.id.nav_button);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic=prefs.getString("bing_pic",null);
        if (bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else{
            loadBingPic();
        }
        String weatherString=prefs.getString("weather",null);
        final String weatherId;
        if (weatherString!=null){
            Weather weather= Utility.handleWeatherResponse(weatherString);
            weatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });
    }
    public void requestWeather(final String weatherId){
        String weatherUrl="https://free-api.heweather.net/s6/weather?location="+ weatherId+"&key=fceab618c240409d850ad68992be0217";
        String aqiUrl="https://free-api.heweather.com/s6/air/now?location="+weatherId+"&key=fceab618c240409d850ad68992be0217";


        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure( Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse( Call call,  Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("nihao",responseText);
                        if (weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor  editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this,responseText,Toast.LENGTH_LONG).show();

                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        HttpUtil.sendOkHttpRequest(aqiUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取空气质量失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseText=response.body().string();
                final AQI aqi=Utility.handleAQIResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("nihao",responseText);
                        if (aqi!=null&&"ok".equals(aqi.status)){
                            SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("api",responseText);
                            editor.apply();
                            showAQIInfo(aqi);
                        }else{
                            Toast.makeText(WeatherActivity.this,responseText,Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });
        loadBingPic();
    }

//    private void showAQIInfo(AQI aqi  +){


//    }
    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
    private void showWeatherInfo(Weather weather){
        String cityName=weather.basic.cityName;
        String updateTime=weather.update.updateTime+"更新";
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.weatherTxt;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(int i=0;i<3;i++ )
        {View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dataText=(TextView) view.findViewById(R.id.date_text);
            TextView infoText=(TextView) view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            Log.d("nihao", weather.forecastList.get(i).date);
            dataText.setText(weather.forecastList.get(i).date);
            infoText.setText(weather.forecastList.get(i).cond_txt_d);
            maxText.setText(weather.forecastList.get(i).tmp_max+"℃");
            minText.setText(weather.forecastList.get(i).tmp_min+"℃");
            forecastLayout.addView(view);
        }
//        for (Forecast forecast:weather.forecastList){
//            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
//            TextView dateText=findViewById(R.id.date_text);
//            TextView infoText=findViewById(R.id.info_text);
//            TextView maxText=findViewById(R.id.max_text);
//            TextView minText=findViewById(R.id.min_text);
//            dateText.setText(forecast.date);
//            infoText.setText(forecast.more_info);
//            maxText.setText(forecast.tmp_max);
//            minText.setText(forecast.tmp_min);
//            forecastLayout.addView(view);
//        }

//        if (weather.aqi!=null){
//            aqiText.setText(weather.aqi.city.aqi);
//            pm25Text.setText(weather.aqi.city.pm25);
//        }

        comfortText.setText("舒适度："+weather.lifestyleList.get(0).lifeIndex+"\n"+weather.lifestyleList.get(0).lifeDetail);
        carWashText.setText("洗车指数："+weather.lifestyleList.get(6).lifeIndex+"\n"+weather.lifestyleList.get(6).lifeDetail);
        sportText.setText("运动指数："+weather.lifestyleList.get(3).lifeIndex+"\n"+weather.lifestyleList.get(3).lifeDetail);
            weatherLayout.setVisibility(View.VISIBLE);
        if (weather != null && "ok".equals(weather.status)) {
            Intent intent=new Intent(this,AutoUpdateService.class);
            startService(intent);
        }
        else {
            Toast.makeText(this,"自动获取天气失败",Toast.LENGTH_LONG).show();
        }
        }
        private void showAQIInfo(AQI aqi) {
            if (aqi != null) {
                aqiText.setText(aqi.airNow.qlty);
                pm25Text.setText(aqi.airNow.pm25);
            }

        }
}
