package com.example.smartsend.smartsendapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.adapters.HistoryAdapter;
import com.example.smartsend.smartsendapp.fragments.OrderDetailsFragment;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.example.smartsend.smartsendapp.utilities.app.OrderItem;
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
        setContentView(R.layout.activity_order_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Active Orders");

        firebaseManager = FirebaseManager.getInstance();
        firebaseDatabase = firebaseManager.getFirebaseDatabase();

        orderHistoryCard = findViewById(R.id.active_order_view);
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
        ArrayList<OrderItem> activeOrders = new ArrayList<>();
        DatabaseReference activeOrdersRef = firebaseDatabase
                .getReference("riders")
                .child(riderID)
                .child("active_orders");

        activeOrdersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot orderSnapshot : task.getResult().getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    OrderItem activeOrder = new OrderItem(order);

                    activeOrders.add(activeOrder);
                }
                initializeRecyclerView(activeOrders);
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

                selectedOrder = historyItem.getOrder();
                displayHistoryItem(selectedOrder);
                orderHistoryBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        });
    }

    private void displayHistoryItem(Order activeOrder) {
        OrderDetailsFragment orderDetailsFragment = new OrderDetailsFragment();
        Bundle bundle = new Bundle();

        bundle.putSerializable("order", activeOrder);
        bundle.putBoolean("client", false);
        bundle.putBoolean("activeOrder", true);
        orderDetailsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.active_order_view, orderDetailsFragment).addToBackStack(null).commit();
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
