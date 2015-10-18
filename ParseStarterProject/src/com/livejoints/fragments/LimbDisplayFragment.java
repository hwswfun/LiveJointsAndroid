package com.livejoints.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.livejoints.R;
import com.livejoints.bluetooth.BluetoothLeService;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class LimbDisplayFragment extends Fragment {
    private final static String TAG = LimbDisplayFragment.class.getSimpleName();

    private PieChart mChart;

    public LimbDisplayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_realtime_display, container, false);

        mChart = (PieChart) v.findViewById(R.id.chart);
        noDataChart();

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }



    private void noDataChart() {
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
        //data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }


    static long counter = 0;

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {



        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
    /*        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else
            */

            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String angleStr = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                if (angleStr != null) {
                    try {
                        //Log.d(TAG, "data received =========>" + angleStr);
                        int angle = Integer.parseInt(angleStr);
                        //Log.d(TAG, "data received int =========>" + angle);
                        armAngle(angle);

                    } catch(NumberFormatException nfe) {
                        Log.d(TAG, "bummer.  value for angle was wierd: " + angleStr);
                    }
                } else {
                    Log.d(TAG, "null ptr");
                }


            }
        }
    };


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        //intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        //intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    private void armAngle(int angle) {
        //Log.d(TAG, "create pie chart for angle: " + angle);
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("fOn");
        xVals.add("fOff");

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        Entry e1 = new Entry(angle,0);
        Entry e2 = new Entry(100-angle,1);
        yVals1.add(e1);
        yVals1.add(e2);

        PieDataSet dataSet = new PieDataSet(yVals1, "Election Results");

        ArrayList<Integer> colors = new ArrayList<Integer>();

        colors.add(Color.BLACK);
        colors.add(Color.WHITE);

        dataSet.setColors(colors);



        PieData data = new PieData(xVals, dataSet);
        //data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);



        mChart.setData(data);


        mChart.setUsePercentValues(false);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

}
