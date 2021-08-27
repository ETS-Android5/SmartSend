package com.example.smartsend.smartsendapp.utilities;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by AGM TAZIM on 1/3/2016.
 */
public class SmartSendLocationListener implements LocationListener {

    private double lat, lng, alt, acc, time;
    Context ctx;
    ConnectivityDetector connectivityDetector;

    public SmartSendLocationListener(Context ctx){
        this.ctx = ctx;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
        this.alt = location.getAltitude();
        this.acc = location.getAccuracy();
        this.time = location.getTime();

        Toast.makeText(ctx, "Lat : "+this.lat+" \n Lng : "+this.lng+" \nAlt : "+this.alt+" \n Acc : "+this.acc+" \n Time : "+this.time, Toast.LENGTH_LONG).show();

        changeRiderLocation(this.lat, this.lng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    //Change rider location
    private void changeRiderLocation(final double lat, final double lng) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        UserLocalStore userLocalStore = UserLocalStore.getInstance(ctx);
        Rider loggedInRider = userLocalStore.getLoggedInRider();
        String loggedInRiderId = loggedInRider.getId();

        try {
            DatabaseReference databaseRef = FirebaseManager.getInstance().getFirebaseDatabase().getReference("/riders/" + loggedInRiderId);
            databaseRef.child("/lat").setValue(lat);
            databaseRef.child("/lng").setValue(lng);
        } catch (Exception ex) {
            Toast.makeText(ctx, "Updating location error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
