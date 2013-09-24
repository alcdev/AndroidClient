package com.swtracks.timetracks;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class LocationLogger implements LocationListener {

    public final static int ONE_MINUTE = 60 * 1000;
    public final static int TEN_MINUTES = 10 * 60 * 1000;
    public final static int FIVE_METERS = 5;
    public final static int TWENTY_METERS = 20;

    TimeTracksAPI api;
    Double longitude, latitude;
    private LocationLoggingTask locationLoggingTask = null;

    public LocationLogger(Context c) {
        api = new TimeTracksAPI(c);
    }

    @Override
    public void onLocationChanged(Location loc) {
        Log.i("loc", "changed");
        Double lng = loc.getLatitude();
        Double lat = loc.getLongitude();

        if (lat != latitude && lng != longitude) {
            latitude = lat;
            longitude = lng;

            if (locationLoggingTask == null) {
                locationLoggingTask = new LocationLoggingTask();
                locationLoggingTask.execute((Void) null);
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                break;
            case LocationProvider.OUT_OF_SERVICE:
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                break;
        }
    }

    public class LocationLoggingTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return api.LogLocation(longitude, latitude);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Do not null out task.
            locationLoggingTask = null;

            if (!success) {
                //showErrorDialog("GPS Error", "GPS is not enabled.");
            }
        }

        @Override
        protected void onCancelled() {
            locationLoggingTask = null;
        }
    }
}