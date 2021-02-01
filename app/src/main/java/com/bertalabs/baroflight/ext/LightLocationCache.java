package com.bertalabs.baroflight.ext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LightLocationCache {
    private LocalDateTime cacheStart;
    private static  LightLocationCache instance;
    private static final String BASE_HTTP_ADDR = "http://192.168.1.";
    private HashMap<String, String> ipToRawOutput = new HashMap<>();
    private HashMap<String, Light> ipToLight = new HashMap<>();


    private LightLocationCache(){
        if (cacheStart == null || Duration.between(cacheStart, LocalDateTime.now()).getSeconds() > 6){
            update();
            cacheStart = LocalDateTime.now();
        }
    }

    //TODO: detect a lost light
    //TODO: detect a new light
    public void update(){
        ipToRawOutput = findLightIPAndState(BASE_HTTP_ADDR);
        ipToLight = makeIPToLight(ipToRawOutput);
    }

    synchronized public static LightLocationCache getInstance(){
        if (instance == null){
            instance = new LightLocationCache();
        }
        return instance;
    }

    private HashMap<String, Light> makeIPToLight(HashMap<String, String> ipToRaw){
        Pattern mac = Pattern.compile("MAC: (.*)");
        Pattern requestedPwr = Pattern.compile("Requested Power .*:(.*)");
        Pattern actualPwr = Pattern.compile("Actual Power .*:(.*)");
        Pattern health = Pattern.compile("Health: OK");
        Pattern rssi = Pattern.compile("RSSI: (-\\d+)");

        for (Map.Entry<String, String> entry: ipToRaw.entrySet()){

            Matcher macM = mac.matcher(entry.getValue());
            Matcher reqM = requestedPwr.matcher(entry.getValue());
            Matcher actM = actualPwr.matcher(entry.getValue());
            Matcher healM = health.matcher(entry.getValue());
            Matcher rssiM = rssi.matcher(entry.getValue());
            if(macM.find() && reqM.find() &&
            actM.find() && rssiM.find()){
                String m = macM.group(1);
                int actP = Integer.valueOf(actM.group(1).trim());
                int reqP = Integer.valueOf(reqM.group(1).trim());
                int x = Integer.valueOf(rssiM.group(1).trim());
                System.out.println(m);
                int i =0;
            }

        }
        return null;
    }


    private HashMap<String, String> findLightIPAndState(String baseIp) {
        final Map<String, String> rm = new HashMap<>();
        OkHttpClient client = new OkHttpClient();
        for (int i = 1; i <= 255; i++) {
            final String adr = baseIp + String.valueOf(i)+"/status";
            Request req = new Request.Builder().url(adr).build();
            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try (ResponseBody responseBody = response.body()) {
                        if (!response.isSuccessful())
                            throw new IOException("Unexpected code " + response);
                        String lightReplyRaw = new BufferedReader(
                                new InputStreamReader(responseBody.byteStream(), StandardCharsets.UTF_8)).lines()
                                .collect(Collectors.joining("\n"));

                        if(lightReplyRaw.contains("MAC:") && lightReplyRaw.contains("<html>")){
                            rm.put(adr, lightReplyRaw);
                        }
                    }
                }
            });

        }
        return (HashMap<String, String>) rm;
    }


}
