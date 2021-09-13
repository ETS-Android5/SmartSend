package com.example.smartsend.smartsendapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.app.order.ClientPendingOrderItem;

import java.util.ArrayList;

public class ClientPendingOrdersAdapter extends RecyclerView.Adapter<ClientPendingOrdersAdapter.PendingOrderHolder> {
    private ArrayList<ClientPendingOrderItem> clientPendingOrderItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class PendingOrderHolder extends  RecyclerView.ViewHolder {
        public TextView tvAddress, tvOrderNumber, tvTimestamp;

        public PendingOrderHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);

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

    public ClientPendingOrdersAdapter(ArrayList<ClientPendingOrderItem> clientPendingOrderItems) {
        this.clientPendingOrderItems = clientPendingOrderItems;
    }

    @NonNull
    @Override
    public PendingOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_order_item, parent, false);
        PendingOrderHolder poh = new PendingOrderHolder(v, listener);

        return poh;
    }

    @Override
    public void onBindViewHolder(@NonNull PendingOrderHolder holder, int position) {
        ClientPendingOrderItem clientPendingOrderItem = clientPendingOrderItems.get(position);

        if (clientPendingOrderItem != null) {
            holder.tvOrderNumber.setText("Order #" + clientPendingOrderItem.getOrderNumber());
            holder.tvAddress.setText(clientPendingOrderItem.getPrderDropOffAddress());
            holder.tvTimestamp.setText(clientPendingOrderItem.getOrderTimestamp());
        }
    }

    @Override
    public int getItemCount() {
        if (clientPendingOrderItems == null) return 0;
        else return clientPendingOrderItems.size();
    }
}
