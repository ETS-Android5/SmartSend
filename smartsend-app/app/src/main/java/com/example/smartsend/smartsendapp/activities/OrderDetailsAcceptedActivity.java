package com.example.smartsend.smartsendapp.activities;

import android.app.NotificationManager;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartsend.smartsendapp.R;


public class OrderDetailsAcceptedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_accepted);
    }

    public void closeActivity(View view) {
        finish();
    }

    public void goToTrackOrderActivity(View view) {
    }
}
