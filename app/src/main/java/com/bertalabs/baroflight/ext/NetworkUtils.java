package com.bertalabs.baroflight.ext;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
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

    private static final String TAG = "NETWORKUTILS -" ;
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();

    public static HashMap<String, Light> findLights(String baseIp) throws UnhealthyLightException{
        final Map<String, String> rm = new HashMap<>();
        OkHttpClient client = new OkHttpClient();
        for (int i = 1; i <= 255; i++) {
            final String adr = baseIp + i + "/status";
            Request req = new Request.Builder().url(adr).build();
            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "SEARCHING FOR LIGHT - WRONG IP GUESS - "+adr);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful()){
                            Log.d(TAG, "SEARCHING FOR LIGHT - code: "+response.code()+" => "
                                    + response);
                        } else {
                            String lightReplyRaw = new BufferedReader(
                                    new InputStreamReader(responseBody.byteStream(), StandardCharsets.UTF_8)).lines()
                                    .collect(Collectors.joining("\n"));
                            if (lightReplyRaw.contains("MAC:") && lightReplyRaw.contains("<html>")) {
                                rm.put(adr, lightReplyRaw);
                            }
                        }
                    }
                }
            });

        }
        return makeIPToLight(rm);
    }

    /**
     * Return true if a light responds
     *
     * @param anHttpAddress an http://[IP]/status address where [IP] is replaced with some val
     * @return false if no http 200 is found
     */
    public static boolean checkLightStatus( String anHttpAddress)  {
        final boolean[] r = {false};
//        new Thread(new Runnable(){
//            @Override
//            public void run() {
//                // Do network action in this function

//                Request req = new Request.Builder().url(anHttpAddress).build();
//                try {
//
//                    Response res = client.newCall(req).execute();
//                    if (res.isSuccessful()){
//                        r[0] = true;
//                    }
//                } catch (IOException e) {
//                    Log.d(TAG, "something bad happened when asking for: "+anHttpAddress);
//                }
//            }
//        }).start();
        final Request req = new Request.Builder().url(anHttpAddress).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "SEARCHING FOR LIGHT checkLightStatus - WRONG IP GUESS - "+ call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()){
                        Log.d(TAG, "SEARCHING FOR LIGHT - code: "+response.code()+" => "
                                + response);
                    } else {
                        String lightReplyRaw = new BufferedReader(
                                new InputStreamReader(responseBody.byteStream(), StandardCharsets.UTF_8)).lines()
                                .collect(Collectors.joining("\n"));
                        if (lightReplyRaw.contains("MAC:") && lightReplyRaw.contains("<html>")) {
                            r[0] = true;
                        }
                    }
                }
            }
        });
        if (r[0])
            return true;
        return false;
    }


    private static HashMap<String, Light> makeIPToLight(Map<String, String> ipToRaw) throws UnhealthyLightException{
        Pattern mac = Pattern.compile("MAC: (.*)");
        Pattern requestedPwr = Pattern.compile("Requested Power .*:(.*)");
        Pattern actualPwr = Pattern.compile("Actual Power .*:(.*)");
        Pattern typeP = Pattern.compile("Type: (.*)");
        Pattern rssi = Pattern.compile("RSSI: (-\\d+)");
        HashMap<String, Light> rm = new HashMap<>();
        for (Map.Entry<String, String> entry : ipToRaw.entrySet()) {
            Matcher typeM = typeP.matcher(entry.getValue());
            Matcher macM = mac.matcher(entry.getValue());
            Matcher reqM = requestedPwr.matcher(entry.getValue());
            Matcher actM = actualPwr.matcher(entry.getValue());
            boolean health = Pattern.matches("Health: OK", entry.getValue());
            Matcher rssiM = rssi.matcher(entry.getValue());
            if (macM.find() && reqM.find() &&
                    actM.find() && rssiM.find()) {
                String m = Objects.requireNonNull(macM.group(1)).trim();
                int actP = Integer.parseInt(Objects.requireNonNull(actM.group(1)).trim());
                int reqP = Integer.parseInt(Objects.requireNonNull(reqM.group(1)).trim());
                int rssP = Integer.parseInt(Objects.requireNonNull(rssiM.group(1)).trim());
                Light.LightGroup group = Light.LightGroup.FRONT;
                // TODO clean this out once you can test it
                if (typeM.find()){
                    String lightType = Objects.requireNonNull(typeM.group(1).trim());
                    if (lightType.equals("Light")){
                        lightType = "FRONT";
                        Log.w(TAG, "found an old firmware light!");
                    }
                    group = Light.LightGroup.valueOf(lightType);
                } else {Log.d(TAG, "Could not find a type for this light!!!");}
                Light l = new Light(entry.getKey(), m, reqP, actP, rssP, LocalDateTime.now(),
                        group);
                if (health) {
                    rm.put(entry.getKey(), l);
                } else {
                    throw new UnhealthyLightException(l, "found unhealthy light");
                }
            }
        }
        return rm;
    }

}
