package com.example.smartsend.smartsendapp.utilities.location;

import android.content.Context;
import android.location.LocationManager;

/**
 * Created by AGM TAZIM on 1/3/2016.
 */
public class SmartSendLocationManager {

    private int minDistanceForUpdateLocation;
    private int minTimeForUpdateLocation;
    LocationManager locationManager;
    SmartSendLocationListener locationListener;
    Context ctx;

    public SmartSendLocationManager(Context ctx, int minTimeForUpdateLocation, int minDistanceForUpdateLocation) {
        this.minDistanceForUpdateLocation = minDistanceForUpdateLocation;
        this.minTimeForUpdateLocation = minTimeForUpdateLocation;
        this.ctx = ctx;
    }

    //Set Location Manager and Listener
    public void setLocationManagerAndListener() {
        this.locationListener = new SmartSendLocationListener(this.ctx);
        this.locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        boolean isGPSProviderEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkProviderEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        try {
            if (isGPSProviderEnable) {
                this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeForUpdateLocation, minDistanceForUpdateLocation, locationListener);
            }else if(isNetworkProviderEnable){
                this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTimeForUpdateLocation, minDistanceForUpdateLocation, locationListener);
            }
        } catch(SecurityException ignored) {
        }
    }

    //Get Minimum Distance for updating location
    public int getMinDistanceForUpdateLocation() {
        return minDistanceForUpdateLocation;
    }


    //Set Minimum Distance for updating location
    public void setMinDistanceForUpdateLocation(int minDistanceForUpdateLocation) {
        this.minDistanceForUpdateLocation = minDistanceForUpdateLocation;
    }

    //Get Minimum Time for updating location
    public int getMinTimeForUpdateLocation() {
        return minTimeForUpdateLocation;
    }

    //Set Minimum Time for updating location
    public void setMinTimeForUpdateLocation(int minTimeForUpdateLocation) {
        this.minTimeForUpdateLocation = minTimeForUpdateLocation;
    }

    public double getLat() {
        return locationListener.getLat();
    }


    public double getLng() {
        return locationListener.getLng();
    }
}
