package com.bertalabs.baroflight.utils;

import android.util.Log;

import com.bertalabs.baroflight.ext.LightIntensity;
import com.bertalabs.baroflight.lib.Device;
import com.bertalabs.baroflight.lib.Light;
import com.bertalabs.baroflight.lib.Telematics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetworkUtils {

    private static final String TAG = "NETWORKUTILS -";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();

    private static final Pattern macAddrPattern = Pattern.compile("\\w+:\\w+:\\w+:\\w+:\\w+:\\w+");
    private static final Pattern reqPowerPattern = Pattern.compile("Requested Power .*:(.*)");
    private static final Pattern actualPwrPattern = Pattern.compile("Actual Power .*:(.*)");
    private static final Pattern typePattern = Pattern.compile("Type: (.*)");
    private static final Pattern healthPattern = Pattern.compile("Health: OK");
    private static final Pattern rssiPattern = Pattern.compile("RSSI: (-\\d+)");
    private static final Pattern tempPattern = Pattern.compile("Temperature: (.*)");
    private static final Pattern highBeamPattern = Pattern.compile("High Beam: (.*)");

    public static HashMap<String, Device> findDevices(String baseIp) throws UnhealthyLightException {
        final Map<String, String> rm = new HashMap<>();
        final List<String> wrongIPGuesses = new ArrayList<>();
        for (int i = 1; i <= 255; i++) {
            final String lightReqAdr = baseIp + i + "/status";
            Request req = new Request.Builder().url(lightReqAdr).build();
            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    try {
                        wrongIPGuesses.add(lightReqAdr);
                    } catch (ArrayIndexOutOfBoundsException e2) {
                        e2.printStackTrace();
                        Log.e(TAG, "\n\n\n" + wrongIPGuesses.size() + "\n\n\n" + lightReqAdr);
                    }
                    if (wrongIPGuesses.size() % 55 == 0) {
                        Log.i(TAG, "Searched " + wrongIPGuesses.size() + " wrong ips!");
                    }
                }

                @Override
                public void onResponse(Call call, Response response) {
                    try (ResponseBody responseBody = response.body()) {
                        String lightReplyRaw = new BufferedReader(
                                new InputStreamReader(
                                        responseBody.byteStream(),
                                        StandardCharsets.UTF_8)).lines()
                                .collect(Collectors.joining("\n"));
                        if (lightReplyRaw.contains("MAC:") &&
                                lightReplyRaw.contains("<html>")) {
                            String key = lightReqAdr.substring(0, lightReqAdr.lastIndexOf("/"));
                            rm.put(key, lightReplyRaw);
                        }
                    }
                }
            });
        }
        return makeIPToDevice(rm);
    }


    public static int setLightIntensity(String anHttpAddress, LightIntensity anInten) {
        String setVal = "1023";
        switch (anInten) {
            case HIGH:
                setVal = "1023";
                break;
            case MEDIUM:
                setVal = "600";
                break;
            case LOW:
                setVal = "300";
                break;
            case OFF:
                setVal = "0";
        }

        final Request req = new Request.Builder().url(anHttpAddress + "/set/" + setVal).build();

        client.newCall(req).enqueue(new Callback() {
            final int[] r = {1023};

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(
                        TAG,
                        "SEARCHING FOR LIGHT checkLightStatus - WRONG IP GUESS - "
                                + call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    String lightReplyRaw = new BufferedReader(
                            new InputStreamReader(
                                    responseBody.byteStream(),
                                    StandardCharsets.UTF_8)).lines()
                            .collect(Collectors.joining("\n"));
                    if (lightReplyRaw.contains("Requested") && lightReplyRaw.contains("<html>")) {
                        r[0] = 1;
                    }
                }
            }
        });
        return -1;
    }
    //TODO: check that this method actually works

    /**
     * Return true if a device responds
     *
     * @param anHttpAddress an http://[IP]/status address where [IP] is replaced with some val
     * @return false if no http 200 is found
     */
    public static boolean checkDeviceStatus(String anHttpAddress) {
        final boolean[] r = {false};
        final Request req = new Request.Builder().url(anHttpAddress).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(
                        TAG,
                        "SEARCHING FOR LIGHT checkLightStatus - WRONG IP GUESS - "
                                + call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    String lightReplyRaw = new BufferedReader(
                            new InputStreamReader(
                                    responseBody.byteStream(),
                                    StandardCharsets.UTF_8)).lines()
                            .collect(Collectors.joining("\n"));
                    if (lightReplyRaw.contains("MAC:") && lightReplyRaw.contains("<html>")) {
                        r[0] = true;
                    }
                }
            }
        });
        return r[0];
    }


    private static HashMap<String, Device> makeIPToDevice(Map<String, String> ipToRaw)
            throws UnhealthyLightException {
        HashMap<String, Device> rm = new HashMap<>();
        for (Map.Entry<String, String> entry : ipToRaw.entrySet()) {
            Matcher macM = macAddrPattern.matcher(entry.getValue());
            Matcher typeM = typePattern.matcher(entry.getValue());
            Matcher rssiM = rssiPattern.matcher(entry.getValue());
            Matcher beamM = highBeamPattern.matcher(entry.getValue());
            Matcher requestedPwrM = reqPowerPattern.matcher(entry.getValue());
            Matcher actualPowerM = actualPwrPattern.matcher(entry.getValue());
            Matcher tempM = tempPattern.matcher(entry.getValue());

            if (typeM.find() && macM.find() && rssiM.find()) {
                Light.DeviceGroup deviceT =
                        Light.DeviceGroup.valueOf(
                                Objects.requireNonNull(typeM.group(1).trim()));
                // mac address and RSSI
                String macAddress = macM.group().trim();
                Integer rssi = Integer.valueOf(rssiM.group(1).trim());
                if (deviceT.equals(Light.DeviceGroup.TELEMATICS)
                        && beamM.find()) {
                    // TELEMATICS
                    Integer highBeanVal = Integer.valueOf(
                            beamM.group(1).trim());
                    Telematics tm = new Telematics(entry.getKey(), macAddress, rssi);
                    tm.setBeamState(highBeanVal);
                    rm.put(entry.getKey(), tm);
                } else if (!deviceT.equals(Light.DeviceGroup.TELEMATICS)
                        && tempM.find() && requestedPwrM.find() && actualPowerM.find()  ) {
                    // LIGHT
                    int actualPower = Integer.parseInt(Objects.requireNonNull(actualPowerM.group(1)).trim());
                    int requestedPower = Integer.parseInt(
                            Objects.requireNonNull(requestedPwrM.group(1)).trim());
                    double temperature = Double.parseDouble(
                            Objects.requireNonNull(tempM.group(1)).trim());
                    boolean health = healthPattern.matcher(entry.getValue()).find();
                    Light l = new Light(
                            entry.getKey(), macAddress, temperature, health,
                            requestedPower, actualPower, rssi, LocalDateTime.now(), deviceT
                    );
                    rm.put(entry.getKey(), l);
                    if (!health) throw new UnhealthyLightException(l, "found an unhealthy light");
                }
            }
        }
        return rm;
    }
}
