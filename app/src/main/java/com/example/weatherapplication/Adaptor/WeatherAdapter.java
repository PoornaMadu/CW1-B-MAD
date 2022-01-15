package com.example.weatherapplication.Adaptor;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapplication.Modal.Weather;
import com.example.weatherapplication.R;

import java.util.List;

import butterknife.Unbinder;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.MyWeatherViewHolder> {

    private Context context;
    private List<Weather> weatherList;



    public WeatherAdapter(Context context, List<Weather> weatherList, Activity activity) {
        this.context = context;
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public MyWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyWeatherViewHolder((LayoutInflater.from(context).inflate(R.layout.weather_next_item_list,parent,false)));
    }

    @Override
    public void onBindViewHolder(@NonNull MyWeatherViewHolder holder, int position) {

        holder.tempTxt.setText(new StringBuffer().append(weatherList.get(position).getTemp()+(char) 0x00B0+"C"));
        holder.cityTxt.setText(new StringBuffer().append(weatherList.get(position).getCity()));
        holder.mintemptxt.setText(new StringBuffer().append(weatherList.get(position).getMinTemp()+(char) 0x00B0+"C"));
        holder.maxtempTxt.setText(new StringBuffer().append(weatherList.get(position).getMaxxTemp()+(char) 0x00B0+"C"));
        holder.descTxt.setText(new StringBuffer().append(weatherList.get(position).getDec()));
        holder.weatherTxt.setText(new StringBuffer("Weather : ").append(weatherList.get(position).getWeather()));
        holder.dateTimeTxt.setText(new StringBuffer("Date Time : ").append(weatherList.get(position).getDateTime()));
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public class MyWeatherViewHolder extends RecyclerView.ViewHolder{

        TextView tempTxt,cityTxt,mintemptxt,maxtempTxt,dateTimeTxt,weatherTxt,descTxt;


        Unbinder unbinder;
        public MyWeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            tempTxt = (TextView)itemView.findViewById(R.id.tempText);
            cityTxt = (TextView)itemView.findViewById(R.id.cityTxt);
            mintemptxt = (TextView)itemView.findViewById(R.id.mintempTxt);
            maxtempTxt = (TextView)itemView.findViewById(R.id.maxTempTxt);
            descTxt = (TextView)itemView.findViewById(R.id.descTxt);
            dateTimeTxt = (TextView)itemView.findViewById(R.id.dateTimeTxt);
            weatherTxt = (TextView)itemView.findViewById(R.id.weatherTxt);

        }
    }
}
