package com.example.smartsend.smartsendapp.utilities;

public class ClientHistoryItem {
    private String pickUpAddress, dropOffAddress, pickUpTimestamp, dropOffTimestamp;
    private int orderNumber;

    public ClientHistoryItem(String pickUpAddress, String dropOffAddress, String pickUpTimestamp, String dropOffTimestamp, int orderNumber) {
        this.pickUpAddress = pickUpAddress;
        this.dropOffAddress = dropOffAddress;
        this.pickUpTimestamp = pickUpTimestamp;
        this.dropOffTimestamp = dropOffTimestamp;
        this.orderNumber = orderNumber;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getPickUpAddress() {
        return pickUpAddress;
    }

    public String getDropOffAddress() {
        return dropOffAddress;
    }

    public String getPickUpTimestamp() {
        return pickUpTimestamp;
    }

    public String getDropOffTimestamp() {
        return dropOffTimestamp;
    }
}
