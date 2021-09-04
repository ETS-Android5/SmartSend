package com.example.smartsend.smartsendapp.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.app.order.ContactInfo;
import com.example.smartsend.smartsendapp.utilities.app.order.Order;
import com.example.smartsend.smartsendapp.utilities.location.SmartSendLocationManager;
import com.example.smartsend.smartsendapp.utilities.app.order.OrderAddressDetails;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.time.Month;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.smartsend.smartsendapp.utilities.AppController.TAG;

public class ClientMapActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private GoogleMap mMap;
    private Marker marker;

    private ImageButton btnChangeCardView;
    private LinearLayout CardView;
    private LinearLayout btnSmallDelivery;
    private LinearLayout btnMediumDelivery;
    private LinearLayout btnLargeDelivery;
    private LinearLayout AdditionalDetailsCard;
    private LinearLayout PickUpContactCard;
    private LinearLayout DropOffContactCard;
    private LinearLayout pickUpDetailsBtn;
    private LinearLayout dropOffDetailsBtn;
    private AutocompleteSupportFragment autocompleteFragment;
    private TextInputEditText tiPickUpAddress, tiDropOffAddress;
    private BottomSheetBehavior<LinearLayout> mainCardBehavior, addtionalDetailsBehavior;
    private BottomSheetBehavior<LinearLayout> pickUpContactBehavior, dropOffContactBehavior, courierNoteBehavior;
    private SmartSendLocationManager locationManager;
    private TextView tvAdditionalDetails;
    private ImageButton btnOrderLater;
    private LinearLayout CourierNoteCard;
    private LinearLayout courierNoteBtn;
    private TextView tvPickUpEntrance;
    private TextView tvPickUpApartment;
    private TextView tvPickUpFloor;
    private TextView tvDropOffEntrance;
    private TextView tvDropOffApartment;
    private TextView tvDropOffFloor;

    private Order.eOrderSize orderSize = Order.eOrderSize.SMALL_PACKAGE;
    private OrderAddressDetails pickUpAddressInfo = new OrderAddressDetails();
    private OrderAddressDetails dropOffAddressInfo = new OrderAddressDetails();
    private ContactInfo pickUpContactInfo = null, dropOffContactInfo = null;
    private String courierNote = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Places.initialize(getApplicationContext(), "AIzaSyBUiecg0U9MpA9SNXI-UoPSUpvZV8tXYTg");
        initializeActivityComponents();
    }

    private void initializeActivityComponents() {
        btnChangeCardView = findViewById(R.id.btnChangeCardView);
        CardView = findViewById(R.id.CardViewSheet);
        btnSmallDelivery = findViewById(R.id.btnSmallDelivery);
        btnMediumDelivery = findViewById(R.id.btnMediumDelivery);
        btnLargeDelivery = findViewById(R.id.btnLargeDelivery);
        tiPickUpAddress = findViewById(R.id.tiPickUpAddress);
        tiDropOffAddress = findViewById(R.id.tiDeliverToAddress);
        tvAdditionalDetails = findViewById(R.id.tvAdditionalDetails);
        AdditionalDetailsCard = findViewById(R.id.AdditionalDetailsCard);
        PickUpContactCard = findViewById(R.id.PickUpContactCard);
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

        initalizeAdditionalDetailsBtn();
        initializeOrderLaterBtn();
        initalizeAddressInput();
        initializeLocationManager();
        initializeDeliveryBtn();
        initializeBottomSheetBehavior();
        initializeAutoCompleteFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeOrderLaterBtn() {
        btnOrderLater.setOnClickListener(v -> {
            Date now = new Date();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                ClientMapActivity.this,
                android.R.style.Theme_Holo_Dialog_MinWidth,
                    (datePicker, year, month, day) -> {
                        Toast.makeText(ClientMapActivity.this, day + " " + Month.of(month + 1) + ", " + year, Toast.LENGTH_SHORT).show();

                        TimePickerDialog timePickerDialog = new TimePickerDialog(
                                ClientMapActivity.this,
                                android.R.style.Theme_Holo_Dialog_MinWidth,
                                (timePicker, hour, minute) -> {
                                    Toast.makeText(ClientMapActivity.this, hour + ":" + minute, Toast.LENGTH_SHORT).show();


                                }, now.getHours(), now.getMinutes(), true);
                        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        timePickerDialog.show();
                    }, 1900 + now.getYear(), now.getMonth(), now.getDate());

            datePickerDialog.getDatePicker().setMinDate(now.getTime());
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });
    }

    private void initializeAutoCompleteFragment() {
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                updateMap(place);
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void updateMap(@NonNull Place place) {
        LatLng currentLocation = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
        marker = mMap.addMarker(new MarkerOptions().position(currentLocation).title(place.getAddress()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private void initalizeAdditionalDetailsBtn() {
        addtionalDetailsBehavior = BottomSheetBehavior.from(AdditionalDetailsCard);
        addtionalDetailsBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        pickUpContactBehavior = BottomSheetBehavior.from(PickUpContactCard);
        pickUpContactBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        dropOffContactBehavior = BottomSheetBehavior.from(DropOffContactCard);
        dropOffContactBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        courierNoteBehavior = BottomSheetBehavior.from(CourierNoteCard);
        courierNoteBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        tvAdditionalDetails.setOnClickListener(v -> addtionalDetailsBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
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
                addtionalDetailsBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                pickUpContactBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                dropOffContactBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                courierNoteBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.AdditionalDetailsSaveBtn:
                addtionalDetailsBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                saveAdditionalDetails();
                break;
            case R.id.PickUpSaveBtn:
                pickUpContactBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                savePickUpContactInfo();
                break;
            case R.id.DropOffSaveBtn:
                dropOffContactBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                saveDropOffContactInfo();
                break;
            case R.id.CourierNoteSaveBtn:
                courierNoteBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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

    private void initalizeAddressInput() {
        tiPickUpAddress.setFocusable(false);
        tiPickUpAddress.setOnClickListener(v -> {
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(ClientMapActivity.this);
            startActivityForResult(intent, 100);
        });
        tiDropOffAddress.setFocusable(false);
        tiDropOffAddress.setOnClickListener(v -> {
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(ClientMapActivity.this);
            startActivityForResult(intent, 100);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);

            updateMap(place);
            if (tiPickUpAddress.getText().toString().isEmpty()) {
                tiPickUpAddress.setText(place.getAddress());
            }
            else {
                tiDropOffAddress.setText(place.getAddress());
            }
        }
        else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(data);

            Toast.makeText(getApplicationContext(), status.getStatusMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeLocationManager() {
        locationManager = new SmartSendLocationManager(this, 60000, 10);
        locationManager.setLocationManagerAndListener();
    }

    private void initializeBottomSheetBehavior() {
        mainCardBehavior = BottomSheetBehavior.from(CardView);
        mainCardBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        mainCardBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        btnChangeCardView.setImageResource(R.drawable.ic_arrow_up_black);
                       break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        btnChangeCardView.setImageResource(R.drawable.ic_arrow_down_black);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(latlng -> {
            if (marker != null) {
                marker.remove();
            }
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                Address address = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1).get(0);
                Place place = Place.builder().setAddress(address.getAddressLine(0)).setLatLng(latlng).build();

                autocompleteFragment.setText(address.getAddressLine(0));
                updateMap(place);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        LatLng currentLocation = new LatLng(locationManager.getLat(), locationManager.getLng());
        marker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    public void closeActivity(View view) {
        finish();
    }

    public void makeOrder(View view) {
        pickUpAddressInfo.setAddress(tiPickUpAddress.getText().toString());
        dropOffAddressInfo.setAddress(tiDropOffAddress.getText().toString());

        Order order = new Order(orderSize, pickUpContactInfo, dropOffContactInfo,
                pickUpAddressInfo, dropOffAddressInfo, courierNote);

        // validate order
        goToAcceptedOrderActivity();
    }

    private void goToAcceptedOrderActivity() {
        Intent intent = new Intent(this,
                OrderDetailsAcceptedActivity.class);
        startActivity(intent);
        finish();
    }
}
