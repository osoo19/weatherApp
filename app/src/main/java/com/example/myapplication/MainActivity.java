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
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private ExecutorService executor;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        executor = Executors.newSingleThreadExecutor();

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
        if (executor != null) {
            executor.shutdown();
        }
    }

    public void showWeatherResult(String point) {
        String weatherData = getWeatherData(point);
        if (Objects.equals(weatherData, "") || weatherData == null) {
            showRetryDialog(point);
        }
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

    // 通信を行う前にキャッシュを確認し、同日中のデータであれば通信をスキップ
    public String getWeatherData(String point) {
        String currentDate = getCurrentDate();
        String cacheKey = "weather_" + point + "_" + currentDate;

        // キャッシュからデータを取得
        String cachedData = getFromCache(cacheKey);

        if (cachedData != null && !cachedData.equals("")) {
            // キャッシュが存在する場合はキャッシュのデータを返す
            return cachedData;
        } else {
            // キャッシュが存在しない場合は通信を行い、結果をキャッシュに保存
            Future<String> future = executor.submit(new WeatherApiService(this, point));
            String result;
            try {
                result = future.get();
            } catch (ExecutionException | InterruptedException e) {
                // 通信失敗時の処理
                Log.e("WeatherData", "Communication failed: " + e.getMessage());
                return null;  // 通信失敗時はここで終了
            }
            saveToCache(cacheKey, result);
            return result;
        }
    }

    // やり直しボタンを表示するメソッド
    private void showRetryDialog(String point) {
        new AlertDialog.Builder(this)
                .setTitle("通信に失敗しました。")
                .setPositiveButton("リトライ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWeatherResult(point);
                    }
                })
                .setNegativeButton("戻る", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 戻るボタンが押されたときの処理
                        onBackPressed();
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

    // 位置情報の取得
    public void requestLocation() {
        // 位置情報の取得
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // パーミッションがある場合は位置情報を取得
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();
                Log.d("Location", "Latitude: " + latitude + ", Longitude: " + longitude);
            } else {
                Log.e("Location", "Last known location is null");
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            Log.e("Location", "Permission denied");
        }
    }
}