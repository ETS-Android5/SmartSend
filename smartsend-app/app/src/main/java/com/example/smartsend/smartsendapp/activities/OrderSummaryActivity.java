package com.example.smartsend.smartsendapp.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.example.smartsend.smartsendapp.utilities.app.order.ContactInfo;
import com.example.smartsend.smartsendapp.utilities.app.order.Order;
import com.example.smartsend.smartsendapp.utilities.app.order.OrderAddressDetails;
import com.example.smartsend.smartsendapp.utilities.location.SmartSendLocationManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderSummaryActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, RoutingListener {
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    protected LatLng pickUpLatLng = null;
    protected LatLng dropOffLatLng = null;
    private List<Polyline> polylines = null;

    private LinearLayout btnSmallDelivery;
    private LinearLayout btnMediumDelivery;
    private LinearLayout btnLargeDelivery;
    private LinearLayout AdditionalDetailsCard;
    private RelativeLayout OrderCard;
    private LinearLayout PickUpContactCard;
    private LinearLayout DropOffContactCard;
    private LinearLayout pickUpDetailsBtn;
    private LinearLayout dropOffDetailsBtn;
    private TextView tvPickUpAddress, tvDropOffAddress;
    private BottomSheetBehavior<LinearLayout> pickUpContactBehavior, additionalDetailsBehavior;
    private BottomSheetBehavior<LinearLayout> dropOffContactBehavior, courierNoteBehavior;
    private BottomSheetBehavior<RelativeLayout> orderCardBehavior;
    private SmartSendLocationManager locationManager;
    private TextView tvAdditionalDetails;
    private ImageView btnOrderLater;
    private LinearLayout CourierNoteCard;
    private LinearLayout courierNoteBtn;
    private TextView tvPickUpEntrance;
    private TextView tvPickUpApartment;
    private TextView tvPickUpFloor;
    private TextView tvDropOffEntrance;
    private TextView tvDropOffApartment;
    private TextView tvDropOffFloor;

    private String clientID;
    private FirebaseManager firebaseManager = FirebaseManager.getInstance();
    private FirebaseDatabase firebaseDatabase = firebaseManager.getFirebaseDatabase();

    private Order order;
    private Order.eOrderSize orderSize = Order.eOrderSize.SMALL_PACKAGE;
    private OrderAddressDetails pickUpAddressInfo = new OrderAddressDetails();
    private OrderAddressDetails dropOffAddressInfo = new OrderAddressDetails();
    private ContactInfo pickUpContactInfo = null, dropOffContactInfo = null;
    private String courierNote = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        clientID = firebaseManager.getCurrentUser().getUid();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initializeActivityComponents();
        getOrderAddress();
        Places.initialize(getApplicationContext(), "AIzaSyBUiecg0U9MpA9SNXI-UoPSUpvZV8tXYTg");
    }

    private void getOrderAddress() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Geocoder geocoder = new Geocoder(this);
            String pickUpAddress = extras.getString("pickUpAddress");
            String dropOffAddress = extras.getString("dropOffAddress");

            try {
                Address pickUpAddr = geocoder.getFromLocationName(pickUpAddress, 1).get(0);
                Address dropOffAddr = geocoder.getFromLocationName(dropOffAddress, 1).get(0);

                tvPickUpAddress.setText(pickUpAddress);
                tvDropOffAddress.setText(dropOffAddress);
                pickUpLatLng = new LatLng(pickUpAddr.getLatitude(), pickUpAddr.getLongitude());
                dropOffLatLng = new LatLng(dropOffAddr.getLatitude(), dropOffAddr.getLongitude());
                findRoutes(pickUpLatLng, dropOffLatLng);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeActivityComponents() {
        btnSmallDelivery = findViewById(R.id.btnSmallDelivery);
        btnMediumDelivery = findViewById(R.id.btnMediumDelivery);
        btnLargeDelivery = findViewById(R.id.btnLargeDelivery);
        tvPickUpAddress = findViewById(R.id.tvPickUpAddress);
        tvDropOffAddress = findViewById(R.id.tvDropOffAddress);
        tvAdditionalDetails = findViewById(R.id.tvAdditionalDetails);
        AdditionalDetailsCard = findViewById(R.id.AdditionalDetailsCard);
        OrderCard = findViewById(R.id.orderCard);
        PickUpContactCard = findViewById(R.id.pickUpContactCard);
        DropOffContactCard = findViewById(R.id.DropOffContactCard);
        pickUpDetailsBtn = findViewById(R.id.pickUpDetailsBtn);
        dropOffDetailsBtn = findViewById(R.id.dropOffDetailsBtn);
        CourierNoteCard = findViewById(R.id.CourierNoteCard);
        courierNoteBtn = findViewById(R.id.courierNoteBtn);
        btnOrderLater = findViewById(R.id.btnOrderLater);
        tvPickUpEntrance = findViewById(R.id.pickUpEntrance);
        tvPickUpApartment = findViewById(R.id.pickUpApartment);
        tvPickUpFloor = findViewById(R.id.pickUpFloor);
        tvDropOffEntrance = findViewById(R.id.dropOffEntrance);
        tvDropOffApartment = findViewById(R.id.dropOffApartment);
        tvDropOffFloor = findViewById(R.id.dropOffFloor);

        initializeAdditionalDetailsBan();
        initializeOrderLaterBtn();
        initializeLocationManager();
        initializeDeliveryBtn();
    }

    private void initializeOrderLaterBtn() {
        btnOrderLater.setOnClickListener(v -> {
            Date now = new Date();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    OrderSummaryActivity.this,
                    android.R.style.Theme_Holo_Dialog_MinWidth,
                    (datePicker, year, month, day) -> {
                        Toast.makeText(OrderSummaryActivity.this, day + " " + Month.of(month + 1) + ", " + year, Toast.LENGTH_SHORT).show();

                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                OrderSummaryActivity.this,
                                android.R.style.Theme_Holo_Dialog_MinWidth,
                                (timePicker, hour, minute) -> {
                                    Toast.makeText(OrderSummaryActivity.this, hour + ":" + minute, Toast.LENGTH_SHORT).show();
                                }, now.getHours(), now.getMinutes(), true);
                        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        timePickerDialog.show();
                    }, 1900 + now.getYear(), now.getMonth(), now.getDate());
            Date tomorrow = new Date(now.getTime() + (1000 * 60 * 60 * 24));

            datePickerDialog.getDatePicker().setMinDate(tomorrow.getTime());
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });
    }


    private void initializeAdditionalDetailsBan() {
        orderCardBehavior = BottomSheetBehavior.from(OrderCard);
        orderCardBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        additionalDetailsBehavior = BottomSheetBehavior.from(AdditionalDetailsCard);
        additionalDetailsBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        pickUpContactBehavior = BottomSheetBehavior.from(PickUpContactCard);
        pickUpContactBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        dropOffContactBehavior = BottomSheetBehavior.from(DropOffContactCard);
        dropOffContactBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        courierNoteBehavior = BottomSheetBehavior.from(CourierNoteCard);
        courierNoteBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        tvAdditionalDetails.setOnClickListener(v -> additionalDetailsBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        pickUpDetailsBtn.setOnClickListener(v -> pickUpContactBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        dropOffDetailsBtn.setOnClickListener(v -> dropOffContactBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        courierNoteBtn.setOnClickListener(v -> courierNoteBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
    }

    public void closeCard(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.AdditionalDetailsBackBtn:
            case R.id.PickUpBackBtn:
            case R.id.DropOffBackBtn:
            case R.id.CourierNoteBackBtn:
                additionalDetailsBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                pickUpContactBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                dropOffContactBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                courierNoteBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.AdditionalDetailsSaveBtn:
                additionalDetailsBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                saveAdditionalDetails();
                break;
            case R.id.PickUpSaveBtn:
                pickUpContactBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                savePickUpContactInfo();
                break;
            case R.id.DropOffSaveBtn:
                dropOffContactBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                saveDropOffContactInfo();
                break;
            case R.id.CourierNoteSaveBtn:
                courierNoteBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                TextView tvCourierNote = findViewById(R.id.tiCourierNote);
                courierNote = tvCourierNote.getText().toString();
                break;
        }
    }

    private void saveDropOffContactInfo() {
        TextView tvContactName = findViewById(R.id.tvDropOffContactName);
        TextView tvContactPhoneNumber = findViewById(R.id.tvDropOffContactPhoneNumber);

        dropOffContactInfo = new ContactInfo(tvContactName.getText().toString(),
                tvContactPhoneNumber.getText().toString());
    }

    private void savePickUpContactInfo() {
        TextView tvContactName = findViewById(R.id.tvPickUpContactName);
        TextView tvContactPhoneNumber = findViewById(R.id.tvPickUpContactPhoneNumber);

        pickUpContactInfo = new ContactInfo(tvContactName.getText().toString(),
                tvContactPhoneNumber.getText().toString());
    }

    private void saveAdditionalDetails() {
        pickUpAddressInfo.setEntrance(tvPickUpEntrance.getText().toString());
        pickUpAddressInfo.setApartmentNumber(Integer.parseInt(tvPickUpApartment.getText().toString()));
        pickUpAddressInfo.setFloor(Integer.parseInt(tvPickUpFloor.getText().toString()));

        dropOffAddressInfo.setEntrance(tvDropOffEntrance.getText().toString());
        dropOffAddressInfo.setApartmentNumber(Integer.parseInt(tvDropOffApartment.getText().toString()));
        dropOffAddressInfo.setFloor(Integer.parseInt(tvDropOffFloor.getText().toString()));
    }

    private void initializeLocationManager() {
        locationManager = new SmartSendLocationManager(this, 60000, 10);
        locationManager.setLocationManagerAndListener();
    }

    private void initializeDeliveryBtn() {
        btnSmallDelivery.setOnClickListener(v -> changeBtnElevations(Order.eOrderSize.SMALL_PACKAGE));
        btnMediumDelivery.setOnClickListener(v -> changeBtnElevations(Order.eOrderSize.MEDIUM_PACKAGE));
        btnLargeDelivery.setOnClickListener(v -> changeBtnElevations(Order.eOrderSize.LARGE_PACKAGE));
    }

    private void changeBtnElevations(Order.eOrderSize size) {
        int elevationVal = 25;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            btnSmallDelivery.setElevation(size == Order.eOrderSize.SMALL_PACKAGE ? elevationVal : 0);
            btnMediumDelivery.setElevation(size == Order.eOrderSize.MEDIUM_PACKAGE ? elevationVal : 0);
            btnLargeDelivery.setElevation(size == Order.eOrderSize.LARGE_PACKAGE ? elevationVal : 0);
        }

        orderSize = size;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setPadding(0, 0, 0, 450);
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style);
        mMap.setMapStyle(style);
    }

    public void closeActivity(View view) {
        finish();
    }

    public void makeOrder(View view) {
        pickUpAddressInfo.setAddress(tvPickUpAddress.getText().toString());
        dropOffAddressInfo.setAddress(tvDropOffAddress.getText().toString());

        order = new Order(clientID, orderSize,
                new com.example.smartsend.smartsendapp.utilities.location.LatLng(pickUpLatLng.latitude, pickUpLatLng.longitude),
                new com.example.smartsend.smartsendapp.utilities.location.LatLng(dropOffLatLng.latitude, dropOffLatLng.longitude),
                pickUpContactInfo, dropOffContactInfo,
                pickUpAddressInfo, dropOffAddressInfo, courierNote);

        addOrderToClient(order);
        addOrderToPendingOrdersList(order);
        // validate order
        goToSearchCourierActivity();
    }

    private void addOrderToPendingOrdersList(Order order) {
        DatabaseReference ref = firebaseDatabase.getReference("pending_orders")
                .child(String.valueOf(order.getOrderNumber()));

        ref.setValue(order);
    }

    private void addOrderToClient(Order order) {
        DatabaseReference ref = firebaseDatabase.getReference("clients")
                .child(clientID)
                .child("pending_orders")
                .child(String.valueOf(order.getOrderNumber()));

        ref.setValue(order);
    }

    private void goToSearchCourierActivity() {
        Intent intent = new Intent(this,
                SearchCourierActivity.class);

        intent.putExtra("Order", order);
        startActivity(intent);
        finish();
    }

    private void goToAcceptedOrderActivity() {
        Intent intent = new Intent(this,
                OrderDetailsAcceptedActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        findRoutes(pickUpLatLng, dropOffLatLng);
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(OrderSummaryActivity.this,"Finding Route...",Toast.LENGTH_LONG).show();
    }

    public void findRoutes(com.google.android.gms.maps.model.LatLng Start, com.google.android.gms.maps.model.LatLng End)
    {
        if(Start == null || End == null) {
            Toast.makeText(OrderSummaryActivity.this,"Unable to get location",Toast.LENGTH_LONG).show();
        }
        else
        {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("AIzaSyBUiecg0U9MpA9SNXI-UoPSUpvZV8tXYTg")  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(dropOffLatLng);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        com.google.android.gms.maps.model.LatLng polylineStartLatLng=null;
        com.google.android.gms.maps.model.LatLng polylineEndLatLng=null;

        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {

            if(i == shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.colorPrimaryGreen));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylineStartLatLng = polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng = polyline.getPoints().get(k-1);
                polylines.add(polyline);
            }
        }
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        mMap.addMarker(startMarker);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.snippet("and snippet");
        endMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mMap.addMarker(endMarker);
    }

    @Override
    public void onRoutingCancelled() {
        findRoutes(pickUpLatLng, dropOffLatLng);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(OrderSummaryActivity.this,"Routing Failed",Toast.LENGTH_LONG).show();
        findRoutes(pickUpLatLng, dropOffLatLng);
    }
}
