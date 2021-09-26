package com.example.smartsend.smartsendapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.activities.ActiveOrderMapActivity;
import com.example.smartsend.smartsendapp.utilities.app.order.Order;

public class OrderDetailsFragment extends Fragment {
    private Order order;
    private View orderDetailsFragment;
    private boolean showClientBtn = false;
    private boolean activeOrder = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        orderDetailsFragment = inflater.inflate(R.layout.layout_pending_order_details_fragment, container, false);

        getOrder();
        initializeFragmentComponents();

        return orderDetailsFragment;
    }

    private void initializeFragmentComponents() {
        TextView orderIDLabel = orderDetailsFragment.findViewById(R.id.orderIDLabel);
        Button btnTakeOrder = orderDetailsFragment.findViewById(R.id.btnTakeOrder);
        Button btnGoToOrder = orderDetailsFragment.findViewById(R.id.btnGoToOrder);
        TextView tvOrderID = orderDetailsFragment.findViewById(R.id.tvOrderID);
        TextView tvPickUpAddress = orderDetailsFragment.findViewById(R.id.tvPickUpAddress);
        TextView tvDropOffAddress = orderDetailsFragment.findViewById(R.id.tvDropOffAddress);
        TextView tvOrderStatus = orderDetailsFragment.findViewById(R.id.tvOrderStatus);
        TextView tvPickUpTimestamp = orderDetailsFragment.findViewById(R.id.tvPickUpTimestamp);
        TextView tvDeliverTimestamp = orderDetailsFragment.findViewById(R.id.tvDeliverTimestamp);
        TextView tvPickUpContactName = orderDetailsFragment.findViewById(R.id.tvPickUpContactName);
        TextView tvPickUpContactPhoneNumber = orderDetailsFragment.findViewById(R.id.tvPickUpContactNumber);
        TextView tvDropOffContactName = orderDetailsFragment.findViewById(R.id.tvDropOffContactName);
        TextView tvDropOffContactPhoneNumber = orderDetailsFragment.findViewById(R.id.tvDropOffContactNumber);

        tvOrderID.setText(order.getOrderNumber());
        tvPickUpAddress.setText(order.getPickUpAddress().getAddress());
        tvDropOffAddress.setText(order.getDropOffAddress().getAddress());
        tvOrderStatus.setText(order.getOrderStatus().getStatus());
        tvPickUpTimestamp.setText(order.getPickUpTimestamp() != null ? order.getPickUpTimestamp() : "Order has not been picked up yet");
        tvDeliverTimestamp.setText(order.getDropOffTimestamp() != null ? order.getDropOffTimestamp() : "Order has not been dropped off yet");
        tvPickUpContactName.setText(order.getPickUpContactInfo().getName());
        tvPickUpContactPhoneNumber.setText(order.getPickUpContactInfo().getPhoneNumber());
        tvDropOffContactName.setText(order.getDropOffContactInfo().getName());
        tvDropOffContactPhoneNumber.setText(order.getDropOffContactInfo().getPhoneNumber());
        orderIDLabel.setText("Order #" + order.getOrderNumber());
        btnTakeOrder.setVisibility(showClientBtn ? View.INVISIBLE : View.VISIBLE);
        btnGoToOrder.setVisibility(activeOrder ? View.VISIBLE : View.INVISIBLE);
        btnGoToOrder.setOnClickListener(v -> goToActiveOrderMapActivity());
    }

    private void getOrder() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            order = (Order) bundle.getSerializable("order");
            showClientBtn = bundle.getBoolean("client");
            activeOrder = bundle.getBoolean("activeOrder");
        }
    }

    public void goToActiveOrderMapActivity() {
        Intent intent = new Intent(getActivity(), ActiveOrderMapActivity.class);

        intent.putExtra("Order", order);
        startActivity(intent);
    }

}
