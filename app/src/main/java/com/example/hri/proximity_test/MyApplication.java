package com.example.hri.proximity_test;

import android.app.Application;

import com.estimote.coresdk.service.BeaconManager;


public class MyApplication extends Application {

    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();

        beaconManager = new BeaconManager(getApplicationContext());
    }
}