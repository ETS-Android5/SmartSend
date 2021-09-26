package com.example.smartsend.smartsendapp.utilities.app.order;

import android.graphics.Bitmap;

import com.example.smartsend.smartsendapp.utilities.app.order.price.BaseFare;
import com.example.smartsend.smartsendapp.utilities.app.order.price.DistanceFare;
import com.example.smartsend.smartsendapp.utilities.app.order.price.Fare;
import com.example.smartsend.smartsendapp.utilities.app.order.price.IFare;
import com.example.smartsend.smartsendapp.utilities.app.order.price.TaxFare;
import com.example.smartsend.smartsendapp.utilities.app.order.price.TimeFare;
import com.example.smartsend.smartsendapp.utilities.app.order.signature.SerializableBitmap;
import com.example.smartsend.smartsendapp.utilities.app.order.signature.SignatureItem;
import com.example.smartsend.smartsendapp.utilities.location.LatLng;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Order implements Serializable {
    private String issuedClientID;
    private Order.eOrderSize orderSize;
    private Order.eOrderStatus orderStatus;
    private ContactInfo pickUpContactInfo, dropOffContactInfo;
    private OrderAddressDetails pickUpAddress, dropOffAddress;
    private String courierNote;
    private String timestamp;
    private String pickUpTimestamp;
    private String dropOffTimestamp;
    private LatLng pickUpLatLng, dropOffLatLng;
    private String orderNumber;
    private Fare fareDetails;
    private double currentPrice;
    private static final Random random = new Random();

    public enum eOrderSize {
        SMALL_PACKAGE(5), MEDIUM_PACKAGE(10), LARGE_PACKAGE(15);

        int packageWeight;
        eOrderSize(int packageWeight) {
            this.packageWeight = packageWeight;
        }
    }


    public enum eOrderStatus {
        ORDER_SUBMITTED, ORDER_CONFIRMED, ORDER_PICK_UP, ORDER_DROP_OFF, ORDER_COMPLETED, ORDER_CANCELED, ORDER_SCHEDULED;

        public String getStatus() {
            switch (this) {
                case ORDER_SUBMITTED: {
                    return "Submitted";
                }
                case ORDER_CONFIRMED: {
                    return "Confirmed by courier";
                }
                case ORDER_PICK_UP: {
                    return "Courier about to pick up order";
                }
                case ORDER_DROP_OFF: {
                    return "Courier is delivering order to destination";
                }
                case ORDER_COMPLETED: {
                    return "Order delivered";
                }
            }
            return null;
        }
    }

    public Order(){
    }

    public Order(String issuedClientID, eOrderSize orderSize,
                 LatLng pickUpLatLng,
                 LatLng dropOffLatLng,
                 ContactInfo pickUpContactInfo,
                 ContactInfo dropOffContactInfo,
                 OrderAddressDetails pickUpAddress,
                 OrderAddressDetails dropOffAddress,
                 String courierNote) {
        this.issuedClientID = issuedClientID;
        this.dropOffLatLng = dropOffLatLng;
        this.pickUpLatLng = pickUpLatLng;
        this.timestamp = new Date().toString();
        this.orderStatus = eOrderStatus.ORDER_SUBMITTED;
        this.orderSize = orderSize;
        this.pickUpContactInfo = pickUpContactInfo;
        this.dropOffContactInfo = dropOffContactInfo;
        this.pickUpAddress = pickUpAddress;
        this.dropOffAddress = dropOffAddress;
        this.courierNote = courierNote;
        this.orderNumber = generateOrderNumber();
        this.fareDetails = calcOrderPrice();
        this.currentPrice = fareDetails.getPrice();
    }

    public Fare getFareDetails() {
        return fareDetails;
    }

    private Fare calcOrderPrice() {
        Fare orderFare = new TaxFare(new TimeFare(new DistanceFare(new BaseFare(new Fare(this)))));

        currentPrice = orderFare.getPrice();
        return orderFare;
    }

    private static String generateOrderNumber() {
        return String.valueOf(random.nextInt((99999 - 10000) + 1) + 10000);
    }

    public double getCurrentPrice() {
        BigDecimal bd = new BigDecimal(currentPrice).setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getPickUpTimestamp() {
        return pickUpTimestamp;
    }

    public void setPickUpTimestamp(String pickUpTimestamp) {
        this.pickUpTimestamp = pickUpTimestamp;
    }

    public String getDropOffTimestamp() {
        return dropOffTimestamp;
    }

    public void setDropOffTimestamp(String dropOffTimestamp) {
        this.dropOffTimestamp = dropOffTimestamp;
    }

    public String getIssuedClientID() {
        return issuedClientID;
    }

    public void setIssuedClientID(String issuedClientID) {
        this.issuedClientID = issuedClientID;
    }

    public LatLng getPickUpLatLng() {
        return pickUpLatLng;
    }

    public void setPickUpLatLng(LatLng pickUpLatLng) {
        this.pickUpLatLng = pickUpLatLng;
    }

    public LatLng getDropOffLatLng() {
        return dropOffLatLng;
    }

    public void setDropOffLatLng(LatLng dropOffLatLng) {
        this.dropOffLatLng = dropOffLatLng;
    }

    public eOrderSize getOrderSize() {
        return orderSize;
    }

    public void setOrderSize(eOrderSize orderSize) {
        this.orderSize = orderSize;
    }

    public eOrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(eOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public ContactInfo getPickUpContactInfo() {
        return pickUpContactInfo;
    }

    public void setPickUpContactInfo(ContactInfo pickUpContactInfo) {
        this.pickUpContactInfo = pickUpContactInfo;
    }

    public ContactInfo getDropOffContactInfo() {
        return dropOffContactInfo;
    }

    public void setDropOffContactInfo(ContactInfo dropOffContactInfo) {
        this.dropOffContactInfo = dropOffContactInfo;
    }

    public OrderAddressDetails getPickUpAddress() {
        return pickUpAddress;
    }

    public void setPickUpAddress(OrderAddressDetails pickUpAddress) {
        this.pickUpAddress = pickUpAddress;
    }

    public OrderAddressDetails getDropOffAddress() {
        return dropOffAddress;
    }

    public void setDropOffAddress(OrderAddressDetails dropOffAddress) {
        this.dropOffAddress = dropOffAddress;
    }

    public String getCourierNote() {
        return courierNote;
    }

    public void setCourierNote(String courierNote) {
        this.courierNote = courierNote;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOrderNumber() {
        return orderNumber;
    }
}
