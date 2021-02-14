package com.bertalabs.baroflight.ext;

import android.util.Log;

import com.bertalabs.baroflight.R;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class LightLocationCache {
    private static final String BASE_HTTP_ADDR = "http://192.168.1.";
    private static LightLocationCache instance;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();
    /**
     * How often to perform hard refresh
     */
    public int hardRefreshTime = 60;
    public LocalDateTime startTime;
    private String TAG = "LIGHTCACHE";
    private HashMap<String, Light> ipToLight = new HashMap<>();
    private Set<Light> disconnectedLights = new HashSet<>();

    private LightLocationCache() {
        if (startTime == null ||
                Duration.between(startTime, LocalDateTime.now()).getSeconds() > hardRefreshTime) {
            try {
                ipToLight = NetworkUtils.findLights(BASE_HTTP_ADDR);
            } catch (UnhealthyLightException e){
                disconnectedLights.add(e.light);
                Log.w(TAG, "found an unhealthy light at cache creation! - "+e.light.ip_address);
            }
            startTime = LocalDateTime.now();
        }
    }

    synchronized public static LightLocationCache getInstance() {
        if (instance == null) {
            instance = new LightLocationCache();
        }
        if (Duration.between(instance.startTime, LocalDateTime.now()).getSeconds() > 5)
            instance.update();
        return instance;
    }

    public List<Light> getLights() {
        return new ArrayList<Light>(this.ipToLight.values());
    }

    //TODO: detect a lost light
    //TODO: detect a new light
    public void update() {
        if (Duration.between(this.startTime,
                LocalDateTime.now()).getSeconds() > hardRefreshTime) {
            Log.d(TAG, "regular update - after refresh seconds ");
            try {
                ipToLight = NetworkUtils.findLights(BASE_HTTP_ADDR);
            } catch (UnhealthyLightException e) {
                disconnectedLights.add(e.light);
                Log.d(TAG , "Added an unhealthy light - updating! ");
            }
        } else {
            fastUpdate();
        }
    }

    // go through lights in the cache and verify that they are still there
    private void fastUpdate() {
        // first we need to check if there are already some disconnected lights
        // iterate over disconnected lights and make sure they are still disconnected
        Set<Light> disconnected = new HashSet<>();
        for (Light l : disconnectedLights) {
            Log.d(TAG, "fast update - found an already disconnected light");
            if (NetworkUtils.checkLightStatus(l.ip_address)) {
                disconnected.add(l);
            }
        }
        for (Map.Entry<String, Light> entry : ipToLight.entrySet()) {
            if (!NetworkUtils.checkLightStatus(entry.getKey())) {
                disconnected.add(entry.getValue());
                Log.d(TAG, "fast update - adding a disconnected light");
            }
        }
        disconnectedLights = disconnected;
    }
}
