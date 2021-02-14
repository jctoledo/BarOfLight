package com.bertalabs.baroflight.ext;

import java.time.LocalDateTime;
import java.util.Objects;

public class Light {
    public String ip_address;
    public String mac_address;
    public int request_power;
    public int actual_power;
    public int rssi;
    public double temperature;
    public boolean health;
    public boolean isConnected;
    public LightGroup group;
    public LocalDateTime lastSeen;

    public enum LightGroup {
        FRONT,
        DITCH,
        FILL;
    }

    public Light(String ip_address, String mac_address, double temperature, boolean health, int request_power, int actual_power,
                 int rssi, LocalDateTime lastSeen, LightGroup group) {
        this.ip_address = ip_address;
        this.mac_address = mac_address;
        this.request_power = request_power;
        this.actual_power = actual_power;
        this.rssi = rssi;
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
        Light light = (Light) o;
        return request_power == light.request_power &&
                actual_power == light.actual_power &&
                rssi == light.rssi &&
                temperature == light.temperature &&
                group == light.group &&
                health == light.health &&
                lastSeen.equals(light.lastSeen) &&
                ip_address.equals(light.ip_address) &&
                mac_address.equals(light.mac_address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip_address, health, mac_address, request_power, actual_power, temperature,
                rssi, lastSeen, group);
    }

}
