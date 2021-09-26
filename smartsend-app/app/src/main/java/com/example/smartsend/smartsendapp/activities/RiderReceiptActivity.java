package com.example.smartsend.smartsendapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.app.order.price.Fare;

import java.util.Map;

public class RiderReceiptActivity extends AppCompatActivity {
    private LinearLayout paymentLabels;
    private LinearLayout paymentPrices;
    private TextView tvSubTotal;
    private TextView tvTotalPrice;
    private TextView tvDeliveryPrice;
    private Fare orderFare;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_receipt);

        getOrderFare();
        initializeActivityComponents();
    }

    private void getOrderFare() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            orderFare = (Fare) extras.getSerializable("orderFare");
        }
    }

    private void initializeActivityComponents() {
        double orderTotalPrice = orderFare.getPrice();

        paymentLabels = findViewById(R.id.paymentLabels);
        paymentPrices = findViewById(R.id.paymentPrices);
        tvSubTotal = findViewById(R.id.tvSubTotal);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvDeliveryPrice = findViewById(R.id.tvDeliveryPrice);

        for (Map.Entry<String,Double> fareDetail : orderFare.getDetails().entrySet()) {
            TextView tvFareDetail = (TextView) View.inflate(this, R.layout.layout_receipt_text, null);
            TextView tvFarePrice = (TextView) View.inflate(this, R.layout.layout_receipt_text, null);

            tvFareDetail.setText(fareDetail.getKey());
            tvFarePrice.setText(String.valueOf(fareDetail.getValue()));
            paymentLabels.addView(tvFareDetail);
            paymentPrices.addView(tvFarePrice);
        }

        tvSubTotal.setText(String.valueOf(orderTotalPrice - 5));
        tvTotalPrice.setText(String.valueOf(orderTotalPrice));
        tvDeliveryPrice.setText(String.valueOf(orderTotalPrice));
    }

    public void closeActivity(View view) {
        finish();
    }
}
