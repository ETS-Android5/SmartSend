package com.example.smartsend.smartsendapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
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
import com.example.smartsend.smartsendapp.utilities.app.Rider;
import com.example.smartsend.smartsendapp.utilities.UserLocalStore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import static com.example.smartsend.smartsendapp.utilities.AppController.TAG;


public class LoginActivity extends AppCompatActivity {

    //Declare variable and object
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
    private String serverUrl;
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Initilize variable and object
        firebaseManager = FirebaseManager.getInstance();
        currentUser = firebaseManager.getCurrentUser();
        mAuth = firebaseManager.getAuth();
        database = firebaseManager.getFirebaseDatabase();
        serverUrl = FirebaseManager.getServerUrl(this);
        sessionManager = UserLocalStore.getInstance(this);

        etPassword = findViewById(R.id.etLoginPassword);
        ivLogo = findViewById(R.id.logoLogin);
        btnLoginSubmit = findViewById(R.id.btnLoginSubmit);
        etUserEmail = findViewById(R.id.etLoginUserEmail);
        rbgUserType = findViewById(R.id.rbgLoginUserType);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        // Progress dialog
        pDialog = new CustomDialog(LoginActivity.this);


//        sessionManager.clearData();
        mAuthListener = firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {
                if(sessionManager.loggedInUser().equals("rider")) {
                    Intent goRiderProfileActivity = new Intent(LoginActivity.this, RiderDashboardActivity.class);
                    startActivity(goRiderProfileActivity);
                    finish();
                } else if(sessionManager.loggedInUser().equals("client")) {
                    Intent goClientProfileActivity = new Intent(LoginActivity.this, ClientDashboardActivity.class);
                    startActivity(goClientProfileActivity);
                    finish();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        btnLoginSubmit.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                v.getBackground().setAlpha(150);
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                v.getBackground().setAlpha(255);
            }
            return false;
        });


        // End of onClick
        btnLoginSubmit.setOnClickListener(v -> {
            userEmail = etUserEmail.getText().toString().trim();
            userPassword = etPassword.getText().toString().trim();

            //Login for validation
            if(userEmail.isEmpty() || userPassword.isEmpty()){
                Toast.makeText(LoginActivity.this, "Enter email and password", Toast.LENGTH_SHORT).show();
            }else{
                //Check user type
                checkedUserId = rbgUserType.getCheckedRadioButtonId();
                connectivityDetector = new ConnectivityDetector(getBaseContext());

                if(checkedUserId == R.id.rbUserRider){
                    if(connectivityDetector.checkConnectivityStatus()){
                        checkRiderLogin(userEmail, userPassword);
                        Toast.makeText(LoginActivity.this, "Rider", Toast.LENGTH_SHORT).show();
                    }else{
                        connectivityDetector.showAlertDialog(LoginActivity.this, "Login Failed","No internet connection");
                    }

                } else if(checkedUserId == R.id.rbUserClient){
                    if(connectivityDetector.checkConnectivityStatus()){
                        checkClientLogin(userEmail, userPassword);
                        Toast.makeText(LoginActivity.this, "Client", Toast.LENGTH_SHORT).show();
                    }else{
                        connectivityDetector.showAlertDialog(LoginActivity.this, "Login Failed","No internet connection");
                    }
                }
            } //End of else

        });

        btnForgotPassword.setOnClickListener(v -> {
            goForgotPasswordActivity();
        });
    } //End of onCreate


    //Not used
    //Resizeing Image function
    public Drawable ResizeImage (int imageID) {
        //Get device dimensions
        Display display = getWindowManager().getDefaultDisplay();
        double deviceWidth = display.getWidth();

        BitmapDrawable bd=(BitmapDrawable) this.getResources().getDrawable(imageID);
        double imageHeight = bd.getBitmap().getHeight();
        double imageWidth = bd.getBitmap().getWidth();

        double ratio = deviceWidth / imageWidth;
        int newImageHeight = (int) (imageHeight * ratio);

        Bitmap bMap = BitmapFactory.decodeResource(getResources(), imageID);
        Drawable drawable = new BitmapDrawable(this.getResources(),getResizedBitmap(bMap,newImageHeight,(int) deviceWidth));

        return drawable;
    }


    //Not used
    /************************ Resize Bitmap *********************************/
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

// create a matrix for the manipulation
        Matrix matrix = new Matrix();

// resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

// recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }

    private void checkRiderLogin(final String email, final String password) {
        showDialog();
        firebaseManager.getAuth().signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());

            try {
                if (task.isSuccessful()) {
                    currentUser = task.getResult().getUser();
                    firebaseManager.setCurrentUser(currentUser);
                    DatabaseReference ref = firebaseManager.getFirebaseDatabase().getReference().child("riders").child(currentUser.getUid());
                    Rider loggedInRider = new Rider();

                    ref.get().addOnCompleteListener(refTask -> {
                        if (refTask.isSuccessful()) {
                            HashMap<String, String> riderData = ((HashMap<String, String>) refTask.getResult().getValue());

                            try {
                                loggedInRider.setId(riderData.get("id"));
                                loggedInRider.setEmail(riderData.get("email"));
                                loggedInRider.setCreatedDate(riderData.get("created_date"));
                                loggedInRider.setPasword(riderData.get("password"));
                                loggedInRider.setName(riderData.get("first_name") + " " + riderData.get("last_name"));
                                loggedInRider.setBikeNumber(riderData.get("vehicle_number"));
                                loggedInRider.setContactNumber(riderData.get("contact_number"));
                                loggedInRider.setProfilePicture(riderData.get("profile_picture"));
                                loggedInRider.setStatus(riderData.get("status"));

                                sessionManager.storeRiderData(loggedInRider);
                                sessionManager.setRiderLoggedIn(true);

                                // Launch main activity
                                hideDialog();
                                goRiderDashboardActivity();
                            } catch (Exception e){
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
            } catch (/*JSONException e*/ Exception e) {
                // JSON error
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Exception error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
    }


    //Check Client Login
    private void checkClientLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        String eEmail = null;
        String ePassword = null;
        try {
            eEmail = URLEncoder.encode(email, "UTF-8");
            ePassword = URLEncoder.encode(password, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        firebaseManager.getAuth().signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());

            try {
                if (task.isSuccessful()) {
                    currentUser = task.getResult().getUser();
                    firebaseManager.setCurrentUser(currentUser);
                    DatabaseReference ref = firebaseManager.getFirebaseDatabase().getReference().child("clients").child(currentUser.getUid());
                    Client loggedInClient = new Client();

                    ref.get().addOnCompleteListener(refTask -> {
                        if (refTask.isSuccessful()) {
                            HashMap<String, String> riderData = ((HashMap<String, String>) refTask.getResult().getValue());

                            try {
                                loggedInClient.setId(riderData.get("id"));
                                loggedInClient.setEmail(riderData.get("email"));
                                loggedInClient.setPassword(riderData.get("password"));
                                loggedInClient.setCompanyName(riderData.get("company_name"));
                                loggedInClient.setCompanyPostalCode(riderData.get("company_postal_code"));
                                loggedInClient.setCompanyUnitNumber(riderData.get("company_unit_number"));
                                loggedInClient.setLocation(riderData.get("location"));
                                loggedInClient.setBillingAddress(riderData.get("billing_address"));
                                loggedInClient.setContactNumber(riderData.get("contact_number"));
                                loggedInClient.setContactPersonName(riderData.get("contact_person_name"));
                                loggedInClient.setContactPersonEmail(riderData.get("contact_person_email"));
                                loggedInClient.setContactPersonNumber(riderData.get("contact_person_number"));
                                loggedInClient.setCreatedDate(riderData.get("created_date"));
                                loggedInClient.setClientType(riderData.get("client_type"));

                                sessionManager.storeClientData(loggedInClient);
                                sessionManager.setClientLoggedIn(true);

                                hideDialog();
                                goClientDashboardActivity();
                            } catch (Exception e){
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
            } catch (/*JSONException e*/ Exception e) {
                // JSON error
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Exception error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });
    }

    //Show Diaslog
    private void showDialog() {
        //if (!pDialog.isShowing()) {
        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Proccessing");
        pDialog.show();
        // }
    }

    //Hide Dialog
    private void hideDialog() {
        // if (pDialog.isShowing()) {
        pDialog.dismiss();
        //}
    }

    public void goForgotPasswordActivity() {
        Intent intent = new Intent(LoginActivity.this,
                ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void goRiderDashboardActivity(){
        Intent intent = new Intent(LoginActivity.this,
                RiderDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    public void goClientDashboardActivity(){
        Intent intent = new Intent(LoginActivity.this,
                ClientDashboardActivity.class);
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
} //End of activity
