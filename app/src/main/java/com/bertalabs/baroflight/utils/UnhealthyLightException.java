package com.bertalabs.baroflight.utils;

import com.bertalabs.baroflight.ext.Light;

public class UnhealthyLightException extends Exception {
    public Light light;

    public UnhealthyLightException(String message) {
        super(message);
    }

    public UnhealthyLightException(Light aLight, String message) {
        super(message);
        light = aLight;
    }

    public Light getLight() {
        return light;
    }
}
