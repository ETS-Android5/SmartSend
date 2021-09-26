package com.example.smartsend.smartsendapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.app.Client;
import com.example.smartsend.smartsendapp.utilities.ConnectivityDetector;
import com.example.smartsend.smartsendapp.fragments.CustomDialog;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.example.smartsend.smartsendapp.utilities.UserLocalStore;
import com.example.smartsend.smartsendapp.utilities.app.Rider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {

    private EditText etUserEmail;
    private EditText etPassword;
    private RadioGroup rbgUserType;
    private ImageView ivLogo;
    private Drawable resizedImage;
    private Button btnLoginSubmit, btnForgotPassword;
    private CustomDialog pDialog;
    private String userEmail, userPassword;
    private int checkedUserId;
    private ConnectivityDetector connectivityDetector;
    private FirebaseManager firebaseManager;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private UserLocalStore sessionManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        firebaseManager = FirebaseManager.getInstance();
        currentUser = firebaseManager.getCurrentUser();
        mAuth = firebaseManager.getAuth();
        database = firebaseManager.getFirebaseDatabase();
        sessionManager = UserLocalStore.getInstance(this);
        pDialog = new CustomDialog(LoginActivity.this);

        etPassword = findViewById(R.id.etLoginPassword);
        ivLogo = findViewById(R.id.logoLogin);
        btnLoginSubmit = findViewById(R.id.btnLoginSubmit);
        etUserEmail = findViewById(R.id.etLoginUserEmail);
        rbgUserType = findViewById(R.id.rbgLoginUserType);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        mAuthListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                firebaseManager.setCurrentUser(currentUser);
                if (sessionManager.loggedInUser().equals("rider")) {
                    goRiderDashboardActivity();
                } else if (sessionManager.loggedInUser().equals("client")) {
                    goClientDashboardActivity();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        initializeActivityComponents();
    }

    private void initializeActivityComponents() {
        btnLoginSubmit.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.getBackground().setAlpha(150);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.getBackground().setAlpha(255);
            }
            return false;
        });

        btnLoginSubmit.setOnClickListener(v -> {
            userEmail = etUserEmail.getText().toString().trim();
            userPassword = etPassword.getText().toString().trim();


            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Enter email and password", Toast.LENGTH_SHORT).show();
            } else {
                checkedUserId = rbgUserType.getCheckedRadioButtonId();
                connectivityDetector = new ConnectivityDetector(getBaseContext());

                if (checkedUserId == R.id.rbUserRider) {
                    if (connectivityDetector.checkConnectivityStatus()) {
                        checkRiderLogin(userEmail, userPassword);
                        Toast.makeText(LoginActivity.this, "Rider", Toast.LENGTH_SHORT).show();
                    } else {
                        connectivityDetector.showAlertDialog(LoginActivity.this, "Login Failed", "No internet connection");
                    }

                } else if (checkedUserId == R.id.rbUserClient) {
                    if (connectivityDetector.checkConnectivityStatus()) {
                        checkClientLogin(userEmail, userPassword);
                        Toast.makeText(LoginActivity.this, "Client", Toast.LENGTH_SHORT).show();
                    } else {
                        connectivityDetector.showAlertDialog(LoginActivity.this, "Login Failed", "No internet connection");
                    }
                }
            }
        });

        btnForgotPassword.setOnClickListener(v -> goForgotPasswordActivity());
    }


    private void checkRiderLogin(final String email, final String password) {
        showDialog();

        firebaseManager.getAuth().signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                currentUser = task.getResult().getUser();
                firebaseManager.setCurrentUser(currentUser);
                DatabaseReference ref = firebaseManager.getFirebaseDatabase().getReference().child("riders").child(currentUser.getUid());
                Rider loggedInRider = new Rider();

                ref.get().addOnCompleteListener(refTask -> {
                    if (refTask.isSuccessful()) {
                        HashMap<String, String> riderData = ((HashMap<String, String>) refTask.getResult().getValue());

                        try {
                            loadRiderData(loggedInRider, riderData);

                            sessionManager.storeRiderData(loggedInRider);
                            sessionManager.setRiderLoggedIn(true);
                            hideDialog();
                            goRiderDashboardActivity();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    "Error trying log in. Please try again.", Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    }
                });
            } else {
                hideDialog();
                Toast.makeText(getApplicationContext(),
                        "Error trying log in. Check username or password", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadRiderData(Rider loggedInRider, HashMap<String, String> riderData) {
        loggedInRider.setId(riderData.get("id"));
        loggedInRider.setEmail(riderData.get("email"));
        loggedInRider.setCreated_date(riderData.get("created_date"));
        loggedInRider.setBirth_date(riderData.get("birth_date"));
        loggedInRider.setPassword(riderData.get("password"));
        loggedInRider.setLast_name(riderData.get("last_name"));
        loggedInRider.setFirst_name(riderData.get("first_name"));
        loggedInRider.setVehicle_number(riderData.get("vehicle_number"));
        loggedInRider.setContact_number(riderData.get("contact_number"));
        loggedInRider.setProfile_picture(riderData.get("profile_picture"));
        loggedInRider.setStatus(riderData.get("status"));
    }

    private void checkClientLogin(final String email, final String password) {
        showDialog();

        firebaseManager.getAuth().signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                currentUser = task.getResult().getUser();
                firebaseManager.setCurrentUser(currentUser);
                DatabaseReference ref = firebaseManager.getFirebaseDatabase().getReference().child("clients").child(currentUser.getUid());
                Client loggedInClient = new Client();

                ref.get().addOnCompleteListener(refTask -> {
                    if (refTask.isSuccessful()) {
                        HashMap<String, String> riderData = ((HashMap<String, String>) refTask.getResult().getValue());

                        try {
                            loadClientData(loggedInClient, riderData);
                            sessionManager.storeClientData(loggedInClient);
                            sessionManager.setClientLoggedIn(true);
                            hideDialog();
                            goClientDashboardActivity();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    "Error trying log in. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                hideDialog();
                Toast.makeText(getApplicationContext(),
                        "Error trying log in. Check username or password", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadClientData(Client loggedInClient, HashMap<String, String> riderData) {
        loggedInClient.setId(riderData.get("id"));
        loggedInClient.setEmail(riderData.get("email"));
        loggedInClient.setPassword(riderData.get("password"));
        loggedInClient.setCompany_name(riderData.get("company_name"));
        loggedInClient.setLocation(riderData.get("location"));
        loggedInClient.setBilling_address(riderData.get("billing_address"));
        loggedInClient.setContact_number(riderData.get("contact_number"));
        loggedInClient.setContact_person_name(riderData.get("contact_person_name"));
        loggedInClient.setContact_person_email(riderData.get("contact_person_email"));
        loggedInClient.setContact_person_number(riderData.get("contact_person_number"));
        loggedInClient.setCreated_date(riderData.get("created_date"));
    }

    private void showDialog() {
        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Proccessing");
        pDialog.show();
    }

    private void hideDialog() {
        pDialog.dismiss();
    }

    public void goForgotPasswordActivity() {
        Intent intent = new Intent(LoginActivity.this,
                ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void goRiderDashboardActivity() {
        Intent intent = new Intent(LoginActivity.this,
                RiderMapActivity.class);
        intent.putExtra("riderID", currentUser.getUid());
        startActivity(intent);
        finish();
    }

    public void goClientDashboardActivity() {
        Intent intent = new Intent(LoginActivity.this,
                ClientMapActivity.class);
        intent.putExtra("clientID", currentUser.getUid());
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}