package com.example.myapplication;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

public class WeatherApiService implements Callable<String> {
    private String apiUrl;
    private String point;
    private Context context;

    private Handler uiHandler = new Handler(Looper.getMainLooper());

    public WeatherApiService(Context context, String point) {
        this.point = point;
        this.apiUrl = context.getString(R.string.five_day_weather_url);
        this.context = context;
    }

    @Override
    public String call() {
        HttpURLConnection urlConnection;
        InputStream inputStream;
        String result;
        StringBuilder str = new StringBuilder();

        try {
            // パラメーターをクエリ文字列に追加
            String fullUrl = apiUrl + "?APPID=" + context.getString(R.string.api_key) + "&q=" + point + "&units=metric&lang=ja";
            URL url = new URL(fullUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(30000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {

                inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                result = bufferedReader.readLine();

                while (result != null) {
                    str.append(result);
                    result = bufferedReader.readLine();
                }
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
}
