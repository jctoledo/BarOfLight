package com.bertalabs.baroflight.lib;

import java.util.Objects;

public class Device {

    public String macAddress;
    public String ipAddress;
    public int rssi;

    public Device(String macAddress, String ipAddress, int rssi) {
        this.macAddress = macAddress;
        this.ipAddress = ipAddress;
        this.rssi = rssi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(macAddress, device.macAddress) &&
                Objects.equals(ipAddress, device.ipAddress) &&
                Objects.equals(rssi, device.rssi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(macAddress, ipAddress, rssi);
    }
}
