package com.example.smartsend.smartsendapp.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.app.order.signature.SignatureItem;

import java.util.ArrayList;

public class SignaturesAdapter extends RecyclerView.Adapter<SignaturesAdapter.SignatureViewHolder> {
    private final ArrayList<SignatureItem> signatureItems;
    private SignaturesAdapter.OnSignatureDeleteListener deleteListener;

    public void setOnSignatureDeleteListener(SignaturesAdapter.OnSignatureDeleteListener listener) {
        this.deleteListener = listener;
    }

    public interface OnSignatureDeleteListener {
        void onSignatureDelete(int position);
    }

    public static class SignatureViewHolder extends RecyclerView.ViewHolder {
        public ImageView signatureImage;
        public ImageButton btnDeleteSignature;
        public TextView signatureDescription;

        public SignatureViewHolder(@NonNull View itemView, SignaturesAdapter.OnSignatureDeleteListener listener) {
            super(itemView);
            signatureImage = itemView.findViewById(R.id.signatureImage);
            btnDeleteSignature = itemView.findViewById(R.id.btnDeleteSignature);
            signatureDescription = itemView.findViewById(R.id.signatureDescription);

            btnDeleteSignature.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        listener.onSignatureDelete(position);
                    }
                }
            });
        }
    }

    public SignaturesAdapter(ArrayList<SignatureItem> signatureItems) {
        this.signatureItems = signatureItems;
    }

    @NonNull
    @Override
    public SignaturesAdapter.SignatureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_signature_item, parent, false);
        SignaturesAdapter.SignatureViewHolder svh = new SignaturesAdapter.SignatureViewHolder(v, deleteListener);

        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull SignaturesAdapter.SignatureViewHolder holder, int position) {
        SignatureItem signatureItem = signatureItems.get(position);
        Bitmap signature = signatureItem.getSignature().getCurrentImage();

        holder.signatureImage.setImageBitmap(signature);
        holder.signatureDescription.setText(signatureItem.getSignatureDescription());
    }

    @Override
    public int getItemCount() {
        return signatureItems.size();
    }
}

