package com.bertalabs.baroflight;

import android.os.Bundle;
import android.os.Handler;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bertalabs.baroflight.ext.LightLocationCache;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    ToggleButton powerToggle, linkToggle;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5000;
    private LightLocationCache cache = LightLocationCache.getInstance();

    @Override
    protected void onResume() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                cache.update();
            }
        }, delay);
        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cache = LightLocationCache.getInstance();
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        powerToggle = (ToggleButton) findViewById(R.id.powerTgl);
        powerToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    powerToggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_power_button_on, getApplicationContext().getTheme()));
                } else {
                    powerToggle.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_power_button, getApplicationContext().getTheme()));
                }
            }
        });
        powerToggle.isChecked();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_front, R.id.navigation_beam, R.id.navigation_fog)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public LightLocationCache getCache() {
        return this.cache;
    }

}