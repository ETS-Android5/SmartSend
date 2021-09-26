package com.example.smartsend.smartsendapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.app.OrderItem;
import com.example.smartsend.smartsendapp.utilities.app.order.Order;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private ArrayList<OrderItem> historyItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class HistoryViewHolder extends  RecyclerView.ViewHolder {
        public TextView pickUpAddress, pickUpTimestamp, dropOffAddress, dropOffTimestamp, orderNumber;

        public HistoryViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            pickUpAddress = itemView.findViewById(R.id.pickUpAddress);
            pickUpTimestamp = itemView.findViewById(R.id.pickUpTimestamp);
            dropOffAddress = itemView.findViewById(R.id.dropOffAddress);
            dropOffTimestamp = itemView.findViewById(R.id.dropOffTimestamp);
            orderNumber = itemView.findViewById(R.id.orderNumber);

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    public HistoryAdapter(ArrayList<OrderItem> historyItems) {
        this.historyItems = historyItems;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_client_history_item, parent, false);
        HistoryViewHolder hvh = new HistoryViewHolder(v, listener);

        return hvh;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Order historyItem = historyItems.get(position).getOrder();

        holder.pickUpAddress.setText(historyItem.getPickUpAddress().getAddress());
        holder.pickUpTimestamp.setText(historyItem.getTimestamp());
        holder.dropOffAddress.setText(historyItem.getDropOffAddress().getAddress());
        holder.dropOffTimestamp.setText(historyItem.getTimestamp());
        holder.orderNumber.setText("Order #" + historyItem.getOrderNumber());
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }
}
