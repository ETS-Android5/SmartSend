package com.example.smartsend.smartsendapp.utilities.app.order.signature;

import java.io.Serializable;

public class SignatureItem implements Serializable {
    private SerializableBitmap signature;
    private String signatureDescription;

    public SignatureItem(SerializableBitmap signature, String signatureDescription) {
        this.signature = signature;
        this.signatureDescription = signatureDescription;
    }

    public SerializableBitmap getSignature() {
        return signature;
    }

    public String getSignatureDescription() {
        return signatureDescription;
    }

    public void setSignatureDescription(String signatureDescription) {
        this.signatureDescription = signatureDescription;
    }

    public void setSignature(SerializableBitmap signature) {
        this.signature = signature;
    }
}
