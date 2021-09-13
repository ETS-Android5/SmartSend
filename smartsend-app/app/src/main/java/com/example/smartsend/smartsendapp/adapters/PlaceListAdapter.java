package com.example.smartsend.smartsendapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.autocomplete.Listclass;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {
    private ArrayList<Listclass> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PlaceListAdapter(ArrayList<Listclass> list) {
        this.list = list;
    }

    public Listclass getItem(int position) {
        return list.get(position);
    }

    @NotNull
    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.place_search_item, parent, false);
        return new PlaceViewHolder(view, listener);
    }


    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        holder.nameTextView.setText(list.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        else return list.size();
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public PlaceViewHolder(View itemView , OnItemClickListener listener) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.address_text_view);

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
}