package com.example.smartsend.smartsendapp.utilities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsend.smartsendapp.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private ArrayList<ClientHistoryItem> historyItems;
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

    public HistoryAdapter(ArrayList<ClientHistoryItem> historyItems) {
        this.historyItems = historyItems;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_history_item, parent, false);
        HistoryViewHolder hvh = new HistoryViewHolder(v, listener);

        return hvh;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ClientHistoryItem historyItem = historyItems.get(position);

        holder.pickUpAddress.setText(historyItem.getPickUpAddress());
        holder.pickUpTimestamp.setText(historyItem.getPickUpTimestamp());
        holder.dropOffAddress.setText(historyItem.getDropOffAddress());
        holder.dropOffTimestamp.setText(historyItem.getDropOffTimestamp());
        holder.orderNumber.setText("Order #" + historyItem.getOrderNumber());
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }
}
