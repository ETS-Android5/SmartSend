package com.example.smartsend.smartsendapp.utilities.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.example.smartsend.smartsendapp.utilities.ConnectivityDetector;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.example.smartsend.smartsendapp.utilities.app.Client;
import com.example.smartsend.smartsendapp.utilities.UserLocalStore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by AGM TAZIM on 1/3/2016.
 */
public class SmartSendLocationListener implements LocationListener {

    private double lat, lng;
    Context ctx;
    ConnectivityDetector connectivityDetector;
    FirebaseDatabase firebaseDatabase;

    public SmartSendLocationListener(Context ctx){
        this.ctx = ctx;
        firebaseDatabase = FirebaseManager.getInstance().getFirebaseDatabase();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
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

    private void changeRiderLocation(final double lat, final double lng) {
        UserLocalStore userLocalStore = UserLocalStore.getInstance(ctx);
        Client loggedInRider = userLocalStore.getLogedInClient();
        String loggedInRiderId = loggedInRider.getId();

        try {
            DatabaseReference databaseRef = firebaseDatabase.getReference("riders").child(loggedInRiderId);
            databaseRef.child("lat").setValue(lat);
            databaseRef.child("lng").setValue(lng);

        } catch (Exception ex) {
            Toast.makeText(ctx, "Updating location error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
