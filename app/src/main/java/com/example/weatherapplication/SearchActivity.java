package com.example.weatherapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchActivity extends AppCompatActivity {

    //Components
    EditText cityName;
    Button searchBtn;
    TextView tempTxt,cityTxt,mintemptxt,maxtempTxt;
    LinearLayout dataSet;
    Const constants = new Const();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //get internet access for app
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        cityName = findViewById(R.id.searchCityTxt);
        searchBtn = findViewById(R.id.searchBtn);
        tempTxt = (TextView)findViewById(R.id.tempText);
        cityTxt = (TextView)findViewById(R.id.cityTxt);
        mintemptxt = (TextView)findViewById(R.id.mintempTxt);
        maxtempTxt = (TextView)findViewById(R.id.maxTempTxt);
        dataSet = (LinearLayout) findViewById(R.id.dataLayout);

        getWeatherData();
        navigation();
    }

    /**
     * get weather data from api call
     */
    private void getWeatherData() {
        searchBtn.setOnClickListener(view -> {
            String city = cityName.getText().toString();

            if(city.equals("")){
                cityName.setError("Enter city name !");
                return;
            }

            StringBuilder result = new StringBuilder();
            try {
                URL urls = new URL("https://api.openweathermap.org/data/2.5/weather?q="+city.trim()+"&appid="+constants.APIKEY+"");
                HttpURLConnection httpURLConnection = (HttpURLConnection) urls.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";

                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                showdata(result);
            } catch (Exception e) {
                e.printStackTrace();

            }

        });
    }

    /**
     * show weather data
     * @param result
     */
    private void showdata(StringBuilder result) {

        if (result != null) {
            dataSet.setVisibility(View.VISIBLE);
            try {
                JSONObject object = new JSONObject(result.toString());
                String weatherInfo = object.getString("main");

                JSONObject temp = new JSONObject(weatherInfo.toString());
                tempTxt.setText(temp.getString("temp")+(char) 0x00B0+"C");

                JSONObject tempMin = new JSONObject(weatherInfo.toString());
                mintemptxt.setText(tempMin.getString("temp_min")+(char) 0x00B0+"C");

                JSONObject temptMax = new JSONObject(weatherInfo.toString());
                maxtempTxt.setText(temptMax.getString("temp_max")+(char) 0x00B0+"C");

                cityTxt.setText(cityName.getText().toString().trim());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            dataSet.setVisibility(View.GONE);
        }
    }

    /**
     * Navigation bar button click listeners
     */
    public void navigation(){
        ImageButton listBtn = (ImageButton) findViewById(R.id.listBtn);
        ImageButton homeBtn = (ImageButton) findViewById(R.id.homeBtn);
        ImageButton mapBtn = (ImageButton) findViewById(R.id.mapBtn);

        listBtn.setOnClickListener(view -> startActivity(new Intent(SearchActivity.this,ListActivity.class)));
        mapBtn.setOnClickListener(view -> startActivity(new Intent(SearchActivity.this,MapActivity.class)));
        homeBtn.setOnClickListener(view -> startActivity(new Intent(SearchActivity.this,HomeActivity.class)));

    }

}