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

public class RiderOrderHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private BottomSheetBehavior<RelativeLayout> orderHistoryBehavior;
    private RelativeLayout orderHistoryCard;
    private RecyclerView.LayoutManager layoutManager;
    private String riderID;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Orders");

        getRiderID();
        firebaseDatabase = FirebaseManager.getInstance().getFirebaseDatabase();
        getClientOrderHistory();
        orderHistoryCard = findViewById(R.id.active_order_view);
        orderHistoryBehavior = BottomSheetBehavior.from(orderHistoryCard);
        orderHistoryBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void getRiderID() {
        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            riderID = extra.getString("riderID");
        }
    }

    private void getClientOrderHistory() {
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        DatabaseReference activeOrdersRef = firebaseDatabase
                .getReference("riders")
                .child(riderID)
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

                displayHistoryItem(historyItem.getOrder());
                orderHistoryBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        });
    }

    private void displayHistoryItem(Order historyOrder) {
        OrderDetailsFragment orderDetailsFragment = new OrderDetailsFragment();
        Bundle bundle = new Bundle();

        bundle.putSerializable("order", historyOrder);
        bundle.putBoolean("client", false);
        orderDetailsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.active_order_view, orderDetailsFragment).addToBackStack(null).commit();
    }


    public void closeActivity(View view) {
        finish();
    }

    public void cancelOrder(View view) {

    }

}
