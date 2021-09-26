package com.example.smartsend.smartsendapp.utilities.app.order.price;

import java.util.Map;

public interface IFare {
    double getPrice();
    Map<String, Double> getDetails();
}
