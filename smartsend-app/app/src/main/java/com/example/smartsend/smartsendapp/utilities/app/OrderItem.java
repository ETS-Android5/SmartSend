package com.example.smartsend.smartsendapp.utilities.app;

import com.example.smartsend.smartsendapp.utilities.app.order.Order;

public class OrderItem {
    private Order order;

    public OrderItem(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
