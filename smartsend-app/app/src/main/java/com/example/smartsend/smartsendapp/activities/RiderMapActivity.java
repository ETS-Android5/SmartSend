package com.example.smartsend.smartsendapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arsy.maps_library.MapRadar;
import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.activities.containers.RiderMenuContainerActivity;
import com.example.smartsend.smartsendapp.adapters.PendingOrdersAdapter;
import com.example.smartsend.smartsendapp.fragments.OrderDetailsFragment;
import com.example.smartsend.smartsendapp.services.PendingOrderNotifierService;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.example.smartsend.smartsendapp.utilities.UserLocalStore;
import com.example.smartsend.smartsendapp.utilities.app.order.Order;
import com.example.smartsend.smartsendapp.utilities.app.order.ClientPendingOrderItem;
import com.example.smartsend.smartsendapp.utilities.location.SmartSendLocationManager;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.smartsend.smartsendapp.utilities.AppController.TAG;

public class RiderMapActivity extends RiderMenuContainerActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION_PERMISSION = 99;
    private GoogleMap mMap;
    private Marker marker;
    private SupportMapFragment mapFragment;
    private MapRadar mapRadar;

    private SmartSendLocationManager locationManager;
    private SwitchCompat swStatus;
    private Handler mHandler = new Handler();
    private Runnable scanOrdersTask;
    private BottomSheetBehavior<RelativeLayout> pendingOrdersCardBehavior, pendingOrderDetailsCardBehavior;
    private LinearLayout noPendingOrders;
    private RecyclerView rvPendingOrders;
    private PendingOrdersAdapter adapter;
    private FirebaseManager firebaseManager;
    private FirebaseDatabase firebaseDatabase;
    private String riderID;
    private DatabaseReference pendingOrdersRef;

    private LatLng currentLocation;
    private PendingOrdersReceiver receiver;
    private Order pendingOrder;

    @Override
    protected void onResume() {
        super.onResume();
        if (mMap != null) {
            LatLng riderLatLng = new LatLng(locationManager.getLat(), locationManager.getLng());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(riderLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            initializeStatusButton();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopOrderScan();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_rider_map, null, false);
        drawer.addView(contentView, 0);

        riderID = UserLocalStore.getInstance(this).getLoggedInRider().getId();
        if (riderID != null)
        {
            Intent intent = new Intent(this, PendingOrderNotifierService.class);
            startService(intent);
            receiver = new PendingOrdersReceiver();
            registerReceiver(receiver, new IntentFilter("GET_PENDING_ORDER"));
            mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            Places.initialize(getApplicationContext(), "AIzaSyBUiecg0U9MpA9SNXI-UoPSUpvZV8tXYTg");

            firebaseManager = FirebaseManager.getInstance();
            firebaseDatabase = firebaseManager.getFirebaseDatabase();
            pendingOrdersRef = firebaseDatabase.getReference("pending_orders");

            initializeActivityComponents();
        }
    }

    private void initializeActivityComponents() {
        noPendingOrders = findViewById(R.id.noPendingOrders);
        rvPendingOrders = findViewById(R.id.rvPendingOrders);

        initializeLocationManager();
        initializeCardBehavior();
        initializeStatusButton();
        initializeLocationManager();
        initializePendingOrderCardBehavior();
    }

    private void initializePendingOrderCardBehavior() {
        RelativeLayout rl = findViewById(R.id.pending_order_view);
        pendingOrderDetailsCardBehavior = BottomSheetBehavior.from(rl);
        pendingOrderDetailsCardBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void initializeCardBehavior() {
        RelativeLayout rl = findViewById(R.id.pickUpContactCard);
        pendingOrdersCardBehavior = BottomSheetBehavior.from(rl);
        pendingOrdersCardBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void initializeStatusButton() {
        swStatus = findViewById(R.id.statusSwitch);
        swStatus.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                changeRiderStatus("Active");
                addToAvailableRiders();
                startOrderScan();
            }
            else {
                changeRiderStatus("Inactive");
                removeFromAvailableRiders();
                stopOrderScan();
            }
        });
        getRiderStatus();
        if (swStatus.isActivated()) {
            startOrderScan();
        }
    }

    private void removeFromAvailableRiders() {
        DatabaseReference availableRidersRef = firebaseDatabase.getReference("available_riders");

        availableRidersRef.child(riderID).removeValue();
    }

    private void addToAvailableRiders() {
        GeoFire geoFire = new GeoFire(firebaseDatabase.getReference().child("available_riders"));

        geoFire.setLocation(riderID, new GeoLocation(currentLocation.latitude, currentLocation.longitude),
                (key, error) -> {});
    }

    private void stopOrderScan() {
        if (mapRadar != null && mapRadar.isAnimationRunning()) {
            mapRadar.stopRadarAnimation();
        }
        mHandler.removeCallbacks(scanOrdersTask);
    }

    private void startOrderScan() {
        if (mapRadar == null || !mapRadar.isAnimationRunning()) {
            mapRadar = new MapRadar(mMap, new LatLng(locationManager.getLat(), locationManager.getLng()), getApplicationContext());
            mapRadar.withDistance(2000);
            mapRadar.withOuterCircleStrokeColor(0xfccd29);
            mapRadar.startRadarAnimation();
            mapRadar.startRadarAnimation();
        }
        scanOrdersTask = () -> {
            scanOrders(new View(this));
            mHandler.postDelayed(scanOrdersTask, 2000);
        };
        mHandler.postDelayed(scanOrdersTask, 2000);
    }

    public void scanOrders(View view) {
        ArrayList<ClientPendingOrderItem> pendingOrderItems = new ArrayList<>();

        try {
            pendingOrdersRef.get().addOnCompleteListener(refTask -> {
                if (refTask.isSuccessful()) {
                    for(DataSnapshot orderSnapshot : refTask.getResult().getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);

                        if (order.getOrderStatus() == Order.eOrderStatus.ORDER_SUBMITTED) {
                            marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(order.getDropOffLatLng().getLatitude(), order.getDropOffLatLng().getLongitude())));
                            ClientPendingOrderItem clientPendingOrderItem = new ClientPendingOrderItem(order, marker);

                            pendingOrderItems.add(clientPendingOrderItem);
                        }
                    }

                    boolean emptyPendingOrders = pendingOrderItems.size() == 0;

                    rvPendingOrders.setVisibility(emptyPendingOrders ? View.INVISIBLE : View.VISIBLE);
                    noPendingOrders.setVisibility(emptyPendingOrders ? View.VISIBLE : View.INVISIBLE);
                    adapter = new PendingOrdersAdapter(pendingOrderItems);
                    adapter.setOnItemClickListener(position -> showPendingOrderOnMap(pendingOrderItems.get(position)));
                    rvPendingOrders.setAdapter(adapter);
                    rvPendingOrders.setLayoutManager(new LinearLayoutManager(this));
                }
            });
        } catch (Exception e){
            Toast.makeText(ctx, "Error Message : "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showPendingOrderOnMap(ClientPendingOrderItem clientPendingOrderItem) {
        Marker orderMarker = clientPendingOrderItem.getMarker();
        Order pendingOrder = clientPendingOrderItem.getOrder();

        this.pendingOrder = pendingOrder;
        zoomToOrderMarker(orderMarker);
        pendingOrdersCardBehavior.setHideable(true);
        pendingOrdersCardBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        pendingOrderDetailsCardBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        OrderDetailsFragment orderDetailsFragment = new OrderDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("order", pendingOrder);
        bundle.putBoolean("client", false);
        bundle.putBoolean("activeOrder", false);
        orderDetailsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.pending_order_view, orderDetailsFragment).addToBackStack(null).commit();

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

    public void TakeOrder(View view) {
        String orderNumber = pendingOrder.getOrderNumber();

        removeFromPendingOrders(orderNumber);
        acceptOrder(pendingOrder);
    }

    private void removeFromPendingOrders(String orderNumber) {
        pendingOrdersRef.child(orderNumber).removeValue();
    }

    private void acceptOrder(Order order) {
        DatabaseReference pendingOrdersRef = firebaseDatabase.getReference("clients")
                .child(order.getIssuedClientID())
                .child("pending_orders")
                .child(order.getOrderNumber())
                .child("orderStatus");

        DatabaseReference riderActiveOrdersRef = firebaseDatabase.getReference("riders")
                .child(riderID)
                .child("active_orders")
                .child(order.getOrderNumber());

        order.setOrderStatus(Order.eOrderStatus.ORDER_CONFIRMED);
        pendingOrdersRef.setValue(Order.eOrderStatus.ORDER_CONFIRMED);
        riderActiveOrdersRef.setValue(order);
        closePendingOrderDetails(new View(this));
    }

    private void zoomToOrderMarker(Marker orderMarker) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(orderMarker.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    public void getRiderStatus(){
        showDialog();
        try {
            DatabaseReference databaseRef = firebaseDatabase.getReference("riders")
                    .child(riderID)
                    .child("status");
            databaseRef.get().addOnCompleteListener(refTask -> {
                if (refTask.isSuccessful()) {
                    String riderStatus = (String) refTask.getResult().getValue();

                    if (riderStatus != null && riderStatus.equals("Active")) {
                        checkIfRiderWorkingTime();
                    }
                }
            });
        } catch (Exception e){
            Toast.makeText(ctx, "Error Message : "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
        hideDialog();
    }

    private void checkIfRiderWorkingTime() {
        DateFormat formatter = new SimpleDateFormat("EEEE");
        Date now = new Date();
        int hourNow = now.getHours();
        String dayOfWeek = formatter.format(now);

        checkIfRiderWorkingDay(hourNow, dayOfWeek);
    }

    private void checkIfRiderWorkingDay(int hourNow, String dayOfWeek) {
        DatabaseReference workingDaysRef = firebaseDatabase
                .getReference("riders")
                .child(riderID)
                .child("working_days");

        workingDaysRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot workingDaysSnapShot : task.getResult().getChildren()) {
                    workingDaysSnapShot.getRef().child("value").get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            if (dayOfWeek.equals(task1.getResult().getValue())) {
                                checkIfRiderWorkingHour(hourNow);
                            }
                        }
                    });
                }
            }
            else {
                Toast.makeText(RiderMapActivity.this, "Failed to load working days", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void closePendingOrderDetails(View view) {
        pendingOrdersCardBehavior.setHideable(false);
        pendingOrderDetailsCardBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        pendingOrdersCardBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void checkIfRiderWorkingHour(int hourNow) {
        DatabaseReference workingHoursRef = firebaseDatabase
                .getReference("riders")
                .child(riderID)
                .child("working_hours");

        workingHoursRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot workingHoursSnapShot : task.getResult().getChildren()) {
                    workingHoursSnapShot.getRef().child("value").get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            String hoursRange = (String) task1.getResult().getValue();
                            int hyphenPos = hoursRange.indexOf('-');
                            int startHour = Integer.parseInt(hoursRange.substring(0, hyphenPos));
                            int endHour = Integer.parseInt(hoursRange.substring(hyphenPos + 1));

                            if (hourNow >= startHour && hourNow <= endHour) {
                                changeRiderStatus("Active");
                            }
                        }
                    });
                }
            }
            else {
                Toast.makeText(RiderMapActivity.this, "Failed to load working days", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeRiderStatus(String status) {
        try {
            showDialog();
            DatabaseReference databaseRef = firebaseDatabase.getReference("riders").child(riderID).child("status");
            databaseRef.setValue(status);

            swStatus.setChecked(status.equals("Active"));
        } catch (Exception ex) {
            Toast.makeText(ctx, "Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        hideDialog();
    }

    private void initializeLocationManager() {
        locationManager = new SmartSendLocationManager(this, 60000, 10);
        locationManager.setLocationManagerAndListener();
    }

    public void zoomOrderMark(View view) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //AppController.requestPremissions(this);
            ActivityCompat.requestPermissions(RiderMapActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        }
        else {
            mMap.setMyLocationEnabled(true);
            initMap();
        }
    }

    private void initMap() {
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setPadding(0, 0, 0, 200);
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
        mMap.setMapStyle(style);

        currentLocation = new LatLng(locationManager.getLat(), locationManager.getLng());
        Log.d(TAG, "Lat = " + currentLocation.latitude + " Lng = " + currentLocation.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission is necessary to app", Toast.LENGTH_SHORT)
                        .show();
                ActivityCompat.requestPermissions(RiderMapActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION,  Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

            }
            else {
                mMap.setMyLocationEnabled(true);
                initMap();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapRadar != null && mapRadar.isAnimationRunning()) {
            mapRadar.stopRadarAnimation();
            unregisterReceiver(receiver);
        }
    }

    public void openMenu(View view) {
        drawer.open();
    }


    class PendingOrdersReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("GET_PENDING_ORDER"))
            {

                Toast.makeText(ctx, "Order number: " + intent.getStringExtra("PENDING_ORDER_NUMBER"), Toast.LENGTH_LONG).show();
            }
        }

    }
}
