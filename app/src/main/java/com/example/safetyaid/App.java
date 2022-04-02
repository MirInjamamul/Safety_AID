package com.example.safetyaid;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.RegionBootstrap;


public class App extends Application {
    public static final String CHANNEL_ID = "exampleServiceChannel";

    RegionBootstrap regionBootstrap;
    BackgroundPowerSaver backgroundPowerSaver;
    BeaconManager beaconManager;
    Region region;
    private static Context context;
    public static boolean isActive;


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }

        context = getApplicationContext();

        beaconManager = BeaconManager.getInstanceForApplication(this);

        // enables auto battery saving of about 60%
        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }

    public static Context getAppContext() {
        return context;
    }

    public BeaconManager getBeaconManager() {
        return beaconManager;
    }
    public Region getRegion() {return region; }

}
