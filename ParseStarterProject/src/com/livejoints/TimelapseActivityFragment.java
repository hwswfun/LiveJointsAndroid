package com.livejoints;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.livejoints.data.ParseSensorSummary;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class TimelapseActivityFragment extends Fragment {

    private final static String TAG = TimelapseActivityFragment.class.getSimpleName();

    private CandleStickChart mChart;

    public TimelapseActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timelapse, container, false);

        mChart = (CandleStickChart) v.findViewById(R.id.chart1);

        setupChart();
        refreshData();
        setHasOptionsMenu(true);

        return v;
    }


    private void setupChart() {


        mChart.setDescription("Today");
        mChart.setVisibleYRangeMaximum(200, YAxis.AxisDependency.LEFT);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(2);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.setEnabled(false);
        leftAxis.setLabelCount(10, false);
        leftAxis.setAxisMaxValue(200f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setStartAtZero(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
//        rightAxis.setStartAtZero(false);


        mChart.getLegend().setEnabled(false);

        // Legend l = mChart.getLegend();
        // l.setPosition(LegendPosition.BELOW_CHART_CENTER);
        // l.setFormSize(8f);
        // l.setFormToTextSpace(4f);
        // l.setXEntrySpace(6f);

        // mChart.setDrawLegend(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_timelapse, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                getData();
                break;
        }
        return true;
    }


    public void refreshData() {
        getData();
    }

    private void getData() {
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        Log.d(TAG, "query for:" + ParseSensorSummary.class.getSimpleName());

        ParseQuery<ParseSensorSummary> query = ParseQuery.getQuery(ParseSensorSummary.class);
        //query.whereEqualTo("playerName", "Dan Stemkoski");

        query.orderByDescending("createdAt");
        query.setLimit(5);


        query.findInBackground(new FindCallback<ParseSensorSummary>() {
            public void done(List<ParseSensorSummary> sensorSummaryList, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + sensorSummaryList.size() + " scores");
                    drawChart(sensorSummaryList);
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }


    private void drawChart(List<ParseSensorSummary> readings) {

        mChart.resetTracking();

        ArrayList<CandleEntry> yVals1 = new ArrayList<CandleEntry>();

        ParseSensorSummary pss=null;
        for (int i = 0; i < readings.size(); i++) {
            pss = readings.get(i);
            pss.analyze();


            float avg = (float) pss.getAverage();
            float stddev = (float) pss.getStddev();


            float highStddev = (float) (avg + stddev);
            float lowStddev = (float) (avg - stddev);

            float high = (float) pss.getHigh();
            float low = (float) pss.getLow();

            // clamp
            if (lowStddev < low) lowStddev=low;
            if (highStddev > high) highStddev = high;

            Log.d(TAG,"createdAt:"+pss.getCreatedAt().toString()+"  low:"+low+", high:"+high+", avg:"+avg+", stddev:"+stddev+", lowStddev:"+lowStddev+", highStddev:"+highStddev);


            yVals1.add(new CandleEntry(i, high, low, highStddev, lowStddev));
        }

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < readings.size(); i++) {
            if (i==0) xVals.add(""+0);
            else xVals.add("-"+i);
        }

        CandleDataSet set1 = new CandleDataSet(yVals1, "Data Set");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set1.setColor(Color.rgb(80, 80, 80));
        set1.setShadowColor(Color.DKGRAY);
        set1.setShadowWidth(0.7f);
        set1.setDecreasingColor(Color.RED);
        set1.setDecreasingPaintStyle(Paint.Style.STROKE);
        set1.setIncreasingColor(Color.rgb(122, 242, 84));
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        //set1.setHighlightLineWidth(1f);

        CandleData data = new CandleData(xVals, set1);

        mChart.setData(data);
        mChart.invalidate();
    }

}
