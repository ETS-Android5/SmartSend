package com.example.smartsend.smartsendapp.utilities.app.order;

import java.util.Date;
import java.util.Random;

public class Order {
    private Order.eOrderSize orderSize;
    private Order.eOrderStatus orderStatus;
    private ContactInfo pickUpContactInfo, dropOffContactInfo;
    private OrderAddressDetails pickUpAddress, dropOffAddress;
    private String courierNote;
    private String timestamp;
    private int orderNumber;
    private static Random random = new Random();

    public enum eOrderSize {
        SMALL_PACKAGE(5), MEDIUM_PACKAGE(10), LARGE_PACKAGE(15);

        int packageWeight;
        eOrderSize(int packageWeight) {
            this.packageWeight = packageWeight;
        }
    }


    public enum eOrderStatus {
        ODRDER_SUBMITTED, ORDER_PICKED_UP, ORDER_DELIVERED;
    }

    public Order(eOrderSize orderSize,
                 ContactInfo pickUpContactInfo,
                 ContactInfo dropOffContactInfo,
                 OrderAddressDetails pickUpAddress,
                 OrderAddressDetails dropOffAddress,
                 String courierNote) {
        Date now = new Date();

        this.timestamp = now.toString();
        this.orderStatus = eOrderStatus.ODRDER_SUBMITTED;
        this.orderSize = orderSize;
        this.pickUpContactInfo = pickUpContactInfo;
        this.dropOffContactInfo = dropOffContactInfo;
        this.pickUpAddress = pickUpAddress;
        this.dropOffAddress = dropOffAddress;
        this.courierNote = courierNote;
        this.orderNumber = generateOrderNumber();
    }

    private static int generateOrderNumber() {
        return random.nextInt((99999 - 10000) + 1) + 10000;
    }
}
