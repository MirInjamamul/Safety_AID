package com.example.safetyaid.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.safetyaid.BeaconTransmitterActivity;
import com.example.safetyaid.R;
import com.example.safetyaid.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "Dashboard Fragment";
    private View dashboard_view;
    private CardView panic_trigger_card_view, ble_transmitter_card_view, cardView3, cardView4, cardView5, cardView6;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dashboard_view = inflater.inflate(R.layout.fragment_dashboard, null);

        panic_trigger_card_view = (CardView) dashboard_view.findViewById(R.id.panic_trigger_card_view);
        ble_transmitter_card_view = (CardView) dashboard_view.findViewById(R.id.ble_transmitter_card_view);

        panic_trigger_card_view.setOnClickListener(this);
        ble_transmitter_card_view.setOnClickListener(this);

        return dashboard_view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(final View v) { //check for what button is pressed
        switch (v.getId()) {
            case R.id.panic_trigger_card_view:
                Toast.makeText(getActivity(),"Coming Soon !!!",Toast.LENGTH_LONG).show();
                break;
            case R.id.ble_transmitter_card_view:
                Intent intent = new Intent(this.getActivity(), BeaconTransmitterActivity.class);
                startActivity(intent);
                break;
            default:

                break;
        }
    }
}