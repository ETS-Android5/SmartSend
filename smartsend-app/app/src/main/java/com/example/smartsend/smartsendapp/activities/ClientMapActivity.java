package com.example.smartsend.smartsendapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.activities.containers.ClientMenuContainerActivity;
import com.example.smartsend.smartsendapp.adapters.ClientPendingOrdersAdapter;
import com.example.smartsend.smartsendapp.fragments.OrderDetailsFragment;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.example.smartsend.smartsendapp.utilities.app.order.ClientPendingOrderItem;
import com.example.smartsend.smartsendapp.utilities.app.order.Order;
import com.example.smartsend.smartsendapp.utilities.location.SmartSendLocationManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.example.smartsend.smartsendapp.utilities.AppController.TAG;

public class ClientMapActivity extends ClientMenuContainerActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION_PERMISSION = 99;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Marker marker;

    BottomSheetBehavior<RelativeLayout> pendingOrdersCardBehavior, pendingOrderDetailsCardBehavior;
    private LinearLayout noPendingOrders;
    private RecyclerView rvPendingOrders;

    private Order pendingOrder;
    private Handler mHandler = new Handler();
    private Runnable scanOrdersTask;
    private SmartSendLocationManager locationManager;
    private ClientPendingOrdersAdapter adapter;
    private String clientID;
    private FirebaseManager firebaseManager;
    private FirebaseDatabase firebaseDatabase;

    LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_client_map, null, false);
        drawer.addView(contentView, 0);

        firebaseManager = FirebaseManager.getInstance();
        firebaseDatabase = firebaseManager.getFirebaseDatabase();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Places.initialize(getApplicationContext(), "AIzaSyBUiecg0U9MpA9SNXI-UoPSUpvZV8tXYTg");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            clientID = extras.getString("clientID");
        }
        else {
            clientID = firebaseManager.getCurrentUser().getUid();
        }
        noPendingOrders = findViewById(R.id.noPendingOrders);
        rvPendingOrders = findViewById(R.id.rvPendingOrders);

        initializeActivityComponents();
        scanOrdersTask = () -> {
            getPendingOrdersFromClient(new View(this));
            mHandler.postDelayed(scanOrdersTask, 2000);
        };
        mHandler.postDelayed(scanOrdersTask, 2000);
    }

    public void getPendingOrdersFromClient(View view) {
        ArrayList<ClientPendingOrderItem> clientPendingOrderItems = new ArrayList<>();
        DatabaseReference pendingOrdersRef;
        pendingOrdersRef = firebaseDatabase.getReference("clients")
                .child(clientID)
                .child("pending_orders");

        pendingOrdersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot orderSnapshot : task.getResult().getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(order.getDropOffLatLng().getLatitude(), order.getDropOffLatLng().getLongitude())));
                    ClientPendingOrderItem clientPendingOrderItem = new ClientPendingOrderItem(order, marker);

                    clientPendingOrderItems.add(clientPendingOrderItem);
                }
                boolean emptyPendingOrders = clientPendingOrderItems.size() == 0;

                rvPendingOrders.setVisibility(emptyPendingOrders ? View.INVISIBLE : View.VISIBLE);
                noPendingOrders.setVisibility(emptyPendingOrders ? View.VISIBLE : View.INVISIBLE);
                adapter = new ClientPendingOrdersAdapter(clientPendingOrderItems);
                adapter.setOnItemClickListener(position -> showPendingOrderOnMap(clientPendingOrderItems.get(position)));
                rvPendingOrders.setAdapter(adapter);
                rvPendingOrders.setLayoutManager(new LinearLayoutManager(this));
            } else {
                Toast.makeText(ClientMapActivity.this, "Failed to load pending orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPendingOrderOnMap(ClientPendingOrderItem clientPendingOrderItem) {
        Marker orderMarker = clientPendingOrderItem.getMarker();
        Order pendingOrder = clientPendingOrderItem.getOrder();

        this.pendingOrder = pendingOrder;
        zoomToOrderMarker(orderMarker);
        pendingOrdersCardBehavior.setHideable(true);
        pendingOrdersCardBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        showPendingOrderFragment();
    }

    private void showPendingOrderFragment() {
        OrderDetailsFragment orderDetailsFragment = new OrderDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", pendingOrder);
        bundle.putBoolean("client", true);
        orderDetailsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.pending_order_view, orderDetailsFragment).addToBackStack(null).commit();

        RelativeLayout rl = findViewById(R.id.pending_order_view);
        pendingOrderDetailsCardBehavior = BottomSheetBehavior.from(rl);
        pendingOrderDetailsCardBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        pendingOrderDetailsCardBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    pendingOrdersCardBehavior.setHideable(false);
                    pendingOrdersCardBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    currentLocation = new LatLng(locationManager.getLat(), locationManager.getLng());
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
    }

    private void zoomToOrderMarker(Marker orderMarker) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(orderMarker.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void initializeActivityComponents() {
        initializeLocationManager();
        initializeCardBehavior();
    }

    private void initializeCardBehavior() {
        RelativeLayout rl = findViewById(R.id.pickUpContactCard);
        pendingOrdersCardBehavior = BottomSheetBehavior.from(rl);
        pendingOrdersCardBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void initializeLocationManager() {
        locationManager = new SmartSendLocationManager(this, 60000, 10);
        locationManager.setLocationManagerAndListener();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ClientMapActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        }
        else {
            mMap.setMyLocationEnabled(true);
        }
        initMap();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission is necessary to app", Toast.LENGTH_SHORT)
                        .show();
                ActivityCompat.requestPermissions(ClientMapActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
            }
            else {
                mMap.setMyLocationEnabled(true);
                initMap();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initMap() {
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setPadding(0, 0, 0, 200);
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
        mMap.setMapStyle(style);

        currentLocation = new LatLng(locationManager.getLat(), locationManager.getLng());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    public void goToAddressPickerActivity(View view) {
        Intent intent = new Intent(this,
                AddressPickerActivity.class);
        startActivity(intent);
    }

    public void openMenu(View view) {
        drawer.open();
    }

    public void closePendingOrderDetails(View view) {
        pendingOrdersCardBehavior.setHideable(false);
        pendingOrderDetailsCardBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        pendingOrdersCardBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void zoomOrderMark(View view) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }


    public void cancelOrder(View view) {
        showCancelDialog();
    }

    private void showCancelDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("CANCEL ORDER")
                .setMessage("Are you sure you want to cancel this order?")
                .setPositiveButton("Yes",null)
                .setNegativeButton("No",null)
                .create();

        alertDialog.setOnShowListener(dialogInterface -> {
            Button yesButton = (alertDialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
            Button noButton = (alertDialog).getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
            yesButton.setOnClickListener(view1 -> {
                alertDialog.dismiss();
                pendingOrderDetailsCardBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                deleteOrder(pendingOrder);
            });

            noButton.setOnClickListener(view1 -> alertDialog.dismiss());
        });

        alertDialog.show();
    }

    private void deleteOrder(Order pendingOrder) {
        DatabaseReference pendingOrdersRef = firebaseDatabase
                .getReference("pending_orders")
                .child(pendingOrder.getOrderNumber());
        DatabaseReference orderRef = firebaseDatabase
                .getReference("clients")
                .child(clientID)
                .child("pending_orders")
                .child(pendingOrder.getOrderNumber());

        orderRef.removeValue();
        pendingOrdersRef.removeValue();
    }

}
