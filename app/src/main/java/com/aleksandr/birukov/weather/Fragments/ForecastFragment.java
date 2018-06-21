package com.aleksandr.birukov.weather.Fragments;


import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aleksandr.birukov.weather.Constants;
import com.aleksandr.birukov.weather.R;
import com.aleksandr.birukov.weather.WeatherApi;
import com.aleksandr.birukov.weather.WeatherApplication;
import com.aleksandr.birukov.weather.adapters.WeatherAdapterDaily;
import com.aleksandr.birukov.weather.adapters.WeatherAdapterWeekly;
import com.aleksandr.birukov.weather.database.AppDatabase;
import com.aleksandr.birukov.weather.database.Converter;
import com.aleksandr.birukov.weather.database.WeatherDB;
import com.aleksandr.birukov.weather.database.WeatherDao;
import com.aleksandr.birukov.weather.model.Weather;
import com.aleksandr.birukov.weather.model.WeatherForecast;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastFragment extends Fragment {
    WeatherApi.ApiInterface api;
    GridView gvForecastWeek, gvForecastDay;
    ImageButton btnFind;
    TextView tvCurDay;

    double lon=0, lat=0;
    String cityName;
    final int PLACE_PICKER_REQUEST = 1;
    String lastUpdateTime;
    String units = "metric";
    String key = WeatherApi.KEY;
    WeatherForecast mWeatherForecast;
    PlaceAutocompleteFragment autocompleteFragment;
    SharedPreferences.Editor spEditor;
    SharedPreferences sharedPreferences;
    WeatherDao db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forecast, container, false);
        api = WeatherApi.getInstance().create(WeatherApi.ApiInterface.class);
        db = WeatherApplication.getInstance().getDatabase();

        gvForecastWeek = v.findViewById(R.id.gv_forecast_week);
        gvForecastDay = v.findViewById(R.id.gv_forecast_day);
        btnFind = v.findViewById(R.id.btn_find);
        tvCurDay = v.findViewById(R.id.tv_cur_day);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                updatePlace(place);
                updateWeather();
            }

            @Override
            public void onError(Status status) {
                Log.i("MyLog", "An error occurred: " + status);
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        gvForecastWeek.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvCurDay.setText(new SimpleDateFormat("EEEE").format(mWeatherForecast.getDailyForcast(position).get(0).getDate().getTime()));
                gvForecastDay.setAdapter(new WeatherAdapterDaily(
                        getActivity(), mWeatherForecast.getDailyForcast(position)));
            }
        });

        sharedPreferences = getActivity().getPreferences(getActivity().MODE_PRIVATE);
        spEditor = sharedPreferences.edit();

        cityName = sharedPreferences.getString(Constants.SP_KEY_CITY_NAME, null);
        lat = sharedPreferences.getFloat(Constants.SP_KEY_CITY_LAT, 0);
        lon = sharedPreferences.getFloat(Constants.SP_KEY_CITY_LON, 0);
        lastUpdateTime = sharedPreferences.getString(Constants.SP_KEY_DATE_UPDATE, null);

        if(cityName != null)
            autocompleteFragment.setText(cityName);

        mWeatherForecast = new WeatherForecast(Converter.convertFromDB(db.getAll()));
        if (mWeatherForecast.getForecast().size()>0)
            updateUI();
//тут нужно обновить прогноз
        return v;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PLACE_PICKER_REQUEST:
                if (resultCode != getActivity().RESULT_OK)
                    return;

                Place place = PlacePicker.getPlace(getActivity(), data);
                updatePlace(place);
                updateWeather();
        }
    }
    public void updateWeather(){
        Call<WeatherForecast> callForecast = api.getForecast(lat, lon, units, key);
        callForecast.enqueue(new Callback<WeatherForecast>() {
            @Override
            public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
                mWeatherForecast = response.body();
                if (response.isSuccessful()) {
                    updateUI();
                    gvForecastDay.setAdapter(null);
                    saveToDB();
                }
            }

            @Override
            public void onFailure(Call<WeatherForecast> call, Throwable t) {

            }
        });
    }
    public void saveToDB(){
        List<WeatherDB> weatherDBlist = Converter.convertForDB(mWeatherForecast.getForecast());

        db.nukeTable();
        for (WeatherDB w : weatherDBlist){
            db.insert(w);
        }

        lastUpdateTime = new SimpleDateFormat("H:mm dd.MM")
                .format(Calendar.getInstance().getTime());

        spEditor.putString(Constants.SP_KEY_CITY_NAME, cityName)
                .putFloat(Constants.SP_KEY_CITY_LAT, Double.doubleToRawLongBits(lat))
                .putFloat(Constants.SP_KEY_CITY_LON, Double.doubleToRawLongBits(lon))
                .putString(Constants.SP_KEY_DATE_UPDATE, lastUpdateTime)
                .commit();
    }

    public void updateUI(){
        gvForecastWeek.setAdapter(new WeatherAdapterWeekly(getActivity(), mWeatherForecast.getWeeklyForcast()));
    }

    public void updatePlace(Place place){
        lat = place.getLatLng().latitude;
        lon = place.getLatLng().longitude;

        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(lat, lon, 1);
            if (addresses.size() > 0) {
                cityName = addresses.get(0).getLocality();
                autocompleteFragment.setText(cityName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void log(String text){
        Log.e("MyLog", text);
    }
}
