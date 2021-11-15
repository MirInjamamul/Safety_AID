package com.example.safetyaid.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.safetyaid.Model.Contact;
import com.example.safetyaid.Utils.Utils;
import com.example.safetyaid.data.DBHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class ScreenOnOffReceiver extends BroadcastReceiver {
    static int countPowerOff = 0;
    private Vibrator vibrator;
    TelephonyManager telephonyManager;
    private static final String TAG = "ScreenOnOffReceiver";
    Context context;
    private Contact[] notifyContacts;

    private ArrayList contact_list_number = new ArrayList<String>();
    private int battery_level;
    private String imei_number;
    private DBHelper myDB;
    FusedLocationProviderClient fusedLocationProviderClient;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        public void run() {
            countPowerOff = 0;
            System.out.println("RESETTING");
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        this.context = context;
        myDB = new DBHelper(context);
        vibrator = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        telephonyManager = (TelephonyManager)this.context.getSystemService(Context.TELEPHONY_SERVICE);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        try {
            System.out.println("Receiver start");
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            Log.d(TAG, "onReceive: " + incomingNumber);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                Toast.makeText(context, "Incoming Call State", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "Ringing State Number is -" + incomingNumber, Toast.LENGTH_SHORT).show();


            }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))) {
                Toast.makeText(context, "Call Received State", Toast.LENGTH_SHORT).show();
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                Toast.makeText(context, "Call Idle State", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            countPowerOff++;
            Log.d("Current clicks are ", String.valueOf(countPowerOff));
            Log.d(TAG, "onReceive: Screen is turning off");
        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
            countPowerOff++;
            Log.d("Current clicks are ", String.valueOf(countPowerOff));
            Log.d(TAG, "onReceive: Screen is turning on");
        }

        if (countPowerOff == 3) {
            vibrator.vibrate(1000);
            Log.d(TAG, "onReceive: USER is in Danger");
            countPowerOff = 0;

            get_emergency_number();
            notifyContacts = Utils.getContactsByGroup("General", context);
            getBatteryLevel();
            get_IMEI_number();
            getCurrentLocationAndPanic();


            handler.removeCallbacks(runnable);
        }
        handler.postDelayed(runnable, (long) (2000 * 1.5));

    }

    private void get_IMEI_number() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imei_number = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager.getDeviceId() != null) {
                imei_number = telephonyManager.getDeviceId();
            } else {
                imei_number = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }
    }

    private void getBatteryLevel() {
        BatteryManager bm = (BatteryManager)this.context.getSystemService(Context.BATTERY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            battery_level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        }else{
            battery_level = 0;
        }

        Log.d(TAG, "getBatteryLevel: "+battery_level);
    }

    private void get_emergency_number() {
        contact_list_number = myDB.getAllCotactsNumber();
    }

    private void sendOutPanic(Location loc) {
        String keyword = "Help";
        SmsManager manager = SmsManager.getDefault();

        for (int counter = 0; counter < contact_list_number.size(); counter++) {
            String phone_number = (String) contact_list_number.get(counter);

            StringBuilder sb = new StringBuilder(keyword);
            // Add Battery Level in SMS
            sb.append("\n" + "Battery Level : " +battery_level);
//                Add IEMI Number in SMS
            sb.append("\n" + "IMEI : " +imei_number);

            if (loc != null)
                sb.append("\n" + "Latitude"+ loc.getLatitude() + "\n" + "Longitude : "+ loc.getLongitude());

            manager.sendTextMessage(phone_number, null, sb.toString(), null, null);
            Log.d(TAG, "sendOutPanic: Message Sent Successfully");

        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            contact_list_number.forEach((number) -> {
//                StringBuilder sb = new StringBuilder(keyword);
//                // Add Battery Level in SMS
//                sb.append("\n" + "Battery Level : " +battery_level);
////                Add IEMI Number in SMS
//                sb.append("\n" + "IMEI : " +imei_number);
//
//                if (loc != null)
//                    sb.append("\n" + "Latitude"+ loc.getLatitude() + "\n" + "Longitude : "+ loc.getLongitude());
//
//                manager.sendTextMessage(number.toString(), null, sb.toString(), null, null);
//                Log.d(TAG, "sendOutPanic: Message Sent Successfully");
//
//                System.out.print(number + " ");
//            });
//        }
    }

    private void getCurrentLocationAndPanic() {
        Log.d(TAG, "getCurrentLocationAndPanic initiated");

        if (isLocationEnabled()) {

            // getting last location from FusedLocationClient object
            if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {

                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        sendOutPanic(location);

                    }
                }
            });
        } else {
            Toast.makeText(context, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startService(intent);
        }
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            sendOutPanic(mLastLocation);
        }
    };
}
