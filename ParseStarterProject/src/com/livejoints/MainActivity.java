package com.livejoints;

/*

Data frequency
* 1/2s for real time arm position or game
* 1 minute
*       Log to parse.
*       average, min, max,
*       individual values stored in array in the row.  120 array items per minute
*           array in parse stored as json array: https://www.parse.com/questions/get-an-array-from-parse-with-android
*
*
*           Make pie chart look like elbow position
*           bar chart for last 5 minutes decay to encourage the different positions  (8 slots?)
*           bar chart with line = avg, lower of bar = 3std dev of extension, upper of bar = 3std dev of flexion
*
*




 */



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.livejoints.analytics.ParseDataCollector;
import com.livejoints.bluetooth.BluetoothLeService;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.starter.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends Activity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private ParseDataCollector pdc = new ParseDataCollector();


    private PieChart mChart;

    private String[] mParties = new String[] {
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I"
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mChart = (PieChart) findViewById(R.id.chart);

        setData2();
        //setData(5,100.0f);



    }


    private void setupParse() {

        Log.d("APP", "MainActivity onCreated start");

        ParseAnalytics.trackAppOpenedInBackground(getIntent());


        Calendar c = Calendar.getInstance();
        Date d = c.getTime();

        ParseObject testObject = new ParseObject("AppStat");


        testObject.put("started", d.toString());
        testObject.saveInBackground();

        Log.d("APP", "MainActivity onCreated end");


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
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
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);



        mChart.setData(data);


        mChart.setUsePercentValues(false);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

                        counter++;
                        if ((counter % 30) == 0) {
                            //dc.add(angle);
                            pdc.add(angle);
                        }


                        //ParseObject testObject = new ParseObject("ArmStats");
                        //testObject.put("pot", angle);
                        //testObject.saveInBackground();

                        //Log.d("APP", "Arm data received");

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




    private void setData(int count, float range) {

        float mult = range;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        for (int i = 0; i < count + 1; i++) {
            yVals1.add(new Entry((float) (Math.random() * mult) + mult / 5, i));
        }

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < count + 1; i++)
            xVals.add(mParties[i % mParties.length]);

        PieDataSet dataSet = new PieDataSet(yVals1, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

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
