package com.example.smartsend.smartsendapp.utilities.app.order.price;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class TaxFare extends Fare {
    private Fare fare;
    private static final double tax = 0.17;

    public TaxFare(Fare fare) {
        super(fare.order);
        this.fare = fare;

        farePrice = calcTaxFare(fare.getPrice());
        fareDetails = fare.fareDetails;
        addTaxFareDetails();
    }

    @Override
    public double getPrice() {
        return fare.getPrice() + farePrice;
    }

    private double calcTaxFare(double price) {
        BigDecimal bd = new BigDecimal(price * tax).setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }

    public void addTaxFareDetails() {
        fareDetails.put("Tax (" + (int)(tax * 100) + " percent)", farePrice);
    }
}
