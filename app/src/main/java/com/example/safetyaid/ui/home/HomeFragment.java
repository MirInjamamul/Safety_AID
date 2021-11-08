package com.example.safetyaid.ui.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.safetyaid.ContactPickerActivity;
import com.example.safetyaid.R;
import com.example.safetyaid.service.PanicService;

public class HomeFragment extends PreferenceFragmentCompat {

    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        SwitchPreferenceCompat panicSwitch = findPreference("panic");
        Preference contact_perference = findPreference("contact_preference");

        if (panicSwitch != null) {
            panicSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference arg0, Object isVibrateOnObject) {
                    boolean panicSwitchActivation = (Boolean) isVibrateOnObject;
                    if(panicSwitchActivation){
                        Toast.makeText(getActivity(), "Panic Service is been Activated", Toast.LENGTH_LONG).show();
                        startService();
                    }else{
                        Toast.makeText(getActivity(), "Panic Service is been DeActivated", Toast.LENGTH_LONG).show();
                        stopService();
                    }
                    return true;
                }
            });
        }

        contact_perference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), ContactPickerActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }

    private void startService() {
        String input = "Women Safety AID";
        Intent serviceIntent = new Intent(this.requireActivity(), PanicService.class);
        serviceIntent.putExtra("inputExtra", input);

        if(Build.VERSION.SDK_INT >= 26)
            ContextCompat.startForegroundService(this.requireActivity(), serviceIntent);
        else
            getContext().startService(serviceIntent);
    }

    private void stopService() {
        Intent serviceIntent = new Intent(getActivity(), PanicService.class);
        getActivity().stopService(serviceIntent);
    }

}