package com.example.safetyaid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.example.safetyaid.Utils.Utils;
import com.example.safetyaid.auth.LoginActivity;
import com.example.safetyaid.data.DBHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.safetyaid.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DBHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieving the value using its keys the file name
        // must be same in both saving and retrieving the data
//        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_APPEND);

        // The value will be default as empty string because for
        // the very first time when the app is opened, there is nothing to show
//        boolean s1 = sh.getBoolean("logged", false);
        myDb = new DBHelper(this);
        boolean s1 = myDb.getStatus();;

        if (s1 == false) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            return;
        }

        String[] perms;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if((perms = Utils.checkPermissions(this)).length > 0)
                requestPermissions(perms, 255);
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


    }

}