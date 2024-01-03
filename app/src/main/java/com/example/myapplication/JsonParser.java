package com.example.myapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    public static String[] getWeatherDataFromJson(String args) {
        List<String> weatherData = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(args);
            JSONArray listArray = jsonObject.getJSONArray("list");

            for (int i = 0; i < listArray.length(); i++) {
                JSONObject listItem = listArray.getJSONObject(i);
                JSONObject mainObject = listItem.getJSONObject("main");
                JSONArray weatherArray = listItem.getJSONArray("weather");

                String icon = weatherArray.getJSONObject(0).getString("icon");
                double temp = mainObject.getDouble("temp");
                long dt = listItem.getLong("dt");
                // dt を日本時間に変換
                LocalDateTime dateTime = Instant.ofEpochSecond(dt).atZone(ZoneId.of("Asia/Tokyo")).toLocalDateTime();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = dateTime.format(formatter);
                weatherData.add(icon);
                weatherData.add(String.valueOf(temp));
                weatherData.add(formattedDateTime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // List を String[] に変換して返す
        return weatherData.toArray(new String[0]);
    }
}
