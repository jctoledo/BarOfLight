package com.bertalabs.baroflight.ui.front;

import androidx.lifecycle.ViewModel;

import com.bertalabs.baroflight.lib.Light;
import com.bertalabs.baroflight.bin.LightDeviceCache;

import java.util.ArrayList;
import java.util.List;

public class FrontViewModel extends ViewModel {
    private LightDeviceCache lightCache;

    public FrontViewModel() {
        lightCache = LightDeviceCache.getInstance();
    }

    public LightDeviceCache getLightCache() {
        if (lightCache == null) {
            lightCache = LightDeviceCache.getInstance();
        }
        return lightCache;
    }

    public List<Light> getLights() {

        if (lightCache.getLights() != null) {
            return lightCache.getLights();
        }
        return new ArrayList<>();
    }

    public void update() {
        lightCache.update();
    }
}