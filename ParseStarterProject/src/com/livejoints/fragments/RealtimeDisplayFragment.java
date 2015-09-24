package com.livejoints.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.livejoints.R;
import com.livejoints.analytics.ParseDataCollector;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class RealtimeDisplayFragment extends Fragment {
    private ParseDataCollector pdc = new ParseDataCollector();


    private PieChart mChart;

    private String[] mParties = new String[] {
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I"
    };

    public RealtimeDisplayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_realtime_display, container, false);

        mChart = (PieChart) v.findViewById(R.id.chart);
        setData2();

        return v;
    }


    private void setData2() {
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("On");
        xVals.add("Off");

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        Entry e1 = new Entry(90.0f,0);
        Entry e2 = new Entry(10.0f,1);
        yVals1.add(e1);
        yVals1.add(e2);

        PieDataSet dataSet = new PieDataSet(yVals1, "Election Results");

        ArrayList<Integer> colors = new ArrayList<Integer>();

        colors.add(Color.BLUE);
        colors.add(Color.RED);

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }
}
