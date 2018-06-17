package com.aleksandr.birukov.weather.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aleksandr.birukov.weather.R;

public class StatisticFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statistic, container, false);
        log("statistic");
        return v;
    }
    public void log(String text){
        Log.e("MyLog", text);
    }
}
