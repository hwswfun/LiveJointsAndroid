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
import android.widget.ImageView;

import com.livejoints.CircleChart;
import com.livejoints.R;
import com.livejoints.bluetooth.BluetoothLeService;
import com.livejoints.data.ParseSensorSummary;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class LimbDisplayFragment extends Fragment {
    private final static String TAG = LimbDisplayFragment.class.getSimpleName();



    ImageView upperArm;
    ImageView lowerArm;
    CircleChart circleChart;

    private boolean showChart = true;

    public LimbDisplayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_limb_display, container, false);

        upperArm = (ImageView)v.findViewById(R.id.upperArmImageView);
        lowerArm = (ImageView)v.findViewById(R.id.lowerArmImageView);
        circleChart = (CircleChart)v.findViewById(R.id.circleChart);

        upperArm.setRotation(0);
        upperArm.setScaleX(0.5f);
        upperArm.setScaleY(0.5f);
        upperArm.setBackgroundColor(Color.TRANSPARENT);

        lowerArm.setRotation(45);
        lowerArm.setScaleX(0.5f);
        lowerArm.setScaleY(0.5f);
        lowerArm.setBackgroundColor(Color.TRANSPARENT);

        if (showChart == false) {
            circleChart.setVisibility(View.GONE);
        }

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();

        // do not show chart
        // request update to chart from parse
        circleChart.setVisibility(View.GONE);
        resetData();


        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);


    }

    private void dataAvailable(Context context, Intent intent) {
        String angleStr = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
        if (angleStr != null) {
            try {
                //Log.d(TAG, "data received =========>" + angleStr);
                int angle = Integer.parseInt(angleStr);

                // set angle of forearm image in chart
                setAngle(angle);
                if (showChart == true) {
                    // add value to chart
                    circleChart.addValue(angle);
                }

            } catch (NumberFormatException nfe) {
                Log.d(TAG, "bummer.  value for angle was wierd: " + angleStr);
            }
        } else {
            Log.d(TAG, "null ptr");
        }
    }


    boolean beingReset = false;

    public void resetData() {
        beingReset = true;
        getResetData();

        // beingReset gets set to false once data returned and processed
    }
    private void getResetData() {
        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);
        Log.d(TAG, "query for:" + ParseSensorSummary.class.getSimpleName());

        ParseQuery<ParseSensorSummary> query = ParseQuery.getQuery(ParseSensorSummary.class);
        //query.whereEqualTo("playerName", "Dan Stemkoski");

        query.orderByDescending("createdAt");
        query.setLimit(10);


        query.findInBackground(new FindCallback<ParseSensorSummary>() {
            public void done(List<ParseSensorSummary> sensorSummaryList, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + sensorSummaryList.size() + " scores");
                    resetDataReceived(sensorSummaryList);
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }


    private void resetDataReceived(List<ParseSensorSummary> readings) {

        circleChart.resetData();

        ParseSensorSummary pss=null;
        for (int i = 0; i < readings.size(); i++) {
            pss = readings.get(i);

            JSONArray detailSensorArray = pss.getReadings();

            int numReadings = detailSensorArray.length();

            try {
                for (int j = 0; j < numReadings; j++) {
                    int val = detailSensorArray.getInt(j);
                    circleChart.addValue(val);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // allow individual readings again
        beingReset = false;

        // show chart again now that repopulated
        if (showChart == true) {
            circleChart.setVisibility(View.VISIBLE);
        }
    }




    private void setAngle(int angle) {
        lowerArm.setRotation(angle);
    }

    public void setShowChart(boolean showChart) {
        this.showChart = showChart;
        if (showChart == false) {
            circleChart.setVisibility(View.GONE);
        }
    }








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
                if (beingReset == false) {
                    // only update real time if not in the middle of a chart reset (pulling from server)
                    dataAvailable(context,intent);
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
