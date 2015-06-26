package com.example.android.stormy.weather;

/**
 * Created by adhyan on 26/6/15.
 */
public class Hour {
    private long mTime;
    private String mSummary;
    private double mTemperature;
    private String mICon;
    private String mTimeZone;

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public double getTemperature() {
        return mTemperature;
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public String getICon() {
        return mICon;
    }

    public void setICon(String ICon) {
        mICon = ICon;
    }
}
