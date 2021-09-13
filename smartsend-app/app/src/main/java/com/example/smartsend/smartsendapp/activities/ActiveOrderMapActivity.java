package com.example.smartsend.smartsendapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.example.smartsend.smartsendapp.utilities.app.order.Order;
import com.example.smartsend.smartsendapp.utilities.location.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

import static com.example.smartsend.smartsendapp.utilities.app.order.Order.eOrderStatus.ORDER_COMPLETED;
import static com.example.smartsend.smartsendapp.utilities.app.order.Order.eOrderStatus.ORDER_DROP_OFF;
import static com.example.smartsend.smartsendapp.utilities.app.order.Order.eOrderStatus.ORDER_PICK_UP;

public class ActiveOrderMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Order order;
    private String phoneNumber;
    private TextView tvContactName;
    private TextView tvAddress;
    private TextView tvPrice;
    private LatLng addressLatLng;
    private FirebaseDatabase firebaseDatabase;
    private String riderID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_order_map);
        Bundle extra = getIntent().getExtras();

        firebaseDatabase = FirebaseManager.getInstance().getFirebaseDatabase();
        getActiveOrder(extra);
        initializeActivityComponents();
    }

    private void initializeActivityComponents() {
        tvContactName = findViewById(R.id.tvContactName);
        tvAddress = findViewById(R.id.tvAddress);
        tvPrice = findViewById(R.id.tvPrice);

        displayOrderDetails();
    }

    private void displayOrderDetails() {
        Order.eOrderStatus orderStatus = order.getOrderStatus();

        switch (orderStatus) {
            case ORDER_CONFIRMED: {
                showPickUpDetails();
                break;
            }
            case ORDER_PICK_UP: {
                showDropOffDetails();
                break;
            }
            case ORDER_DROP_OFF: {
                showReceipt();
                break;
            }
        }
    }

    private void showReceipt() {

    }

    private void showDropOffDetails() {
        tvContactName.setText(order.getDropOffContactInfo().getName());
        tvAddress.setText(order.getDropOffAddress().getAddress());
        tvPrice.setText("$123.45");
        addressLatLng = order.getDropOffLatLng();
        phoneNumber = order.getDropOffContactInfo().getPhoneNumber();
    }

    private void showPickUpDetails() {
        tvContactName.setText(order.getPickUpContactInfo().getName());
        tvAddress.setText(order.getPickUpAddress().getAddress());
        tvPrice.setText("$123.45");
        addressLatLng = order.getPickUpLatLng();
        phoneNumber = order.getPickUpContactInfo().getPhoneNumber();
    }

    private void getActiveOrder(Bundle extra) {
        if (extra != null) {
            order = (Order) extra.getSerializable("Order");
            riderID = extra.getString("riderID");
        }
    }

    public void closeActivity(View view) {
        finish();
    }

    public void callContact(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActiveOrderMapActivity.this, new String[] { Manifest.permission.CALL_PHONE }, PackageManager.PERMISSION_GRANTED);
        }
        else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));//change the number
            startActivity(callIntent);
        }
    }

    public void naviagteToAddress(View view) {
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", addressLatLng.getLatitude(), addressLatLng.getLongitude());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }


    public void updateOrderStatus(View view) {
        Order.eOrderStatus orderStatus = order.getOrderStatus();

        switch (orderStatus) {
            case ORDER_CONFIRMED: {
                updateStatus(ORDER_PICK_UP);
                break;
            }
            case ORDER_PICK_UP: {
                updateStatus(ORDER_DROP_OFF);
                break;
            }
            case ORDER_DROP_OFF: {
                updateStatus(ORDER_COMPLETED);
                addToCompletedOrders();
                removeFromActiveOrders();
                removeFromClientPendingOrders();
                addToClientCompletedOrders();
                break;
            }
        }
    }

    private void addToClientCompletedOrders() {
        DatabaseReference completedOrdersRef = firebaseDatabase
                .getReference("clients")
                .child(order.getIssuedClientID())
                .child("completed_orders")
                .child(order.getOrderNumber());

        completedOrdersRef.setValue(order);
    }

    private void removeFromClientPendingOrders() {
        DatabaseReference pendingOrderRef = firebaseDatabase
                .getReference("clients")
                .child(order.getIssuedClientID())
                .child("pending_orders")
                .child(order.getOrderNumber());

        pendingOrderRef.removeValue();
    }

    private void addToCompletedOrders() {
        DatabaseReference completedOrdersRef = firebaseDatabase
                .getReference("riders")
                .child(riderID)
                .child("completed_orders")
                .child(order.getOrderNumber());

        completedOrdersRef.setValue(order);
    }

    private void removeFromActiveOrders() {
        DatabaseReference activeOrdersRef = firebaseDatabase
                .getReference("riders")
                .child(riderID)
                .child("active_orders")
                .child(order.getOrderNumber());

        activeOrdersRef.removeValue();
    }

    private void updateStatus(Order.eOrderStatus orderStatus) {
        DatabaseReference orderRef = firebaseDatabase
                .getReference("clients")
                .child(order.getIssuedClientID())
                .child("pending_orders")
                .child(order.getOrderNumber())
                .child("orderStatus");

        DatabaseReference activeOrderRef = firebaseDatabase
                .getReference("riders")
                .child(riderID)
                .child("active_orders")
                .child(order.getOrderNumber())
                .child("orderStatus");

        order.setOrderStatus(orderStatus);
        orderRef.setValue(orderStatus);
        activeOrderRef.setValue(orderStatus);
        displayOrderDetails();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActiveOrderMapActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        }
        else {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setPadding(0, 0, 0, 200);
            MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
            mMap.setMapStyle(style);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(new com.google.android.gms.maps.model.LatLng(addressLatLng.getLatitude(), addressLatLng.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }
}
