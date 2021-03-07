package com.bertalabs.baroflight.ui.front;

import androidx.lifecycle.ViewModel;

import com.bertalabs.baroflight.ext.Light;
import com.bertalabs.baroflight.ext.LightLocationCache;

import java.util.ArrayList;
import java.util.List;

public class FrontViewModel extends ViewModel {
    private LightLocationCache lightCache;

    public FrontViewModel() {
        lightCache = LightLocationCache.getInstance();
    }

    public LightLocationCache getLightCache() {
        if (lightCache == null) {
            lightCache = LightLocationCache.getInstance();
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