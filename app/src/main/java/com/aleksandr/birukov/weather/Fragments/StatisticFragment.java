package com.aleksandr.birukov.weather.Fragments;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aleksandr.birukov.weather.Constants;
import com.aleksandr.birukov.weather.R;
import com.aleksandr.birukov.weather.WeatherApplication;
import com.aleksandr.birukov.weather.database.Converter;
import com.aleksandr.birukov.weather.database.WeatherDao;
import com.aleksandr.birukov.weather.model.Weather;
import com.aleksandr.birukov.weather.model.WeatherForecast;
import com.anychart.anychart.AnyChart;
import com.anychart.anychart.AnyChartView;
import com.anychart.anychart.Cartesian;
import com.anychart.anychart.CartesianSeriesLine;
import com.anychart.anychart.DataEntry;
import com.anychart.anychart.EnumsAnchor;
import com.anychart.anychart.Mapping;
import com.anychart.anychart.MarkerType;
import com.anychart.anychart.Pie;
import com.anychart.anychart.Set;
import com.anychart.anychart.Stroke;
import com.anychart.anychart.Tooltip;
import com.anychart.anychart.TooltipPositionMode;
import com.anychart.anychart.ValueDataEntry;
import com.savvi.rangedatepicker.CalendarPickerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class StatisticFragment extends Fragment {
    ImageButton btn;
    TextView tv;
    CalendarPickerView calendar;
    DateFormat format;
    public static final int CODE_TIME_RANGE = 1;
    WeatherDao db;
    WeatherForecast mWeatherForecast;
    Date start, end;
    AnyChartView anyChartView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statistic, container, false);

        db = WeatherApplication.getInstance().getDatabase();
        mWeatherForecast = new WeatherForecast(Converter.convertFromDB(db.getAll()));
        format = new SimpleDateFormat("d.MM H:mm");


        btn = v.findViewById(R.id.btn_date);
        tv = v.findViewById(R.id.tv_selected_date);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateRangeFragment fragment = new DateRangeFragment();
                fragment.setTargetFragment(StatisticFragment.this, CODE_TIME_RANGE);
                fragment.show(getFragmentManager(), fragment.getClass().getName());
            }
        });

        anyChartView = v.findViewById(R.id.any_chart_view);
        /*Cartesian cartesian = AnyChart.line();

        List<Weather> data = mWeatherForecast.getWeeklyForcast();

        cartesian.setAnimation(true);
        cartesian.setPadding(10d, 20d, 5d, 20d);
        log("2");
        cartesian.getCrosshair().setEnabled(true);
        cartesian.getCrosshair()
                .setYLabel(true)
                .setYStroke((Stroke) null, null, null, null, null);

        cartesian.getYAxis().setTitle("Температура(\u00B0C)");

        List<DataEntry> seriesData = new ArrayList<>();
        for (Weather w : data){
            if(w.getTime()*1000 >= 1529580279844l && w.getTime()*1000 <= 1530013278544l) {
                log("+");
                seriesData.add(new CustomDataEntry(format.format(w.getDate().getTime()), w.getTemperature()));
            }
        }
        //3246 3541 2816 2092
        log("3");
        Set set = new Set(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

        CartesianSeriesLine series1 = cartesian.line(series1Mapping);
        series1.setName("Температура");


        cartesian.getLegend().setEnabled(true);
        cartesian.getLegend().setFontSize(20d);
        cartesian.getLegend().setPadding(0d, 0d, 10d, 0d);
        log("4");
        anyChartView.setChart(cartesian);
        /*Cartesian cartesian = AnyChart.line();

        cartesian.setAnimation(true);

        cartesian.setPadding(10d, 20d, 5d, 20d);

        cartesian.getCrosshair().setEnabled(true);
        cartesian.getCrosshair()
                .setYLabel(true)
                .setYStroke((Stroke) null, null, null, null, null);

        cartesian.getTooltip().setPositionMode(TooltipPositionMode.POINT);

        cartesian.setTitle("Trend of Sales of the Most Popular Products of ACME Corp.");

        cartesian.getYAxis().setTitle("Number of Bottles Sold (thousands)");
        cartesian.getXAxis().getLabels().setPadding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();
        seriesData.add(new CustomDataEntry("1986", 3.6, 2.3, 2.8));
        seriesData.add(new CustomDataEntry("1987", 7.1, 4.0, 4.1));
        seriesData.add(new CustomDataEntry("1988", 8.5, 6.2, 5.1));

        Set set = new Set(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

        CartesianSeriesLine series1 = cartesian.line(series1Mapping);
        series1.setName("Brandy");
        series1.getHovered().getMarkers().setEnabled(true);
        series1.getHovered().getMarkers()
                .setType(MarkerType.CIRCLE)
                .setSize(4d);
        series1.getTooltip()
                .setPosition("right")
                .setAnchor(EnumsAnchor.LEFT_CENTER)
                .setOffsetX(5d)
                .setOffsetY(5d);

        CartesianSeriesLine series2 = cartesian.line(series2Mapping);
        series2.setName("Whiskey");
        series2.getHovered().getMarkers().setEnabled(true);
        series2.getHovered().getMarkers()
                .setType(MarkerType.CIRCLE)
                .setSize(4d);
        series2.getTooltip()
                .setPosition("right")
                .setAnchor(EnumsAnchor.LEFT_CENTER)
                .setOffsetX(5d)
                .setOffsetY(5d);

        CartesianSeriesLine series3 = cartesian.line(series3Mapping);
        series3.setName("Tequila");
        series3.getHovered().getMarkers().setEnabled(true);
        series3.getHovered().getMarkers()
                .setType(MarkerType.CIRCLE)
                .setSize(4d);
        series3.getTooltip()
                .setPosition("right")
                .setAnchor(EnumsAnchor.LEFT_CENTER)
                .setOffsetX(5d)
                .setOffsetY(5d);

        cartesian.getLegend().setEnabled(true);
        cartesian.getLegend().setFontSize(13d);
        cartesian.getLegend().setPadding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);*/
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        start = (Date) data.getExtras().get(Constants.KEY_DIALOG_RESULT_START);
        end = (Date) data.getExtras().get(Constants.KEY_DIALOG_RESULT_END);
        if(end == null){
            log("end = null");
        }
        if(start == null){
            log("start = null");
        }
        tv.setText(format.format(start) + " - " + format.format(end));

        updateGraph();
    }

    public void updateGraph(){
        List<Weather> data = mWeatherForecast.getForecast();
        ArrayList<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Calendar cal_end = Calendar.getInstance();

        calendar.setTime(start);
        cal_end.setTime(end);
        log(String.valueOf(data.size()));
        while(calendar.get(Calendar.DAY_OF_MONTH) <= cal_end.get(Calendar.DAY_OF_MONTH)){
            dates.add(format.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        Cartesian cartesian = AnyChart.line();

        cartesian.setAnimation(true);
        //cartesian.setPadding(10d, 20d, 5d, 20d);

        cartesian.getCrosshair().setEnabled(true);
        cartesian.getCrosshair()
                .setYLabel(true)
                .setYStroke((Stroke) null, null, null, null, null);

        //cartesian.getYAxis().setTitle("Температура(\u00B0C)");
        cartesian.getXAxis().getLabels().setPadding(5d, 5d, 20d, 5d);

        cartesian.getXAxis().setStaggerLines(5);

        List<DataEntry> seriesData = new ArrayList<>();
        for (Weather w : data){
            if(w.getTime()*1000 >= start.getTime() && w.getTime()*1000 <= end.getTime()) {
                seriesData.add(new ValueDataEntry(
                        format.format(w.getDate().getTime()), w.getTemperature()));
            }
        }
        Set set = new Set(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        CartesianSeriesLine series1 = cartesian.line(series1Mapping);
        series1.setName("Температура, \u00B0C");
        cartesian.getLegend().setEnabled(true);
        cartesian.getLegend().setFontSize(20d);
        cartesian.getLegend().setPadding(0d, 0d, 10d, 0d);
        //cartesian.setData(seriesData);

        anyChartView.setChart(cartesian);


    }
    public void log(String text){
        Log.e("MyLog", text);
    }

}
