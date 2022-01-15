package com.example.weatherapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapplication.Adaptor.WeatherAdapter;
import com.example.weatherapplication.Listner.IWeatherListner;
import com.example.weatherapplication.Modal.Weather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

public class ListActivity extends AppCompatActivity implements LocationListener , IWeatherListner  {

    RecyclerView recycleWeather;
    Const constants = new Const();
    IWeatherListner iWeatherListner;
    protected LocationManager locationManager;
    ArrayList<Weather> weatherArray = new ArrayList<Weather>();
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //get internet access for app
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);
            return ;
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);

        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


        iWeatherListner = this;

        recycleWeather = findViewById(R.id.recycle_weather);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycleWeather.setLayoutManager(linearLayoutManager);
        recycleWeather.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));



        navigation();
    }

    /**
     * Navigation bar button click listeners
     */
    public void navigation() {
        ImageButton mapBtn = findViewById(R.id.mapBtn);
        ImageButton homeBtn = findViewById(R.id.homeBtn);
        ImageButton locationBtn = findViewById(R.id.locationBtn);

        mapBtn.setOnClickListener(view -> startActivity(new Intent(ListActivity.this, MapActivity.class)));
        locationBtn.setOnClickListener(view -> startActivity(new Intent(ListActivity.this, SearchActivity.class)));
        homeBtn.setOnClickListener(view -> startActivity(new Intent(ListActivity.this, HomeActivity.class)));

    }

    /**
     * get weather data from api call
     */
    private void getWeatherData(Double lang ,Double lati) {

        StringBuilder result = new StringBuilder();
        try {
            URL urls = new URL("https://api.openweathermap.org/data/2.5/forecast?lat="+lati.toString().trim()+"&lon="+lang.toString().trim()+"&&appid="+constants.APIKEY+"");
            HttpURLConnection httpURLConnection = (HttpURLConnection) urls.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";

            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }

            JSONObject object = new JSONObject(result.toString());
            String list = object.getString("list");

            JSONArray array =object.getJSONArray("list");;
            for (int i = 0; i < array.length(); i++) {
                JSONObject row = array.getJSONObject(i);

                JSONObject mainInfo = new JSONObject(row.getString("main"));
                JSONArray weatherInfo =row.getJSONArray("weather");

                Weather weather = new Weather();
                JSONObject mainWeather = weatherInfo.getJSONObject(0);
                weather.setWeather(mainWeather.getString("main"));

                weather.setDateTime(row.getString("dt_txt"));
                weather.setMaxxTemp(mainInfo.getString("temp_max"));
                weather.setMinTemp(mainInfo.getString("temp_min"));
                weather.setDec(mainWeather.getString("description"));
                weather.setTemp(mainInfo.getString("temp"));

                try {

                    Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
                    List <Address> addresses = geo.getFromLocation(lati, lang, 1);
                    if (addresses.isEmpty()) {

                    } else {
                        if (addresses.size() > 0) {
                            weather.setCity(addresses.get(0).getFeatureName() + ",\n " + addresses.get(0).getCountryName());
                            //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // getFromLocation() may sometimes fail
                }
                weatherArray.add(weather);
            }
            onLoadSuccess(weatherArray);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }



    @Override
    public void onLoadSuccess(List<Weather> weatherList) {
        WeatherAdapter adapter = new WeatherAdapter(this, weatherList, this);
        recycleWeather.setAdapter(adapter);
    }

    @Override
    public void onLoadFail(String message) {
        Toast.makeText(this, "Server error code :500 ! Can not get data!", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLocationChanged(Location location) {

        getWeatherData(location.getLongitude(),location.getLatitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
}