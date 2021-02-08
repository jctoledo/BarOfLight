package com.bertalabs.baroflight.ext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Light {
    public String ip_address;
    public String mac_address;
    public int request_power;
    public int actual_power;
    public int rssi;
    public LightGroup group;
    public LocalDateTime lastSeen;

    public enum LightGroup {
        FRONT,
        DITCH,
        FILL;
    }

    public Light(String ip_address, String mac_address, int request_power, int actual_power,
                 int rssi, LocalDateTime lastSeen, LightGroup group) {
        this.ip_address = ip_address;
        this.mac_address = mac_address;
        this.request_power = request_power;
        this.actual_power = actual_power;
        this.rssi = rssi;
        this.group = group;
        this.lastSeen = lastSeen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Light light = (Light) o;
        return request_power == light.request_power &&
                actual_power == light.actual_power &&
                rssi == light.rssi &&
                group == light.group &&
                lastSeen.equals(light.lastSeen) &&
                ip_address.equals(light.ip_address) &&
                mac_address.equals(light.mac_address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip_address, mac_address, request_power, actual_power, rssi,
                lastSeen, group);
    }

    private boolean isHot() {
        return request_power != actual_power;
    }


}
