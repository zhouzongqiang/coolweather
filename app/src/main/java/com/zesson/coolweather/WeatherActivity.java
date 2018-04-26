package com.zesson.coolweather;

import android.content.SharedPreferences;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zesson.coolweather.gson.Forecast;
import com.zesson.coolweather.gson.Weather;
import com.zesson.coolweather.util.HttpUtil;
import com.zesson.coolweather.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/4/16.
 */

public class WeatherActivity extends AppCompatActivity {

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
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        Log.i("tag","WeatherActivity");
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        //aqiText = findViewById(R.id.)
//        pm25Text = findViewById(R.id)
//        comfortText = findViewById(R.id.)

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = preferences.getString("weather",null);


        Log.i("tag","in here");
        if (weatherString!=null)
        {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else{
            String weatherId = getIntent().getStringExtra("weather_id");
           // weatherLayout.setVisibility(View.VISIBLE);
            requestWeather(weatherId);
        }

    }

    public void  requestWeather(final String weatherId)
    {
        Log.i("tag","id=="+weatherId);
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+"&key=fcb5640a014a4697bf1395f08d6d2a08";
       // String weatherUrl = "http://guolin.tech/api/weather?cityid=CN101080203&key=fcb5640a014a4697bf1395f08d6d2a08";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseText = response.body().string();
                Weather weather  = Utility.handleWeatherResponse(responseText);
                if (weather!=null&&"ok".equals(weather.status))
                {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                    SharedPreferences.Editor editor =sharedPreferences.edit();
                    editor.putString("weather",responseText);
                    editor.apply();
                    showWeatherInfo(weather);
                }
            }
        });
    }

    public void showWeatherInfo(Weather weather)
    {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature +"°C";
        String weathInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weathInfo);
        forecastLayout.removeAllViews();

        int count = 0;
        for(Forecast forecast:weather.forecastList)
        {
            count +=1;
            Log.i("tag",""+count);
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            Log.i("tag","max=="+forecast.temperature.max);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }

    }
}
