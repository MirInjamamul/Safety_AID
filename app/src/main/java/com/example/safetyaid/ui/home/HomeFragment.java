package com.example.safetyaid.ui.home;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.safetyaid.R;
import com.example.safetyaid.databinding.FragmentHomeBinding;

public class HomeFragment extends PreferenceFragmentCompat {

    private static final String TAG = "SettingsFragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        SwitchPreferenceCompat alarmSwitch = findPreference("sync");

        if (alarmSwitch != null) {
            alarmSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference arg0, Object isVibrateOnObject) {
                    boolean isVibrateOn = (Boolean) isVibrateOnObject;
                    Log.d(TAG, "onPreferenceChange: "+isVibrateOn);
                    return true;
                }
            });
        }
    }
}