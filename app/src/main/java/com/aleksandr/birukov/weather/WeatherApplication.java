package com.aleksandr.birukov.weather;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.aleksandr.birukov.weather.database.AppDatabase;
import com.aleksandr.birukov.weather.database.WeatherDao;

public class WeatherApplication extends Application{

    public static WeatherApplication instance;

    //Для работы с БД
    private AppDatabase appdb;
    private WeatherDao database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appdb = Room.databaseBuilder(this, AppDatabase.class, "database")
                .allowMainThreadQueries()
                .build();
        database = appdb.weatherDao();
    }

    public static WeatherApplication getInstance() {
        return instance;
    }

    public WeatherDao getDatabase() {
        return database;
    }
}
