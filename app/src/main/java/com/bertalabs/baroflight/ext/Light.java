package com.bertalabs.baroflight.ext;

public class Light {
    public String ip_address = null;
    public String mac_address = null;
    public int request_power = 0;
    public int actual_power = 0;

    private boolean is_hot(){
        return request_power != actual_power;
    }
}
