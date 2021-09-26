package com.example.smartsend.smartsendapp.utilities.app.order.price;

import com.example.smartsend.smartsendapp.utilities.app.order.Order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Fare implements IFare, Serializable {
    protected Fare fare;
    protected double farePrice = 5;
    protected HashMap<String, Double> fareDetails = new HashMap<>();
    protected Order order;

    public Fare() {
    }

    public Fare(Order order) {
        this.order = order;
    }

    @Override
    public double getPrice() {
        return farePrice;
    }

    @Override
    public Map<String, Double> getDetails() {
        return fareDetails;
    }
}
