package com.example.smartsend.smartsendapp.utilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.smartsend.smartsendapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import static com.example.smartsend.smartsendapp.utilities.AppController.TAG;

/**
 * Created by pict-xx on 10/2/2016.
 */

public class FirebaseManager {
    private String serverUrl;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference firebaseDatabaseRef;
    private FirebaseStorage firebaseStorage;
    private static FirebaseManager managerInstance;
    private static final Object managerContextLock = new Object();

    private FirebaseManager() {
        firebaseStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabaseRef = firebaseDatabase.getReference();
    }

    public static FirebaseManager getInstance() {
        if (managerInstance == null) {
            synchronized (managerContextLock) {
                if (managerInstance == null) {
                    managerInstance = new FirebaseManager();
                }
            }
        }

        return managerInstance;
    }

    public File getRiderProfilePicture(String riderID) throws IOException {
        File localFile = File.createTempFile("riderImage", "jpg");
        StorageReference storageReference = firebaseStorage.getReference().child(riderID + ".jpg");

        storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
            Log.d(TAG, "User image uploaded");
        });

        return localFile;
    }

    public void signOut(Context ctx) {
        UserLocalStore localStore = UserLocalStore.getInstance(ctx);

        localStore.clearClientData();
        mAuth.signOut();
    }

    public DatabaseReference getFirebaseDatabaseRef() {
        return firebaseDatabaseRef;
    }

    public void setFirebaseDatabaseRef(DatabaseReference firebaseDatabaseRef) {
        this.firebaseDatabaseRef = firebaseDatabaseRef;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    public String getServerUrl() {
        return this.serverUrl;
    }

    public static String getServerUrl(Context context) {
        return context.getString(R.string.server_url);
    }

    public void resetPassword(Context ctx, String userEmail) {
        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ctx, "Password reset link sent to your email.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ctx, "Error: " + (task.getException().getMessage() != null ? task.getException().getMessage() :
                        "Invalid email entered."), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
