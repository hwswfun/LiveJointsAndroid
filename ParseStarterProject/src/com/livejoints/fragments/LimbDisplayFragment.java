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

import com.livejoints.R;
import com.livejoints.bluetooth.BluetoothLeService;

/**
 * A placeholder fragment containing a simple view.
 */
public class LimbDisplayFragment extends Fragment {
    private final static String TAG = LimbDisplayFragment.class.getSimpleName();



    ImageView upperArm;
    ImageView lowerArm;

    public LimbDisplayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_limb_display, container, false);

        upperArm = (ImageView)v.findViewById(R.id.upperArmImageView);
        lowerArm = (ImageView)v.findViewById(R.id.lowerArmImageView);

        upperArm.setRotation(0);
        upperArm.setScaleX(0.5f);
        upperArm.setScaleY(0.5f);
        upperArm.setBackgroundColor(Color.TRANSPARENT);

        lowerArm.setRotation(45);
        lowerArm.setScaleX(0.5f);
        lowerArm.setScaleY(0.5f);
        lowerArm.setBackgroundColor(Color.TRANSPARENT);

        return v;
    }


    private void setAngle(int angle) {
        lowerArm.setRotation(angle);
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

                        setAngle(angle);
//                        int index = angle / 10;
//                        if (index > NUMBER_OF_CATEGORIES) index = NUMBER_OF_CATEGORIES;
//
//                        valuesByTens[index]++;
//                        valuesTotal++;
//                        alignValues();
//                        adjustedValues();
                        //printAdjustedValues();

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
