package com.example.adityashekhar.onboardbeacon;

/**
 * Created by ADITYA SHEKHAR on 7/4/2017.
 */

import java.util.ArrayList;

/**
 * Created by Anshul on 12-06-2017.
 */
public class stoplist {
    ArrayList<stop> Stops = new ArrayList<stop>();
    void addStop(String s, double la, double lo){
        stop a = new stop(s, la, lo);
        Stops.add(a);
    }
    public ArrayList<stop> getStopsList(){
        return Stops;
    }
}
