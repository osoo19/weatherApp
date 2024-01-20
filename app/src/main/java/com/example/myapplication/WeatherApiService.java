package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class WeatherApiService {
    private String apiUrl;
    private double latitude;
    private double longitude;
    private String point; // 都道府県
    private int pointFlag;  // 0 = 緯度経度で送信, 1 = 都道府県名で送信
    private Context context;

    public WeatherApiService(Context context, double latitude, double longitude, String point, int pointFlag) {
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.point = point;
        this.pointFlag = pointFlag;
        this.apiUrl = context.getString(R.string.five_day_weather_url);
    }

    public void makeRequest(final VolleyCallback callback) {
        // パラメーターをクエリ文字列に追加
        String fullUrl;
        if (pointFlag == 0) {
            fullUrl = apiUrl + "?APPID=" + context.getString(R.string.api_key) + "&lat=" + latitude + "&lon=" + longitude + "&units=metric&lang=ja";
        } else {
            fullUrl = apiUrl + "?APPID=" + context.getString(R.string.api_key) + "&q=" + point + "&units=metric&lang=ja";
        }

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, fullUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 成功
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // エラー
                        callback.onError(error.getMessage());
                    }
                });

        // リクエストをキューに追加
        queue.add(stringRequest);
    }

    public interface VolleyCallback {
        void onSuccess(String result);
        void onError(String error);
    }
}
