package com.example.smartsend.smartsendapp.utilities.app.order.price;

import android.location.Location;

import com.example.smartsend.smartsendapp.utilities.location.LatLng;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;

public class DistanceFare extends Fare {
    private Fare fare;
    private LatLng pickUpLatLng, dropOffLatLng;
    private float distance;
    private static final int minDistance = 5;

    public DistanceFare(Fare fare) {
        super(fare.order);

        this.fare = fare;
        pickUpLatLng = order.getPickUpLatLng();
        dropOffLatLng = order.getDropOffLatLng();
        farePrice = calcDistanceFare();
        fareDetails = fare.fareDetails;
        addDistanceFareDetails();
    }

    @Override
    public double getPrice() {
        return fare.getPrice() + farePrice;
    }

    private double calcDistanceFare() {
        Location loc1 = new Location(""), loc2 = new Location("");
        double price = 0;
        float locationDistance;

        loc1.setLatitude(pickUpLatLng.getLatitude());
        loc1.setLongitude(pickUpLatLng.getLongitude());
        loc2.setLatitude(dropOffLatLng.getLatitude());
        loc2.setLongitude(dropOffLatLng.getLongitude());
        locationDistance = loc1.distanceTo(loc2) / 1000;
        distance = locationDistance;

        while (distance > minDistance) {
            price += 2;
            distance -= minDistance;
        }
        BigDecimal bd = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }

    public void addDistanceFareDetails() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(0);

        fareDetails.put("Distance Fare (over " + minDistance + "km)", farePrice);
    }
}
