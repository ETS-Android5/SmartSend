package com.example.smartsend.smartsendapp.utilities.app.order;

import java.io.Serializable;

public class ContactInfo implements Serializable {
    private String name;
    private String phoneNumber;

    public ContactInfo() {
    }

    public ContactInfo(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
