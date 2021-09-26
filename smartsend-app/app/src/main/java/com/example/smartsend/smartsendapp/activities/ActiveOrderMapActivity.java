package com.example.smartsend.smartsendapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.adapters.SignaturesAdapter;
import com.example.smartsend.smartsendapp.fragments.SignaturePadFragment;
import com.example.smartsend.smartsendapp.interfaces.OnSignaturePadSignedListener;
import com.example.smartsend.smartsendapp.utilities.AppController;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.example.smartsend.smartsendapp.utilities.UserLocalStore;
import com.example.smartsend.smartsendapp.utilities.app.order.Order;
import com.example.smartsend.smartsendapp.utilities.app.order.signature.SerializableBitmap;
import com.example.smartsend.smartsendapp.utilities.app.order.signature.SignatureItem;
import com.example.smartsend.smartsendapp.utilities.location.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.smartsend.smartsendapp.utilities.app.order.Order.eOrderStatus.ORDER_COMPLETED;
import static com.example.smartsend.smartsendapp.utilities.app.order.Order.eOrderStatus.ORDER_DROP_OFF;
import static com.example.smartsend.smartsendapp.utilities.app.order.Order.eOrderStatus.ORDER_PICK_UP;

public class ActiveOrderMapActivity extends AppCompatActivity implements OnMapReadyCallback, OnSignaturePadSignedListener {
    private static final int REQUEST_CALL_PHONE_PERMISSION = 100;
    private GoogleMap mMap;
    private Order order;
    private String phoneNumber;
    private TextView tvContactName;
    private TextView tvAddress;
    private TextView tvPrice;
    private LatLng addressLatLng;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private String riderID;
    private SupportMapFragment mapFragment;
    private BottomSheetBehavior<RelativeLayout> signaturePadBehavior;
    private RelativeLayout signaturePadCard;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SignaturesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_order_map);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Bundle extra = getIntent().getExtras();

        firebaseDatabase = FirebaseManager.getInstance().getFirebaseDatabase();
        firebaseStorage = FirebaseManager.getInstance().getFirebaseStorage();
        riderID = UserLocalStore.getInstance(this).getLoggedInRider().getId();
        getActiveOrder(extra);
        initializeActivityComponents();
    }

    private void initializeActivityComponents() {
        tvContactName = findViewById(R.id.tvContactName);
        tvAddress = findViewById(R.id.tvAddress);
        tvPrice = findViewById(R.id.tvPrice);
        recyclerView = findViewById(R.id.signatures);
        recyclerView.setHasFixedSize(true);
        signaturePadCard = findViewById(R.id.signaturePad);
        signaturePadBehavior = BottomSheetBehavior.from(signaturePadCard);
        signaturePadBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        signaturePadBehavior.setDraggable(false);

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
        refreshSignatures();
    }

    private void showReceipt() {
        Intent intent = new Intent(this, RiderReceiptActivity.class);

        intent.putExtra("orderFare", order.getFareDetails());
        startActivity(intent);
        finish();
    }

    private void showDropOffDetails() {
        tvContactName.setText(order.getDropOffContactInfo().getName());
        tvAddress.setText(order.getDropOffAddress().getAddress());
        tvPrice.setText("255.17");
        addressLatLng = order.getDropOffLatLng();
        phoneNumber = order.getDropOffContactInfo().getPhoneNumber();
    }

    private void showPickUpDetails() {
        tvContactName.setText(order.getPickUpContactInfo().getName());
        tvAddress.setText(order.getPickUpAddress().getAddress());
        tvPrice.setText("$255.17");
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
            callContact();
        }
    }

    private void callContact() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL_PHONE_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Can't call contact without call phone permissions", Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                callContact();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void naviagteToAddress(View view) {
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", addressLatLng.getLatitude(), addressLatLng.getLongitude());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    public void updateOrderStatus(View view) {
        Order.eOrderStatus orderStatus = order.getOrderStatus();

        riderID = UserLocalStore.getInstance(this).getLoggedInRider().getId();
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
        mMap = googleMap;
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

            com.google.android.gms.maps.model.LatLng latLng = new com.google.android.gms.maps.model.LatLng(addressLatLng.getLatitude(), addressLatLng.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    @Override
    public void onSignaturePadSigned(boolean isPadEmpty, Bitmap signature) {
        signaturePadBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        if (!isPadEmpty) {
            SignatureItem newSignature = new SignatureItem(new SerializableBitmap(signature), getSignatureDescription());

            riderID = UserLocalStore.getInstance(this).getLoggedInRider().getId();
            addSignatureToOrder(newSignature);
        }
    }

    private void addSignatureToOrder(SignatureItem newSignature) {
        StorageReference signatureStorageRef = firebaseStorage
                .getReference(order.getOrderNumber())
                .child("signatures")
                .child(String.valueOf(new Date().getTime()));

        Bitmap bitmap = newSignature.getSignature().getCurrentImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = signatureStorageRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> Toast.makeText(this, "Error uploading signature, please try again.", Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(taskSnapshot -> {
                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setContentType("image/jpg")
                            .setCustomMetadata("description", newSignature.getSignatureDescription())
                            .build();

                    signatureStorageRef.updateMetadata(metadata)
                            .addOnSuccessListener(storageMetadata -> {
                                Toast.makeText(this, "Signature Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                                refreshSignatures();
                            })
                            .addOnFailureListener(exception -> {
                                Toast.makeText(this, "Error uploading signature, please try again.", Toast.LENGTH_SHORT).show();
                            });
                });
    }

    private void refreshSignatures() {
        ArrayList<SignatureItem> signatureItems = new ArrayList<>();
        StorageReference signatureStorageRef = firebaseStorage
                .getReference(order.getOrderNumber())
                .child("signatures");

        signatureStorageRef.listAll().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<StorageReference> signatures = task.getResult().getItems();
                for (StorageReference signatureRef : signatures) {
                    final long ONE_MEGABYTE = 1024 * 1024;
                    signatureRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> signatureRef.getMetadata().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            SignatureItem signatureItem = new SignatureItem(new SerializableBitmap(bitmap), signatureRef.getName());

                            signatureItems.add(signatureItem);
                            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                            adapter = new SignaturesAdapter(signatureItems);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);

                            adapter.setOnSignatureDeleteListener(position -> {
                                deleteSignatureFromOrder(position);
                                refreshSignatures();
                            });
                        }
                    })).addOnFailureListener(exception -> {
                        Toast.makeText(this, "Error downloading signature, please try again.", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void deleteSignatureFromOrder(int position) {
        StorageReference signatureStorageRef = firebaseStorage
                .getReference(order.getOrderNumber())
                .child("signatures");

        signatureStorageRef.listAll().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<StorageReference> signaturesRef = task.getResult().getItems();

                if (position < signaturesRef.size()) {
                    StorageReference deleteSignatureRef = signaturesRef.get(position);

                    deleteSignatureRef.delete().addOnSuccessListener(aVoid -> refreshSignatures())
                            .addOnFailureListener(exception -> {
                                Log.d(AppController.TAG, exception.getMessage());});
                }
            }
        });
    }

    private String getSignatureDescription() {
        Order.eOrderStatus orderStatus = order.getOrderStatus();

        switch (orderStatus) {
            case ORDER_CONFIRMED: {
                return "Pick Up Signature(s)";
            }
            case ORDER_PICK_UP: {
                return "Drop Off Signature(s)";
            }
        }
        return "ERROR";
    }

    public void openSignaturePad(View view) {
        SignaturePadFragment signaturePadFragment = new SignaturePadFragment(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.signaturePad, signaturePadFragment).addToBackStack(null).commit();
        signaturePadBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
