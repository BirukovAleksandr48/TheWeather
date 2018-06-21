package com.aleksandr.birukov.weather;

import com.aleksandr.birukov.weather.model.Weather;
import com.aleksandr.birukov.weather.model.WeatherForecast;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class WeatherApi {
    public static final String KEY = "2e08a380cc5fbd2f58af47783f3a8562";
    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private static Retrofit retrofit;

    private WeatherApi(){}

    public interface ApiInterface {
        @GET("weather")
        Call<Weather> getToday(
                @Query("lat") double lat,
                @Query("lon") double lon,
                @Query("units") String units,
                @Query("appid") String appid
        );

        @GET("forecast")
        Call<WeatherForecast> getForecast(
                @Query("lat") double lat,
                @Query("lon") double lon,
                @Query("units") String units,
                @Query("appid") String appid
        );
    }

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}