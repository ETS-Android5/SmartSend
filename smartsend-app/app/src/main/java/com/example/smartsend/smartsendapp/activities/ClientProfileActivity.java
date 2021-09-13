package com.example.smartsend.smartsendapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.example.smartsend.smartsendapp.utilities.app.Client;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ClientProfileActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private String clientID;
    private TextView tvEmailAddress;
    private TextView tvMemberSince;
    private TextView tvPhoneNumber;
    private TextView tvCompanyName;
    private TextView tvPersonName;
    private TextView tvLocation;
    private TextView tvPersonEmailAddress;
    private TextView tvPersonPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);

        firebaseDatabase = FirebaseManager.getInstance().getFirebaseDatabase();
        getClientID();
        initializeActivityComponents();
    }

    private void getClientID() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            clientID = extras.getString("clientID");
        }
    }

    private void initializeActivityComponents() {
        tvLocation = findViewById(R.id.tvLocation);
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvMemberSince = findViewById(R.id.tvMemberSince);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvEmailAddress = findViewById(R.id.tvEmailAddress);
        tvPersonName = findViewById(R.id.tvPersonName);
        tvPersonEmailAddress = findViewById(R.id.tvPersonEmailAddress);
        tvPersonPhoneNumber = findViewById(R.id.tvPersonPhoneNumber);

        displayClientInfo();
    }

    private void displayClientInfo() {
        DatabaseReference riderInfoRef = firebaseDatabase.getReference("clients").child(clientID);

        riderInfoRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Client clientInfo = task.getResult().getValue(Client.class);

                tvLocation.setText(clientInfo.getLocation());
                tvMemberSince.setText(clientInfo.getCreated_date());
                tvPhoneNumber.setText(clientInfo.getContact_number());
                tvEmailAddress.setText(clientInfo.getEmail());
                tvCompanyName.setText(clientInfo.getCompany_name());
                tvPersonName.setText(clientInfo.getContact_person_name());
                tvPersonEmailAddress.setText(clientInfo.getContact_person_email());
                tvPersonPhoneNumber.setText(clientInfo.getContact_person_number());
            }
            else {
                Toast.makeText(this, "Error loading client info, please try again.", Toast.LENGTH_LONG).show();
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
