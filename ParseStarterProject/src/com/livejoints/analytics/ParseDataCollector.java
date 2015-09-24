package com.livejoints.analytics;

import android.util.Log;

import com.livejoints.data.ParseSensorSummary;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by nathantofte on 9/17/15.
 */
public class ParseDataCollector {


    private boolean retainOnlyPrevious = false;

    private static final String TAG = ParseDataCollector.class.getSimpleName().toString();
    private int currentSummaryIndex = 0;
    private static ArrayList<ParseSensorSummary> sensorSummary = null;

    public ParseDataCollector() {
    }


    private void rollover() {
        // perform analysis on current sensor summary before starting next one.
        if (sensorSummary.size() > 0) {
            ParseSensorSummary ss = sensorSummary.get(currentSummaryIndex);
            ss.analyze();
            Log.d(TAG, "analyzed the latest ParseSensorSummary> :" + ss.toString());


            ss.saveEventually();
            Log.d(TAG, "Saved object with average of " + ss.getAverage());
        }

        if (retainOnlyPrevious == true) {
            // clear out all previous items so only one left is the new one.
            sensorSummary.clear();
        }

        ParseSensorSummary newSS = new ParseSensorSummary();

        sensorSummary.add(newSS);
        currentSummaryIndex = sensorSummary.size() - 1;
    }

    public boolean add(int rawSensor) {
        boolean newSensorSummaryAvailable = false;

        if (sensorSummary == null) {
            sensorSummary = new ArrayList<ParseSensorSummary>();
            rollover();
        }

        ParseSensorSummary ss = sensorSummary.get(currentSummaryIndex);
        Calendar prevCalendar = ss.getCalendar();
        int ssMinute = prevCalendar.get(Calendar.MINUTE);

        Calendar currentDate = Calendar.getInstance();
        int currMinute = currentDate.get(Calendar.MINUTE);


        if (currMinute == ssMinute) {

        } else {
            Log.d(TAG, "========> Rollover minute: " + ssMinute + " now:" + currMinute);
            rollover();
            ss = sensorSummary.get(currentSummaryIndex);
            newSensorSummaryAvailable = true;
        }
        ss.addReading(rawSensor);

        return newSensorSummaryAvailable;
    }


    public boolean isRetainOnlyPrevious() {
        return retainOnlyPrevious;
    }

    public void setRetainOnlyPrevious(boolean retainOnlyPrevious) {
        this.retainOnlyPrevious = retainOnlyPrevious;
    }

    public ParseSensorSummary getLastSensorSummary() {
        return sensorSummary.get(sensorSummary.size() - 1);
    }


}
