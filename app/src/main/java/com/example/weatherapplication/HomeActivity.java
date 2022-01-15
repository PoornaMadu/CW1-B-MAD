package com.example.weatherapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import eu.long1.spacetablayout.SpaceTabLayout;


public class HomeActivity extends AppCompatActivity implements LocationListener {

    protected LocationManager locationManager;
    TextView tempTxt,cityTxt,mintemptxt,maxtempTxt,weatherTxt,desTxt,windSpeedTxt,windDigTxt,sunriseTxt,sunsetTxt;
    TextView txtCity;
    LinearLayout dataSet;
    Const constants = new Const();
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //get internet access for app
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        txtCity = (TextView) findViewById(R.id.cityTxt);
        tempTxt = (TextView)findViewById(R.id.tempText);
        sunsetTxt = (TextView)findViewById(R.id.sunsetTxt);
        weatherTxt = (TextView)findViewById(R.id.weatherTxt);
        windDigTxt = (TextView)findViewById(R.id.windDigTxt);
        sunriseTxt = (TextView)findViewById(R.id.sunriseTxt);
        cityTxt = (TextView)findViewById(R.id.cityTxt);
        windSpeedTxt = (TextView)findViewById(R.id.windSpeedTxt);
        desTxt = (TextView)findViewById(R.id.desTxt);
        mintemptxt = (TextView)findViewById(R.id.mintempTxt);
        maxtempTxt = (TextView)findViewById(R.id.maxTempTxt);
        dataSet = (LinearLayout) findViewById(R.id.dataLayout);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);
            return ;
        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);

        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        navigation();


    }


    /**
     * get weather data from api call
     */
    private void getWeatherData(Double lang ,Double lati) {

        StringBuilder result = new StringBuilder();
        try {
            URL urls = new URL("https://api.openweathermap.org/data/2.5/weather?lat="+lati.toString().trim()+"&lon="+lang.toString().trim()+"&&appid="+constants.APIKEY+"");
            HttpURLConnection httpURLConnection = (HttpURLConnection) urls.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";

            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            showdata(result,lati,lang);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * show weather data
     * @param result
     */
    private void showdata(StringBuilder result,Double latitude, Double longitude) {

        if (result != null) {
            dataSet.setVisibility(View.VISIBLE);
            try {
                JSONObject object = new JSONObject(result.toString());
                String weatherInfo = object.getString("main");
                String windInfo = object.getString("wind");
                String dayInfo = object.getString("sys");

                JSONArray skyInfo =object.getJSONArray("weather");
                JSONObject mainWeather = skyInfo.getJSONObject(0);

                weatherTxt.setText(mainWeather.getString("main"));
                desTxt.setText(mainWeather.getString("description"));


                JSONObject wind = new JSONObject(windInfo.toString());
                windDigTxt.setText("degree : " +wind.getString("deg") +(char) 0x00B0+"");
                windSpeedTxt.setText("Speed : " + wind.getString("speed")+ "Kmp/h");


                JSONObject day = new JSONObject(dayInfo.toString());
                String sunrise = day.getString("sunrise");
                String sunset = day.getString("sunset");


                Date date = new Date(Long.parseLong(sunrise) * 1000L);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                sunriseTxt.setText(sdf.format(date));

                Date date1 = new Date(Long.parseLong(sunset) * 1000L);
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                sunsetTxt.setText(sdf1.format(date1));

                JSONObject temp = new JSONObject(weatherInfo.toString());
                tempTxt.setText( temp.getString("temp")+(char) 0x00B0+"C");

                JSONObject tempMin = new JSONObject(weatherInfo.toString());
                mintemptxt.setText(tempMin.getString("temp_min")+(char) 0x00B0+"C");


                try {

                    Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
                    List <Address> addresses = geo.getFromLocation(latitude, longitude, 1);
                    if (addresses.isEmpty()) {
                        cityTxt.setText("Waiting for Location");
                    } else {
                        if (addresses.size() > 0) {
                            cityTxt.setText(addresses.get(0).getFeatureName() + ",\n " + addresses.get(0).getCountryName());
                            dataSet.setVisibility(View.VISIBLE);
                            //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // getFromLocation() may sometimes fail
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            dataSet.setVisibility(View.GONE);
        }
    }


    /**
     * Navigation bar button click listners
     */
    public void navigation() {
        ImageButton listBtn = (ImageButton) findViewById(R.id.listBtn);
        ImageButton locationBtn = (ImageButton) findViewById(R.id.locationBtn);
        ImageButton mapBtn = (ImageButton) findViewById(R.id.mapBtn);

        listBtn.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, ListActivity.class)));
        mapBtn.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, MapActivity.class)));
        locationBtn.setOnClickListener(view -> startActivity(new Intent(HomeActivity.this, SearchActivity.class)));

    }

    @Override
    public void onLocationChanged(Location location) {
        txtCity = (TextView) findViewById(R.id.cityTxt);
        txtCity.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());

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