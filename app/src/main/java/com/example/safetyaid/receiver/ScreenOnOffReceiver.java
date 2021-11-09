package com.example.safetyaid.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


public class ScreenOnOffReceiver extends BroadcastReceiver {
    static int countPowerOff = 0;
    private Vibrator vibrator ;
    private static final String TAG = "ScreenOnOffReceiver";

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
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

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

            handler.removeCallbacks(runnable);
        }
        handler.postDelayed(runnable, (long) (2000*1.5));

    }
}
