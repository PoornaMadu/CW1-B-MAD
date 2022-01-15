package com.example.weatherapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

public class MapActivity extends AppCompatActivity implements LocationListener {


    protected LocationManager locationManager;
    Const constants = new Const();
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    TextView polutionTxt,txtCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        polutionTxt = (TextView) findViewById(R.id.polutionTxt);

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


        navigation();
    }

    /**
     * get weather data from api call
     */
    private void getWeatherData(Double lang ,Double lati) {

        StringBuilder result = new StringBuilder();
        try {
            URL urls = new URL("https://api.openweathermap.org/data/2.5/air_pollution?lat="+lati.toString().trim()+"&lon="+lang.toString().trim()+"&&appid="+constants.APIKEY+"");
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

            try {
                JSONObject object = new JSONObject(result.toString());
                JSONArray weatherInfo =object.getJSONArray("list");

                JSONObject row = weatherInfo.getJSONObject(0);
                JSONObject components = new JSONObject(row.getString("components"));

                Double NO2 = Double.parseDouble(components.getString("no2"));
                Double PM10 = Double.parseDouble(components.getString("pm10"));
                Double O3 =Double.parseDouble( components.getString("o3"));

                if(NO2 < 50 && PM10 < 25 && O3 < 60){
                    polutionTxt.setText("Good");
                }else if(NO2 < 100 && PM10 < 50 && O3 < 120){
                    polutionTxt.setText("Fair");
                }else if(NO2 < 200 && PM10 < 90 && O3 < 180){
                    polutionTxt.setText("Moderate");
                }else if(NO2 < 400 && PM10 < 180 && O3 < 240){
                    polutionTxt.setText ("Poor");
                }else{
                    polutionTxt.setText("Vary Poor");
                }

                Log.d("","" + components);

                try {

                    Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
                    if (addresses.isEmpty()) {
                        txtCity.setText("Waiting for Location");
                    } else {
                        if (addresses.size() > 0) {
                            txtCity.setText(addresses.get(0).getFeatureName() + ",\n " + addresses.get(0).getCountryName());
                            //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // getFromLocation() may sometimes fail
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Navigation bar button click listeners
     */
    public void navigation(){
        ImageButton listBtn = (ImageButton) findViewById(R.id.listBtn);
        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        ImageButton locationBtn = (ImageButton) findViewById(R.id.locationBtn);

        listBtn.setOnClickListener(view -> startActivity(new Intent(MapActivity.this,ListActivity.class)));
        locationBtn.setOnClickListener(view -> startActivity(new Intent(MapActivity.this,SearchActivity.class)));
        homeBtn.setOnClickListener(view -> startActivity(new Intent(MapActivity.this,HomeActivity.class)));

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