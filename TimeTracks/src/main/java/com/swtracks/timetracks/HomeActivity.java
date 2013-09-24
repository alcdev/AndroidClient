package com.swtracks.timetracks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.apache.http.NameValuePair;

import java.util.List;

public class HomeActivity extends Activity {
    private UserInfoTask userInfoTask = null;
    private DeviceListTask deviceListTask = null;
    private RegisterDeviceTask registerDeviceTask = null;
    //private LocationLoggingTask locationLoggingTask = null;

    TimeTracksAPI api;
    TextView userText;
    SharedPreferences settings;
    String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        api = new TimeTracksAPI(this);

        settings = getSharedPreferences("userinfo", MODE_PRIVATE);
        deviceID = settings.getString("device", "");

        getUserInfo();

        if (deviceID.isEmpty()) {
            getDeviceList();
        } else {
            startLocationLogging();
        }
    }

    private void startLocationLogging() {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LocationListener locationListener = new LocationLogger(HomeActivity.this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LocationLogger.ONE_MINUTE,
                    LocationLogger.TWENTY_METERS, locationListener);

            // May not be needed.
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            showErrorDialog("GPS Error", "GPS is not enabled.");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            default:
                return true; //super.onOptionsItemSelected(item);
        }
    }


    public void getUserInfo() {
        if (userInfoTask != null) {
            return;
        }

        userInfoTask = new UserInfoTask();
        userInfoTask.execute((Void) null);
    }

    public void getDeviceList(){
        if (deviceListTask != null) {
            return;
        }

        deviceListTask = new DeviceListTask();
        deviceListTask.execute((Void) null);
    }

    public void showErrorDialog(String title, String message) {
        AlertDialog.Builder  builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        Log.i("Error Dialog", message);
        builder.show();
    }

    public void showDeviceDialog(final List<NameValuePair> devices) {
        CharSequence[] charSequenceItems = new CharSequence[devices.size()];
        AlertDialog.Builder  builder = new AlertDialog.Builder(HomeActivity.this);

        for(int i = 0; i < devices.size(); i++) {
            charSequenceItems[i] = devices.get(i).getName();
        }

        builder.setTitle("Register Device");
        builder.setItems(charSequenceItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                if (registerDeviceTask != null) {
                    return;
                }

                deviceID = devices.get(i).getValue();
                registerDeviceTask = new RegisterDeviceTask();
                registerDeviceTask.execute((Void) null);
            }
        });
        builder.show();
    }

    public class UserInfoTask extends AsyncTask<Void, Void, Boolean> {
        String text;
        @Override
        protected Boolean doInBackground(Void... params) {
            text = api.GetUserInfo();

            // Doesn't matter what we return.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            userInfoTask = null;
            userText = (TextView) findViewById(R.id.userText);

            if (!text.isEmpty()){
                Log.i("text", text);
                userText.setText(text);
            } else{
                userText.setText("Error retrieving user info.");
            }
        }

        @Override
        protected void onCancelled() {
            userInfoTask = null;
        }
    }

    public class DeviceListTask extends AsyncTask<Void, Void, Boolean> {
        List<NameValuePair> devices;

        @Override
        protected Boolean doInBackground(Void... params) {
            devices = api.GetDevices();

            // Doesn't matter what we return.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            deviceListTask = null;

            if (devices != null) {
                showDeviceDialog(devices);
            } else {
                showErrorDialog("Register Device", "No unregistered devices found.");
            }
        }

        @Override
        protected void onCancelled() {
            deviceListTask = null;
        }
    }

    public class RegisterDeviceTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            if (deviceID.isEmpty()) {
                return false;
            }

            if (api.RegisterDevice(deviceID))
            {
                // Save the device ID to settings.
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("device", deviceID);
                editor.commit();
                return true;
            }


            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            registerDeviceTask = null;

            if (success) {
                // just relaunch the activity.
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            } else {
                showErrorDialog("Register Device", "Error registering device.");
            }
        }

        @Override
        protected void onCancelled() {
            registerDeviceTask = null;
        }
    }
}
