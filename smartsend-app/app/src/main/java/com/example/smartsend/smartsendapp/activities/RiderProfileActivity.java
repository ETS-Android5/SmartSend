package com.example.smartsend.smartsendapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.example.smartsend.smartsendapp.utilities.app.Rider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RiderProfileActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private String riderID;
    private TextView tvFullName;
    private TextView tvLastName;
    private TextView tvEmailAddress;
    private TextView tvDOB;
    private TextView tvMemberSince;
    private TextView tvLicenseNumber;
    private TextView tvPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_profile);

        firebaseDatabase = FirebaseManager.getInstance().getFirebaseDatabase();
        getRiderID();
        initializeActivityComponents();
    }

    private void getRiderID() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            riderID = extras.getString("riderID");
        }
    }

    private void initializeActivityComponents() {
        tvFullName = findViewById(R.id.tvFullName);
        tvLastName = findViewById(R.id.tvLastName);
        tvMemberSince = findViewById(R.id.tvMemberSince);
        tvDOB = findViewById(R.id.tvDOB);
        tvLicenseNumber = findViewById(R.id.tvLicenseNumber);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvEmailAddress = findViewById(R.id.tvEmailAddress);

        displayRiderInfo();
    }

    private void displayRiderInfo() {
        DatabaseReference riderInfoRef = firebaseDatabase.getReference("riders").child(riderID);

        riderInfoRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Rider riderInfo = task.getResult().getValue(Rider.class);

                tvFullName.setText(riderInfo.getFirst_name());
                tvLastName.setText(riderInfo.getLast_name());
                tvLicenseNumber.setText(riderInfo.getVehicle_number());
                tvMemberSince.setText(riderInfo.getCreated_date());
                tvPhoneNumber.setText(riderInfo.getContact_number());
                tvEmailAddress.setText(riderInfo.getEmail());
                tvDOB.setText(riderInfo.getBirth_date());
            }
            else {
                Toast.makeText(this, "Error loading rider info, please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void closeActivity(View view) {
        finish();
    }
}
