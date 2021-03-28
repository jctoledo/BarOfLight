package com.bertalabs.baroflight.lib;

import com.bertalabs.baroflight.ext.LightIntensity;

import java.time.LocalDateTime;
import java.util.Objects;

public class Light extends Device {
    public int request_power;
    public int actual_power;
    public double temperature;
    public boolean health;
    public boolean isConnected;
    public LightIntensity intensity;
    public DeviceGroup group;
    public LocalDateTime lastSeen;

    public Light(String ip_address, String mac_address, double temperature, boolean health, int request_power, int actual_power,
                 int rssi, LocalDateTime lastSeen, DeviceGroup group) {
        super(ip_address, mac_address, rssi);
        this.request_power = request_power;
        this.actual_power = actual_power;
        this.health = health;
        this.temperature = temperature;
        this.group = group;
        this.lastSeen = lastSeen;
        this.isConnected = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Light light = (Light) o;
        return request_power == light.request_power &&
                actual_power == light.actual_power &&
                Double.compare(light.temperature, temperature) == 0 &&
                health == light.health &&
                isConnected == light.isConnected &&
                intensity == light.intensity &&
                group == light.group &&
                lastSeen.equals(light.lastSeen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), request_power, actual_power, temperature, health, isConnected, intensity, group, lastSeen);
    }

    public enum DeviceGroup {
        FRONT,
        DITCH,
        FILL,
        TELEMATICS
    }

}
