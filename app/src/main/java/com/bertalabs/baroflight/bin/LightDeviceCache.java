package com.bertalabs.baroflight.bin;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.bertalabs.baroflight.lib.Telematics;
import com.bertalabs.baroflight.lib.Device;
import com.bertalabs.baroflight.lib.Light;
import com.bertalabs.baroflight.ext.LightIntensity;
import com.bertalabs.baroflight.utils.NetworkUtils;
import com.bertalabs.baroflight.utils.UnhealthyLightException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LightDeviceCache extends MutableLiveData<LightDeviceCache> {
    private static final String BASE_HTTP_ADDR = "http://192.168.1.";
    private static LightDeviceCache instance;

    /**
     * How often to perform hard refresh
     */
    public int hardRefreshTime = 60;
    public LocalDateTime startTime;
    private String TAG = "LIGHTCACHE";
    private HashMap<String, Device> ipToDevice = new HashMap<>();
    private Set<Light> disconnectedLights = new HashSet<>();
    private Telematics beamLink;

    private LightDeviceCache() {
        if (startTime == null ||
                Duration.between(startTime, LocalDateTime.now()).getSeconds() > hardRefreshTime) {
            try {
                ipToDevice = NetworkUtils.findDevices(BASE_HTTP_ADDR);
            } catch (UnhealthyLightException e) {
                disconnectedLights.add(e.light);
                Log.w(TAG, "found an unhealthy light at cache creation! - " + e.light.ipAddress);
            }
            startTime = LocalDateTime.now();
        }
    }

    synchronized public static LightDeviceCache getInstance() {
        if (instance == null) {
            instance = new LightDeviceCache();
        }
        if (Duration.between(instance.startTime, LocalDateTime.now()).getSeconds() > 5)
            instance.update();
        return instance;
    }

    public List<Light> getLights() {
        List<Light> rm = new ArrayList<>();
        for (Device d: this.ipToDevice.values()){
            if (d instanceof Light){
                rm.add((Light) d);
            }
        }
        return rm;
    }

    public void update() {
        if (Duration.between(this.startTime,
                LocalDateTime.now()).getSeconds() > hardRefreshTime) {
            Log.d(TAG, "regular update - after "+ hardRefreshTime +" refresh seconds ");
            try {
                ipToDevice = NetworkUtils.findDevices(BASE_HTTP_ADDR);
            } catch (UnhealthyLightException e) {
                disconnectedLights.add(e.light);
                Log.d(TAG, "Added an unhealthy light - updating! ");
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
            if (NetworkUtils.checkDeviceStatus(l.ipAddress)) {
                disconnected.add(l);
            }
        }
//        for (Map.Entry<String, Device> entry : ipToDevice.entrySet()) {
//            if (!NetworkUtils.checkLightStatus(entry.getKey())) {
//                disconnected.add(entry.getValue());
//                Log.d(TAG, "fast update - adding a disconnected light");
//            }
//        }
        disconnectedLights = disconnected;
    }

    public void destroy() {
        instance = null;
        ipToDevice = new HashMap<>();
        disconnectedLights = new HashSet<>();
    }

    public void modifyIntensity(LightIntensity anIntensity){
        for (Map.Entry<String, Device> entry : ipToDevice.entrySet()) {
            if (entry.getValue() instanceof Light){
                NetworkUtils.setLightIntensity(entry.getKey(), anIntensity);
                ((Light) entry.getValue()).intensity = anIntensity;
            }
        }
        Log.d(TAG, "turned everything - "+ anIntensity.toString());
    }
}
