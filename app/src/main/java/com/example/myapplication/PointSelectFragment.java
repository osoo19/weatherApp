package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PointSelectFragment extends Fragment {
    public PointSelectFragment() {
    }
    private Button tokyoButton;
    private Button hyogoButton;
    private Button ooitaButton;
    private Button hokkaidoButton;
    private Button currentLocationButton;
    public static PointSelectFragment newInstance(String param1, String param2) {
        PointSelectFragment fragment = new PointSelectFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_point_select, container, false);
        tokyoButton = view.findViewById(R.id.tokyo_button);
        hyogoButton = view.findViewById(R.id.hyogo_button);
        ooitaButton = view.findViewById(R.id.ooita_button);
        hokkaidoButton = view.findViewById(R.id.hokkaido_button);
        currentLocationButton = view.findViewById(R.id.current_location_button);


        tokyoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) requireActivity()).showWeatherResult("Tokyo");
            }
        });
        hyogoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) requireActivity()).showWeatherResult("Hyogo");
            }
        });
        ooitaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) requireActivity()).showWeatherResult("Oita");
            }
        });
        hokkaidoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) requireActivity()).showWeatherResult("Hokkaido");
            }
        });
        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) requireActivity()).showCurrentLocationWeatherResult();
            }
        });


        return view;
    }
}