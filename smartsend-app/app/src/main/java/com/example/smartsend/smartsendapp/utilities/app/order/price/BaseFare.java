package com.example.smartsend.smartsendapp.utilities.app.order.price;

import com.example.smartsend.smartsendapp.utilities.app.order.Order;

import java.util.Map;

public class BaseFare extends Fare {
    private Order.eOrderSize orderSize;
    private double farePrice;
    private static final int baseFare = 15;

    public BaseFare(Fare fare) {
        super(fare.order);

        this.fare = fare;
        orderSize = this.order.getOrderSize();
        farePrice = calcSizeFare();
        fareDetails = fare.fareDetails;
        addBaseFareDetails();
    }

    @Override
    public double getPrice() {
        return fare.getPrice() + baseFare + farePrice;
    }

    private double calcSizeFare() {
        switch (orderSize) {
            case SMALL_PACKAGE: {
                return 5;
            }
            case MEDIUM_PACKAGE: {
                return 10;
            }
            case LARGE_PACKAGE: {
                return 15;
            }
        }
        return 5;
    }

    private void addBaseFareDetails() {
        fareDetails.put("Base price (order size)", farePrice);
    }
}
