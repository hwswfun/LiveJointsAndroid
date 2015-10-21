package com.livejoints.fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.livejoints.R;
import com.livejoints.bluetooth.BluetoothLeService;

/**
 * A placeholder fragment containing a simple view.
 */
public class LimbDisplayFragment extends Fragment {
    private final static String TAG = LimbDisplayFragment.class.getSimpleName();

    int valuesByTens[] = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    int adjustedValuesByTens[] = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    long valuesTotal = 0;
    private PieChart mChart;

    private final int NUMBER_OF_CATEGORIES = 35;

    public LimbDisplayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_limb_display, container, false);

        mChart = (PieChart) v.findViewById(R.id.chart);

        printValues();

        //noDataChart();
        return v;
    }

    private void printValues() {
        for (int i = 0; i < NUMBER_OF_CATEGORIES; i++) {
            Log.d(TAG, "" + i + ": " + valuesByTens[i]);
        }
    }

    final long MAX_VALUES_TOTAL = 50000;

    private void alignValues() {
        if (valuesTotal > MAX_VALUES_TOTAL) {
            valuesTotal = valuesTotal / 100;

            for (int i = 0; i < NUMBER_OF_CATEGORIES; i++) {
                valuesByTens[i] = valuesByTens[i] / 100;
            }
        }
    }


    final static int CLIP_VALUE = 100;
    // ok with 100% in each of the 36 categories.  So lets set bar at valuesTotal / 36.  Then get percent form that

    private void adjustedValues() {
        double saturationValue = (double)valuesTotal / (double)NUMBER_OF_CATEGORIES;
        if (saturationValue < 100) saturationValue = 100;

        for (int i = 0; i < NUMBER_OF_CATEGORIES; i++) {
            double currentValue = (double) valuesByTens[i];


            double percent = (currentValue / saturationValue) * 100.0;
            if (percent > CLIP_VALUE) percent = CLIP_VALUE;

            adjustedValuesByTens[i] = (int) percent;
        }

    }

    private void printAdjustedValues() {
        for (int i = 0; i < NUMBER_OF_CATEGORIES; i++) {
            Log.d(TAG, "adjusted " + i + ": " + adjustedValuesByTens[i]);
        }
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

                        int index = angle / 10;
                        if (index > NUMBER_OF_CATEGORIES) index = NUMBER_OF_CATEGORIES;

                        valuesByTens[index]++;
                        valuesTotal++;
                        alignValues();
                        adjustedValues();
                        printAdjustedValues();
                        //printValues();
                        //Log.d(TAG, "data received int =========>" + angle);
                        //armAngle(angle);

                    } catch (NumberFormatException nfe) {
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




}
