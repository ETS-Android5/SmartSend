package com.example.smartsend.smartsendapp.utilities.app.order.price;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;

public class TimeFare extends Fare {
    private Fare fare;
    private String pickUpTime, dropOffTime;
    private int orderTime;
    private static final int minTime = 15;

    public TimeFare(Fare fare) {
        super(fare.order);

        this.fare = fare;
        pickUpTime = order.getPickUpTimestamp();
        dropOffTime = order.getDropOffTimestamp();
        farePrice = calcTimeFare();
        fareDetails = fare.fareDetails;
        addTimeFareDetails();
    }

    @Override
    public double getPrice() {
        return fare.getPrice() + farePrice;
    }

    private double calcTimeFare() {
        double price = 0;

        if (pickUpTime != null && dropOffTime != null) {
            Date pickUpDate = new Date(pickUpTime);
            Date dropOffDate = new Date(dropOffTime);
            long diff = pickUpDate.getTime() - dropOffDate.getTime();
            long diffMinutes = diff / (60 * 1000) % 60;

            orderTime = (int) diff;
            while (diffMinutes > minTime) {
                price += 0.5;
                diffMinutes--;
            }
        }
        BigDecimal bd = new BigDecimal(price).setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }

    private void addTimeFareDetails() {
        fare.getDetails().put("Time fare (" + orderTime + " min)", farePrice);
    }
}
