package com.example.familymap.Client;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import android.util.Log;

public class Proxy {
    public static final String tag = "Proxy";
    private static String serverIP = "";
    private static String serverPort = "";

    public static void setIP(String IP) {serverIP = IP;}
    public static void setPort(String port) {serverPort = port;}

    public static JSONObject doAuthenticationAction(String methodCall, String authToken) {
        URL url;
        JSONObject result;
        try {
            url = new URL("http://" + serverIP + ":" + serverPort + "/" + methodCall);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", authToken);
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.e(tag, "got good response code");
                InputStream responseBody = connection.getInputStream();
                ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int length = 0;
                while ((length = responseBody.read(buffer)) != -1) {
                    byteOS.write(buffer, 0, length);
                }
                Log.e(tag, "made it through while loop");
                String responseBodyData = byteOS.toString();
                Log.e(tag, responseBodyData);
                return new JSONObject(responseBodyData);
            }
            else
            {
                Log.e(tag, "got bad response code");
            }
        }catch (Exception e) { Log.e(tag, "exception in auth method Call " + e.getMessage()+" tostring "+e.toString());}

        return null;
    }

    public static JSONObject doPostAction(String methodCall, JSONObject jObject){
        URL url;
        try {
            url = new URL("http://" + serverIP + ":" + serverPort + "/" + methodCall);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            Log.e(tag, "Before connection");
            OutputStream requestBody = connection.getOutputStream();
            requestBody.write(jObject.toString().getBytes());
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.e(tag,"connected");
                InputStream responseBody = connection.getInputStream();
                ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = responseBody.read(buffer)) != -1) {
                    byteOS.write(buffer, 0, length);
                }
                String responseBodyData = byteOS.toString();
                return new JSONObject(responseBodyData);
            }
            else
            {
                Log.e(tag, "got bad response code");
            }
        }
        catch (Exception e) { Log.e(tag, "exception in post action "+ e.getMessage());}
        return null;
    }




}