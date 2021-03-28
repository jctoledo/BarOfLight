package com.bertalabs.baroflight.lib;


public class Telematics extends Device {
    private boolean highBeamOn = false;

    public Telematics(String ip_address, String mac_address, int rssi){
        super(mac_address, ip_address, rssi);
    }

    public void setHighBeamOn(){
        highBeamOn = true;
    }

    public void setHighBeamOff(){
        highBeamOn = false;
    }


    public void setBeamState(int state){
        if (state == 1){
            highBeamOn = true;
        } else {
            highBeamOn = false;
        }
    }

    public boolean getHighBeamState(){
        return highBeamOn;
    }
}
