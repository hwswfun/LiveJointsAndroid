package com.livejoints.analytics;

import com.livejoints.data.ParseSensorSummary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nathantofte on 9/17/15.
 */
public class DaySummary {


    private static final String TAG = DaySummary.class.getSimpleName().toString();
    private int currentSummaryIndex = 0;
    private static ArrayList<ParseSensorSummary> sensorSummaryList = null;

    public DaySummary() {
        if (sensorSummaryList == null) {
            sensorSummaryList = new ArrayList<ParseSensorSummary>();
        } else {
            sensorSummaryList.clear();
        }
    }



    public void add(ParseSensorSummary currSS) {
        boolean slotFound = false;

        int hour = currSS.getReadingDate().getHours();


        for (ParseSensorSummary pss: sensorSummaryList) {
            int curHour = pss.getReadingDate().getHours();
            if (hour == curHour) {
                slotFound=true;

                pss.addReadings(currSS.getReadings());
            }
        }

        if (slotFound == false) {
            // no ss for this hour.
            sensorSummaryList.add(currSS);
        }


    }

    public void add(List<ParseSensorSummary> ssList) {
        for (ParseSensorSummary ss:ssList) {
            this.add(ss);
        }
    }

    public static List<ParseSensorSummary> getReadings() {
        return sensorSummaryList;
    }



}
