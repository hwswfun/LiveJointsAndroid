package com.livejoints.data;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by nathantofte on 9/9/15.
 */

@ParseClassName("ParseSensorSummary")
public class ParseSensorSummary extends ParseObject {

    public static final String TAG = ParseSensorSummary.class.getSimpleName().toString();

    JSONArray detailSensorArray;


    private static final String SENSORSUMMARY_AVERAGE = "Average";
    private static final String SENSORSUMMARY_LOW = "Low";
    private static final String SENSORSUMMARY_HIGH = "High";
    private static final String SENSORSUMMARY_STDDEV = "Stddev";
    private static final String SENSORSUMMARY_DATE = "Date";
    private static final String SENSORSUMMARY_READINGS = "Readings";


    public ParseSensorSummary() {
        if (detailSensorArray == null) {
            detailSensorArray = new JSONArray();
        }
        setReadingDate(Calendar.getInstance().getTime());
    }


    public Date getReadingDate() {
        return this.getDate(SENSORSUMMARY_DATE);
    }

    public Calendar getCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.getDate(SENSORSUMMARY_DATE));
        return cal;
    }

    public void setReadingDate(Date date) {
        this.put(SENSORSUMMARY_DATE, date);
    }

    public boolean analyze() {
        int low = 9999999;
        int high = 0;

        int raw = 0;

        long sum = 0;
        int average = 0;

        try {

            int count = detailSensorArray.length();
            if (count > 0) {

                for (int i = 0; i < count; i++) {
                    int pos = detailSensorArray.getInt(i);

                    sum += pos;
                    if (pos < low) low = pos;
                    if (pos > high) high = pos;
                }
                this.setLow(low);
                this.setHigh(high);
                average = (int) (sum / count);
                this.setAverage(average);
            }

            if (count > 1) {
                long sqrd = 0;
                int diff = 0;
                for (int i = 0; i < count; i++) {
                    int pos = detailSensorArray.getInt(i);
                    diff = average - raw;
                    sqrd += diff * diff;
                }

                long variance = 0;
                variance = sqrd / (count - 1);

                double stddev = Math.sqrt(variance);

                this.setStddev((int) stddev);

            }

        } catch (JSONException e) {
            e.printStackTrace();
            this.setAverage(0);
            this.setHigh(0);
            this.setLow(0);
            this.setStddev(0);
        }

        this.put(SENSORSUMMARY_READINGS, detailSensorArray);
        //JSONArray detailSensorArray;

        return true;
    }

    public JSONArray getReadings() {
        return this.getJSONArray(SENSORSUMMARY_READINGS);
    }


    public void addReading(int rawPosition) {
        detailSensorArray.put(rawPosition);
    }


    public int getLow() {
        return getInt(SENSORSUMMARY_LOW);
    }

    public void setLow(int low) {
        put(SENSORSUMMARY_LOW, low);
    }


    public int getHigh() {
        return getInt(SENSORSUMMARY_HIGH);
    }

    public void setHigh(int high) {
        put(SENSORSUMMARY_HIGH, high);
    }


    public int getStddev() {
        return getInt(SENSORSUMMARY_STDDEV);
    }

    public void setStddev(int stddev) {
        put(SENSORSUMMARY_STDDEV, stddev);
    }


    public int getAverage() {
        return getInt(SENSORSUMMARY_AVERAGE);
    }

    public void setAverage(int average) {
        put(SENSORSUMMARY_AVERAGE, average);
    }


}
