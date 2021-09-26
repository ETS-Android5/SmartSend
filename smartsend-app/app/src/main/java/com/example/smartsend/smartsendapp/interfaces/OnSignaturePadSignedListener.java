package com.example.smartsend.smartsendapp.interfaces;

import android.graphics.Bitmap;

public interface OnSignaturePadSignedListener {
    void onSignaturePadSigned(boolean isPadEmpty, Bitmap signature);
}
