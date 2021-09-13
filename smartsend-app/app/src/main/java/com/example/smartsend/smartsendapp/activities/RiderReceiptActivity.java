package com.example.smartsend.smartsendapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartsend.smartsendapp.R;

public class RiderReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rider_receipt);
    }

    public void closeActivity(View view) {
        finish();
    }
}
