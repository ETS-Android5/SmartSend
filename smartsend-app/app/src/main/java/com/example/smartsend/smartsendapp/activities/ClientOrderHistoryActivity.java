package com.example.smartsend.smartsendapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.example.smartsend.smartsendapp.utilities.app.OrderItem;
import com.example.smartsend.smartsendapp.adapters.HistoryAdapter;
import com.example.smartsend.smartsendapp.utilities.app.order.Order;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ClientOrderHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private BottomSheetBehavior<RelativeLayout> orderHistoryBehavior;
    private RelativeLayout orderHistoryCard;
    private RecyclerView.LayoutManager layoutManager;
    private String clientID;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_order_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Orders");
        firebaseDatabase = FirebaseManager.getInstance().getFirebaseDatabase();

        getClientID();
        getClientOrderHistory();
        orderHistoryCard = findViewById(R.id.OrderHistoryCard);
        orderHistoryBehavior = BottomSheetBehavior.from(orderHistoryCard);
        orderHistoryBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void getClientID() {
        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            clientID = extra.getString("clientID");
        }
    }

    private void getClientOrderHistory() {
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        DatabaseReference activeOrdersRef = firebaseDatabase
                .getReference("clients")
                .child(clientID)
                .child("completed_orders");

        activeOrdersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot orderSnapshot : task.getResult().getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    OrderItem activeOrder = new OrderItem(order);

                    orderItems.add(activeOrder);
                }
                initializeRecyclerView(orderItems);
            }
            else {
                Toast.makeText(this, "Error loading active orders, please try again.", Toast.LENGTH_SHORT);
            }
        });
    }

    private void initializeRecyclerView(ArrayList<OrderItem> orderItems) {
        recyclerView = findViewById(R.id.clientHistoryRecycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new HistoryAdapter(orderItems);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
                OrderItem historyItem = orderItems.get(position);

                displayHistoryItem(historyItem);
                orderHistoryBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        });
    }

    private void displayHistoryItem(OrderItem historyItem) {
        Order historyOrder = historyItem.getOrder();
        TextView tvOrderID = findViewById(R.id.tvOrderID);
        TextView tvPickUpAddress = findViewById(R.id.tvPickUpAddress);
        TextView tvDropOffAddress = findViewById(R.id.tvDropOffAddress);
        TextView tvOrderStatus = findViewById(R.id.tvOrderStatus);
        TextView tvPickUpTimestamp = findViewById(R.id.tvPickUpTimestamp);
        TextView tvDeliverTimestamp = findViewById(R.id.tvDeliverTimestamp);

        tvOrderID.setText(historyOrder.getOrderNumber());
        tvPickUpAddress.setText(historyOrder.getPickUpAddress().getAddress());
        tvDropOffAddress.setText(historyOrder.getDropOffAddress().getAddress());
        tvOrderStatus.setText(historyOrder.getOrderStatus().getStatus());
        tvPickUpTimestamp.setText(historyOrder.getPickUpTimestamp() != null ? historyOrder.getPickUpTimestamp() : "Order has not been picked up yet");
        tvDeliverTimestamp.setText(historyOrder.getDropOffTimestamp() != null ? historyOrder.getDropOffTimestamp() : "Order has not been dropped off yet");
    }


    public void closeActivity(View view) {
        finish();
    }

    public void closeHistoryDetails(View view) {
        orderHistoryBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void cancelOrder(View view) {

    }

    public void editOrder(View view) {
    }
}
