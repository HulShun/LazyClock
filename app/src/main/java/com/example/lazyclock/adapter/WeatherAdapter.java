package com.example.lazyclock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lazyclock.R;
import com.example.lazyclock.bean.Weather;
import com.example.lazyclock.utils.WeatherUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2016/1/13.
 */
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.MyViewHolder> {
    private Context mContenxt;
    private Weather mWeather;
    private List<Weather.DailyBean> mDaily;
    private WeatherUtil mWeatherUtil;

    private SimpleDateFormat formatter1, formatter2;

    public WeatherAdapter(Context context, Weather weather) {
        mContenxt = context;
        mWeather = weather;
        mDaily = mWeather.getDaily();
        formatter1 = new SimpleDateFormat("MM月dd日  EEEE", Locale.CHINA);
        formatter2 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        mWeatherUtil = new WeatherUtil(context);
    }

    @Override
    public WeatherAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.activity_weather_item, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Weather.DailyBean bean = mDaily.get(position);
        try {
            String day = formatter1.format(formatter2.parse(bean.getDate()));
            holder.mWeatherItemDay.setText(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.mWeatherItemTemp.setText(bean.getTemp());
        String text = bean.getText();
        holder.mWeatherItemText.setText(text);
        mWeatherUtil.setImageToWeather(text, holder.mWeatherItemImage);
    }


    @Override
    public int getItemCount() {
        return mWeather.getDaily().size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mWeatherItemDay;
        public TextView mWeatherItemTemp;
        public TextView mWeatherItemText;
        public ImageView mWeatherItemImage;


        public MyViewHolder(View itemView) {
            super(itemView);
            mWeatherItemDay = (TextView) itemView.findViewById(R.id.weather_item_day);
            mWeatherItemTemp = (TextView) itemView.findViewById(R.id.weather_item_temp);
            mWeatherItemText = (TextView) itemView.findViewById(R.id.weather_item_text);
            mWeatherItemImage = (ImageView) itemView.findViewById(R.id.weather_item_image);

        }
    }

}
