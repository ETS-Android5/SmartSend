package com.example.smartsend.smartsendapp.utilities.app.order;

import com.google.android.gms.maps.model.Marker;

public class ClientPendingOrderItem {
    private Order order;
    private Marker marker;

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getOrderNumber() {
        return order.getOrderNumber();
    }

    public String getPrderDropOffAddress() {
        return order.getDropOffAddress().getAddress();
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ClientPendingOrderItem(Order order, Marker marker) {
        this.order = order;
        this.marker = marker;
    }

    public String getOrderTimestamp() {
        return order.getTimestamp();
    }
}
