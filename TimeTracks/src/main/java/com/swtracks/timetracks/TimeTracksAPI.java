package com.swtracks.timetracks;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimeTracksAPI {
    private JSONParser parser;
    private String server, username, password, account, device;
    private String SUCCESS = "success";


    public TimeTracksAPI(Context c){
        parser = new JSONParser(c);

        SharedPreferences settings = c.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        username = settings.getString("username", "nlayne");
        password = settings.getString("password", "qwerty789");
        account = settings.getString("account", "1000");
        server = settings.getString("server", "http://10.0.2.2:1390");
        device = settings.getString("device", "e5b425b2-a7cd-489f-bf45-ceb28691bd35");
    }

    public Boolean Login(){
        return Login(username, password, account);
    }
    public Boolean Login(String un, String pw, String ac){
        List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("un", un));
        postData.add(new BasicNameValuePair("pw", pw));
        postData.add(new BasicNameValuePair("ac", ac));

        try {
            JSONObject json = parser.getJSONFromUrl(server + "/api/login", postData);
            Log.i("Login", json.getString("status"));
            return json.getString("status").equals(SUCCESS);
        } catch (JSONException e) {
            Log.i("API Error", e.getMessage());
            return false;
        }
    }

    public Boolean Logout() {
        try {
            JSONObject json = parser.getJSONFromUrl(server + "/api/logout");
            return json.getString("status").equals(SUCCESS);
        } catch (JSONException e) {
            Log.i("API Error", e.getMessage());
            return false;
        }
    }

    public Boolean LogLocation(Double longitude, Double latitude, String deviceId, String deviceSerial) {

        List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("lat", longitude.toString()));
        postData.add(new BasicNameValuePair("lng", latitude.toString()));
        postData.add(new BasicNameValuePair("id", deviceId));
        postData.add(new BasicNameValuePair("serial", deviceSerial));

        try {
            JSONObject json = parser.getJSONFromUrl(server + "/api/loglocation", postData);
            return json.getString("status").equals(SUCCESS);
        } catch (JSONException e) {
            Log.i("API Error", e.getMessage());
            return false;
        }
    }

    public List<String> GetDevices() {
        return GetDevices(false);
    }

    public List<String> GetDevices(boolean all){
        List<String> devicesIDs = new ArrayList<String>();
        JSONObject json;

        if (all) {
            json = parser.getJSONFromUrl(server + "/api/getdevices");
        } else {
            json = parser.getJSONFromUrl(server + "/api/getdevices?unreg=1");
        }

        try {
            String result = json.getString("data");
            JSONArray devices = new JSONArray(result);

            for(int i = 0; i < devices.length(); i++){
                JSONObject c = devices.getJSONObject(i);
                devicesIDs.add(c.getString("id"));
            }

            return devicesIDs;
        } catch (JSONException e) {
            Log.i("API Error", e.getMessage());
            return null;
        }
    }

    public String GetUserInfo() {
        try {
            JSONObject json = parser.getJSONFromUrl(server + "/api/userinfo");
            JSONObject data = json.getJSONObject("data");

            String info = String.format("Logged in as %s (%d) for %s (%d)",
                    data.getString("username"),
                    data.getInt("userID"),
                    data.getString("accountName"),
                    data.getInt("accountID")
            );
            return info;
        } catch (JSONException e) {
            Log.i("API Error", e.getMessage());
            return "";
        }
    }

    public Boolean RegisterDevice(String deviceId) {
        List<NameValuePair> postData = new ArrayList<NameValuePair>();
        postData.add(new BasicNameValuePair("id", deviceId));
        postData.add(new BasicNameValuePair("serial", Settings.Secure.ANDROID_ID));

        try {
            JSONObject json = parser.getJSONFromUrl(server + "/api/registerdevice", postData);
            return json.getString("status").equals(SUCCESS);
        } catch (JSONException e) {
            Log.i("API Error", e.getMessage());
            return false;
        }
    }
}
