package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PointSelectFragment pointSelectFragment = new PointSelectFragment();
        fragmentTransaction.replace(R.id.fragmentContainer, pointSelectFragment, "pointSelectFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //　天気予報結果を表示
    public void showWeatherResult(String point) {
        //　キャッシュを確認
        String currentDate = getCurrentDate();
        String cacheKey = "weather_" + point + "_" + currentDate;
        String cachedData = getFromCache(cacheKey);

        if (cachedData != null && !cachedData.equals("")) {
            // キャッシュが存在する場合はキャッシュのデータを返す
            showResultFragment(cachedData);
            return;
        }

        // キャッシュが存在しない場合は通信を行い、結果をキャッシュに保存
        WeatherApiService weatherApiService = new WeatherApiService(this, 0, 0, point, 1);
        weatherApiService.makeRequest(new WeatherApiService.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                // リクエスト成功時の処理
                saveToCache(cacheKey, result);
                showResultFragment(result);

            }
            @Override
            public void onError(String error) {
                // リクエストエラーダイアログ表示
                showRetryDialog(point, 1);
            }
        });
    }

    // キャッシュを保存するメソッド
    private void saveToCache(String key, String data) {
        SharedPreferences preferences = this.getSharedPreferences("WeatherCache", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, data);
        editor.apply();
    }

    // キャッシュからデータを取得するメソッド
    private String getFromCache(String key) {
        SharedPreferences preferences = this.getSharedPreferences("WeatherCache", MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    private void showResultFragment(String weatherData) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ShowWeatherFragment showWeatherFragment = new ShowWeatherFragment();
        Bundle args = new Bundle();
        args.putString("result", weatherData);
        showWeatherFragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragmentContainer, showWeatherFragment, "showWeatherFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    // やり直しボタンを表示するメソッド
    private void showRetryDialog(String point, int pointFlag) {
        new AlertDialog.Builder(this)
                .setTitle("通信に失敗しました。")
                .setPositiveButton("リトライ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //  リトライ先を判定
                        if (pointFlag == 1) {
                            //　都道府県
                            showWeatherResult(point);
                        } else {
                            //　現在地
                            showCurrentLocationWeatherResult();
                        }
                    }
                })
                .show();
    }

    // 現在の日付を取得するメソッド
    public static String getCurrentDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return currentDate.format(formatter);
    }

    // 位置情報を取得
    private Map<String, Double> requestLocation() {
        Map<String, Double> locationMap = new HashMap<>();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // パーミッションがある場合は位置情報を取得
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                locationMap.put("latitude", lastKnownLocation.getLatitude());
                locationMap.put("longitude", lastKnownLocation.getLongitude());
            } else {
                Log.e("Location", "Last known location is null");
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        return locationMap;
    }

    //　現在地の天気予報を表示
    public void showCurrentLocationWeatherResult() {
        Map<String, Double> locationMap = requestLocation();

        //　　位置情報が空の場合
        if (locationMap.isEmpty()) {
            return;
        }

        double latitude = locationMap.get("latitude");
        double longitude = locationMap.get("longitude");

        WeatherApiService weatherApiService = new WeatherApiService(this, latitude, longitude, null, 0);
        weatherApiService.makeRequest(new WeatherApiService.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                // リクエスト成功時の処理
                showResultFragment(result);
            }

            @Override
            public void onError(String error) {
                // リクエストエラー時の処理
                showRetryDialog(null, 0);
            }
        });
    }


}