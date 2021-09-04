package com.example.smartsend.smartsendapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.ClientHistoryItem;
import com.example.smartsend.smartsendapp.utilities.HistoryAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

public class ClientOrderHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private BottomSheetBehavior<RelativeLayout> orderHistoryBehavior;
    private RelativeLayout orderHistoryCard;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_order_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Orders");

        ArrayList<ClientHistoryItem> clientHistoryItems = new ArrayList<>();
        clientHistoryItems.add(new ClientHistoryItem("address 1", "timestamp 1", "address 2", "timestamp 2", 1));
        clientHistoryItems.add(new ClientHistoryItem("address 1", "timestamp 1", "address 2", "timestamp 2", 2));
        clientHistoryItems.add(new ClientHistoryItem("address 1", "timestamp 1", "address 2", "timestamp 2", 3));
        clientHistoryItems.add(new ClientHistoryItem("address 1", "timestamp 1", "address 2", "timestamp 2", 4));
        clientHistoryItems.add(new ClientHistoryItem("address 1", "timestamp 1", "address 2", "timestamp 2", 5));
        clientHistoryItems.add(new ClientHistoryItem("address 1", "timestamp 1", "address 2", "timestamp 2", 6));
        clientHistoryItems.add(new ClientHistoryItem("address 1", "timestamp 1", "address 2", "timestamp 2", 7));
        clientHistoryItems.add(new ClientHistoryItem("address 1", "timestamp 1", "address 2", "timestamp 2", 8));
        clientHistoryItems.add(new ClientHistoryItem("address 1", "timestamp 1", "address 2", "timestamp 2", 9));
        clientHistoryItems.add(new ClientHistoryItem("address 1", "timestamp 1", "address 2", "timestamp 2", 10));
        clientHistoryItems.add(new ClientHistoryItem("address 1", "timestamp 1", "address 2", "timestamp 2", 1));
        clientHistoryItems.add(new ClientHistoryItem("address 1", "timestamp 1", "address 2", "timestamp 2", 12));

        initializeRecyclerView(clientHistoryItems);

        orderHistoryCard = findViewById(R.id.OrderHistoryCard);
        orderHistoryBehavior = BottomSheetBehavior.from(orderHistoryCard);
        orderHistoryBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
            orderHistoryBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
    }

    private void displayHistoryItem(ClientHistoryItem historyItem) {
        TextView tvOrderID = findViewById(R.id.tvOrderID);

        tvOrderID.setText(Integer.toString(historyItem.getOrderNumber()));
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
