package com.example.safetyaid.receiver;

import static android.content.Context.LOCATION_SERVICE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.safetyaid.Model.Contact;
import com.example.safetyaid.R;
import com.example.safetyaid.Utils.Utils;


public class ScreenOnOffReceiver extends BroadcastReceiver {
    static int countPowerOff = 0;
    private Vibrator vibrator ;
    private static final String TAG = "ScreenOnOffReceiver";
    Context context;
    private Contact[] notifyContacts;

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
        vibrator = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);

        try {
            System.out.println("Receiver start");
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            Log.d(TAG, "onReceive: "+incomingNumber);

            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                Toast.makeText(context,"Incoming Call State",Toast.LENGTH_SHORT).show();
                Toast.makeText(context,"Ringing State Number is -"+incomingNumber,Toast.LENGTH_SHORT).show();


            }
            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
                Toast.makeText(context,"Call Received State",Toast.LENGTH_SHORT).show();
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                Toast.makeText(context,"Call Idle State",Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if(Intent.ACTION_SCREEN_OFF.equals(action)){
            countPowerOff++;
            Log.d("Current clicks are ", String.valueOf(countPowerOff));
            Log.d(TAG, "onReceive: Screen is turning off");
        }else if(Intent.ACTION_SCREEN_ON.equals(action)){
            countPowerOff++;
            Log.d("Current clicks are ", String.valueOf(countPowerOff));
            Log.d(TAG, "onReceive: Screen is turning on");
        }

        if (countPowerOff == 3) {
            vibrator.vibrate(1000);
            Log.d(TAG, "onReceive: USER is in Danger");
            countPowerOff = 0;

            notifyContacts = Utils.getContactsByGroup("General", context);
            getCurrentLocationAndPanic();

            handler.removeCallbacks(runnable);
        }
        handler.postDelayed(runnable, (long) (2000*1.5));

    }

    private void sendOutPanic(Location loc)
    {
        String keyword = "Panic";
        SmsManager manager = SmsManager.getDefault();

        Toast.makeText(context.getApplicationContext(),"Number found : "+notifyContacts.length,Toast.LENGTH_SHORT).show();

        for (Contact c : notifyContacts)
        {
            StringBuilder sb = new StringBuilder(keyword);
            if(loc != null)
                sb.append("\n" + loc.getLatitude() + "\n" + loc.getLongitude());

            manager.sendTextMessage(c.number, null, sb.toString(), null, null);
            Log.d(TAG, "sendOutPanic: Message Sent Successfully");
        }
    }

    private void getCurrentLocationAndPanic()
    {

        Log.d(TAG, "getCurrentLocationAndPanic initiated");
        sendOutPanic(null);
//        LocationManager locManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
//        try
//        {
//            if(locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
//                locManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
//            else if(locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
//                locManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
//            else
//                sendOutPanic(locManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER));
//        }
//        catch (Exception e)
//        {
//            Toast.makeText(this, "GPS fix could not be acquired. Please check your settings!", Toast.LENGTH_LONG).show();
//            sendOutPanic(null);
//        }
    }
}
