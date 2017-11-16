package com.example.adityashekhar.onboardbeacon;

/**
 * Created by ADITYA SHEKHAR on 7/4/2017.
 */

import java.util.ArrayList;

/**
 * Created by Anshul on 12-06-2017.
 */
public class BusList {
    private int Number_of_Buses ;
    ArrayList<Bus> Buses = new ArrayList<Bus>();
    int getBusesNmber(){
        Number_of_Buses = Buses.size();
        return Number_of_Buses;
    }

    void addBus(String BusName){
        Bus a = new Bus(BusName);
        Buses.add(a);
    }

    public ArrayList<Bus> getList(){
        return Buses;
    }
}
