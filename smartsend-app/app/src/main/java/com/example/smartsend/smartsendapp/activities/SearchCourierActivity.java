package com.example.smartsend.smartsendapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.AppController;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.example.smartsend.smartsendapp.utilities.app.Rider;
import com.example.smartsend.smartsendapp.utilities.app.order.Order;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class SearchCourierActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Order order;
    private GoogleMap mMap;
    private TextView tvPickUpAddress;
    private SupportMapFragment mapFragment;
    private BottomSheetBehavior<RelativeLayout> noRiderFoundBehavior, riderFoundBehavior;
    private RelativeLayout noRiderFoundCard, riderFoundCard;
    private Rider rider;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_riders);

        firebaseDatabase = FirebaseManager.getInstance().getFirebaseDatabase();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            order = (Order)extras.getSerializable("Order");
            initializeActivityComponents();
            searchCourier();
        }
    }

    private GeoQuery geoQuery;
    private int radius = 1;
    private int maxRadius = 100;
    private Boolean riderFound = false;
    private void searchCourier(){
        if (radius > maxRadius) {
            if (!riderFound)
                noRiderFoundBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            return;
        }

        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("available_riders");
        com.example.smartsend.smartsendapp.utilities.location.LatLng pickupLocation = order.getPickUpLatLng();

        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.getLatitude(), pickupLocation.getLongitude()), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!riderFound)
                {
                    DatabaseReference riderFoundRef = firebaseDatabase.getReference("riders").child(key);

                    riderFoundRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            riderFoundRef.child("pickup_offers").child(order.getOrderNumber()).setValue(order.getOrderNumber());
                            riderFound = true;
                            riderFoundBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!riderFound)
                {
                    radius++;
                    Timer timer = new Timer();

                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            searchCourier();
                        }
                    }, 600);
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
            }
        });
    }
    private void initializeActivityComponents() {
        tvPickUpAddress = findViewById(R.id.tvPickUpAddress);
        tvPickUpAddress.setText(order.getPickUpAddress().getAddress());

        initializeNoRiderFoundCard();
    }

    private void initializeNoRiderFoundCard() {
        noRiderFoundCard = findViewById(R.id.noRiderFoundCard);
        noRiderFoundBehavior = BottomSheetBehavior.from(noRiderFoundCard);
        noRiderFoundBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        noRiderFoundBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    noRiderFoundBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        riderFoundCard = findViewById(R.id.RiderFoundCard);
        riderFoundBehavior = BottomSheetBehavior.from(riderFoundCard);
        riderFoundBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        riderFoundBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    riderFoundBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    public void closeActivity(View view) {
        Intent intent = new Intent(this,
                ClientMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setPadding(0, 0, 0, 200);
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
        mMap.setMapStyle(style);

        LatLng orderLatLng = new com.google.android.gms.maps.model.LatLng(order.getPickUpLatLng().getLatitude(), order.getPickUpLatLng().getLongitude());
        Marker marker = mMap.addMarker(new MarkerOptions().position(orderLatLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(orderLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AppController.requestPremissions(this);
        }
        mMap.setMyLocationEnabled(true);
    }
}
