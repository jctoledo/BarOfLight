package com.bertalabs.baroflight.ui.front;

import androidx.lifecycle.ViewModel;

import com.bertalabs.baroflight.ext.Light;
import com.bertalabs.baroflight.ext.LightLocationCache;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

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

        await().atMost(10, SECONDS).until(() -> getLightCache().exists());
        if (lightCache.getLights() != null) {
            return lightCache.getLights();
        }
        return new ArrayList<>();
    }

    public void update() {
        lightCache.update();
    }
}