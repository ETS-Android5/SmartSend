package com.example.smartsend.smartsendapp.utilities.app.order;

import java.io.Serializable;

public class OrderAddressDetails implements Serializable {
    private String address;
    private String entrance;
    private int apartmentNumber;
    private int floor;

    public OrderAddressDetails() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public int getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(int apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }
}
