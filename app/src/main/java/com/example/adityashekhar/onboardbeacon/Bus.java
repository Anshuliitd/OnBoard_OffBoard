package com.example.adityashekhar.onboardbeacon;

/**
 * Created by ADITYA SHEKHAR on 7/4/2017.
 */

import java.util.ArrayList;
import java.io.Serializable;

/**
 * Created by Anshul on 12-06-2017.
 */
public class Bus implements Serializable {
    private double latitude;
    private double longtitude;
    private String name;

    private stoplist S_list;

    public Bus(String busName) {
        name = busName;
        latitude = (long) 0.0;
        longtitude = (long) 0.0;
        S_list = new stoplist();
        S_list.addStop("Main gate", 28.545766, 77.196457);
        S_list.addStop("LHC", 28.544623,77.192348);
        S_list.addStop("Bharti",28.544623,77.190348);
        S_list.addStop("Hospital", 28.545499,77.188210);
    }
    public String getName(){
        return name;
    }

    public ArrayList<stop> getStopList(){
        ArrayList<stop> r = S_list.getStopsList();
        return r;
    }
}
