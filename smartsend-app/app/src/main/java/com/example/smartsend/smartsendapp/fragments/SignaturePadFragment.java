package com.example.smartsend.smartsendapp.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.interfaces.OnSignaturePadSignedListener;
import com.example.smartsend.smartsendapp.utilities.app.order.Order;
import com.kyanogen.signatureview.SignatureView;

public class SignaturePadFragment extends Fragment {
    private View signaturePadFragment;
    private OnSignaturePadSignedListener listener;

    public SignaturePadFragment(OnSignaturePadSignedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        signaturePadFragment = inflater.inflate(R.layout.layout_signaturepad_fragment, container, false);

        initializeFragmentComponents();
        return signaturePadFragment;
    }

    private void initializeFragmentComponents() {
        SignatureView signatureView = signaturePadFragment.findViewById(R.id.signaturePad);
        Button btnSave = signaturePadFragment.findViewById(R.id.btnSave);
        Button btnClear = signaturePadFragment.findViewById(R.id.btnClear);

        btnClear.setOnClickListener(v -> signatureView.clearCanvas());
        btnSave.setOnClickListener(v -> listener.onSignaturePadSigned(signatureView.isBitmapEmpty(),
                signatureView.getSignatureBitmap()));
    }
}
