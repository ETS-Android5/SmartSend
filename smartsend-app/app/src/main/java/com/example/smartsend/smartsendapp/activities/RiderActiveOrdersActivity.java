package com.example.smartsend.smartsendapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.adapters.HistoryAdapter;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.example.smartsend.smartsendapp.utilities.app.ClientHistoryItem;
import com.example.smartsend.smartsendapp.utilities.app.order.Order;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RiderActiveOrdersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private BottomSheetBehavior<RelativeLayout> orderHistoryBehavior;
    private RelativeLayout orderHistoryCard;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseManager firebaseManager;
    private Order selectedOrder;
    private FirebaseDatabase firebaseDatabase;
    private String riderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_order_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Active Orders");

        firebaseManager = FirebaseManager.getInstance();
        firebaseDatabase = firebaseManager.getFirebaseDatabase();

        orderHistoryCard = findViewById(R.id.OrderHistoryCard);
        orderHistoryBehavior = BottomSheetBehavior.from(orderHistoryCard);
        orderHistoryBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        getRiderID();
        displayRiderActiveOrders();
    }

    private void getRiderID() {
        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            riderID = extra.getString("riderID");
        }
    }

    private void displayRiderActiveOrders() {
        ArrayList<ClientHistoryItem> activeOrders = new ArrayList<>();
        DatabaseReference activeOrdersRef = firebaseDatabase
                .getReference("riders")
                .child(riderID)
                .child("active_orders");

        activeOrdersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot orderSnapshot : task.getResult().getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    ClientHistoryItem activeOrder = new ClientHistoryItem(order);

                    activeOrders.add(activeOrder);
                }
                initializeRecyclerView(activeOrders);
            }
            else {
                Toast.makeText(this, "Error loading active orders, please try again.", Toast.LENGTH_SHORT);
            }
        });
    }

    private void initializeRecyclerView(ArrayList<ClientHistoryItem> clientHistoryItems) {
        recyclerView = findViewById(R.id.clientHistoryRecycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new HistoryAdapter(clientHistoryItems);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
                ClientHistoryItem historyItem = clientHistoryItems.get(position);

                displayHistoryItem(historyItem);
                orderHistoryBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                selectedOrder = historyItem.getOrder();
        });
    }

    private void displayHistoryItem(ClientHistoryItem activeOrderItem) {
        Order activeOrder = activeOrderItem.getOrder();
        TextView tvOrderID = findViewById(R.id.tvOrderID);

        tvOrderID.setText(activeOrder.getOrderNumber());
    }


    public void closeActivity(View view) {
        finish();
    }

    public void closeHistoryDetails(View view) {
        orderHistoryBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void showActiveOrderOnMap(View view) {
        Intent intent = new Intent(this,
                ActiveOrderMapActivity.class);

        intent.putExtra("Order", selectedOrder);
        intent.putExtra("riderID", riderID);
        startActivity(intent);
        finish();
    }
}
