package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;

public class ShowWeatherFragment extends Fragment {


    public ShowWeatherFragment newInstance(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            String jsonData = args.getString("gameInfoData");
            JsonParser.getWeatherDataFromJson(jsonData);
        }
        return null;
    }

    public ShowWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_weather, container, false);

        String[] weatherData = new String[0];
        if (getArguments() != null) {
            weatherData = JsonParser.getWeatherDataFromJson(getArguments().getString("result"));
        }
        ListView listView = view.findViewById(R.id.weatherListView);
        WeatherAdapter adapter = new WeatherAdapter(requireContext(), weatherData);
        listView.setAdapter(adapter);

        return view;
    }

}