package com.example.adityashekhar.onboardbeacon;

/**
 * Created by ADITYA SHEKHAR on 7/4/2017.
 */

public class stop {
    private String name;
    private double latitude;
    private double longitude;
    public stop(String s, double la, double lon) {
        name = s;
        latitude = la;
        longitude=lon;
    }
    public String getName() {
        return name;
    }

    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
}
