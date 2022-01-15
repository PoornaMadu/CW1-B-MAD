package com.example.weatherapplication.Listner;

import com.example.weatherapplication.Modal.Weather;

import java.util.List;

public interface IWeatherListner {
    void onLoadSuccess(List<Weather> weatherList);
    void onLoadFail(String message);
}
