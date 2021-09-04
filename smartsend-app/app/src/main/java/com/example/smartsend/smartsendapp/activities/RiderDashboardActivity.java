package com.example.smartsend.smartsendapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.fragments.RiderAccountSettingsFragment;
import com.example.smartsend.smartsendapp.fragments.RiderDashboardFragment;
import com.example.smartsend.smartsendapp.fragments.CustomDialog;
import com.example.smartsend.smartsendapp.utilities.location.SmartSendLocationManager;
import com.example.smartsend.smartsendapp.utilities.UserLocalStore;
import com.example.smartsend.smartsendapp.utilities.ConnectivityDetector;
import com.example.smartsend.smartsendapp.utilities.gcm.GCMController;
import com.example.smartsend.smartsendapp.utilities.app.Rider;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RiderDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private String serverUrl;
    private Toolbar toolbar;
    private ToggleButton tbRiderStatus;
    private GoogleCloudMessaging gcm = null;
    private String projectNumber;
    private String deviceRegIdForGCM = null;
    private Context ctx = this;
    private CustomDialog pDialog;
    private ConnectivityDetector connectivityDetector;
    private UserLocalStore sessionManager;
    private Rider loggedInRider;
    private NavigationView navigationView;
    private ImageView profilePicture;
    private TextView tvName;
    private TextView tvEmail;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_dashboard);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sessionManager = UserLocalStore.getInstance(ctx);
        loggedInRider = sessionManager.getLoggedInRider();
        projectNumber = GCMController.getProjectNumber(this);
        serverUrl = FirebaseManager.getServerUrl(this);
        pDialog = new CustomDialog(RiderDashboardActivity.this);
        connectivityDetector = new ConnectivityDetector(getBaseContext());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view_rider_dashboard);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().performIdentifierAction(R.id.nav_rider_dashboard, 0);

        View navHeader = getLayoutInflater().inflate(R.layout.nav_header_main, navigationView);
        profilePicture = (ImageView) navHeader.findViewById(R.id.ivprofilePicture);
        tvName = (TextView) navHeader.findViewById(R.id.tvName);
        tvName.setText(loggedInRider.getName());
        tvEmail = (TextView) navHeader.findViewById(R.id.tvEmail);
        tvEmail.setText(loggedInRider.getEmail());
        try {
            Picasso.get().load(FirebaseManager.getInstance().getRiderProfilePicture(loggedInRider.getId())).into(profilePicture);
        } catch (IOException ignored) {
        }

        SmartSendLocationManager ssLocationManager = new SmartSendLocationManager(getApplicationContext(),60000, 10);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        ssLocationManager.setLocationManagerAndListener();

        //Background Task for registering
        new AsyncTask() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showDialog();
            }

            @Override
            protected Object doInBackground(Object[] params) {

                try {
                    if(gcm == null){
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }

                    if(gcm != null){
                        deviceRegIdForGCM = gcm.register(projectNumber);
                    }

                    if(deviceRegIdForGCM != null){
                        registerRiderDevice();
                        //Toast.makeText(getApplicationContext(), "Device Registered : "+deviceRegIdForGCM, Toast.LENGTH_LONG).show();
                    }else{
                        // Toast.makeText(getApplicationContext(), "Device Registration Failed: "+deviceRegIdForGCM, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(), "IOException", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                hideDialog();
            }
        }.execute(null,null,null);
        //End of registering rider device

    }//End of onCreate

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.nav_rider_dashboard: {
                RiderDashboardFragment riderDashboardFragment =  new RiderDashboardFragment();
                getFragmentManager().beginTransaction().replace(R.id.flMain, riderDashboardFragment).addToBackStack(null).commit();
                break;
            }
            case R.id.nav_rider_account_settings: {
                RiderAccountSettingsFragment accountSettingsFragment = new RiderAccountSettingsFragment();
                getFragmentManager().beginTransaction().replace(R.id.flMain, accountSettingsFragment).addToBackStack(null).commit();
                break;
            }
            case R.id.nav_rider_signout: {
                FirebaseManager firebaseManager = FirebaseManager.getInstance();

                firebaseManager.signOut(ctx);
                Intent intent = new Intent(this,
                        LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Register Device for GCM  service
    public void registerRiderDevice(){
        String MSG;

        // Tag used to cancel the request
        String serverAddress = serverUrl+"/rest_controller/register_rider_device/"+loggedInRider.getId()+"/"+deviceRegIdForGCM;
        Map<String, String> params = new HashMap<String, String>();

        //Show progress dialog
        //showDialog();

        JsonObjectRequest registerRiderDeviceRequest = new JsonObjectRequest(Request.Method.POST,
                serverAddress, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jObj) {

                try {
                    boolean error = jObj.getBoolean("error");

                    if(!error){
                        String successMessage = jObj.getString("success_message");
                        // return outlets;
                    }else{
                        String errorMessage = jObj.getString("error_message");
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

                //hideDialog();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //hideDialog();
                Log.e("Login Error", "Login Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        //AppController.getInstance().addToRequestQueue(strReq);
        Volley.newRequestQueue(ctx).add(registerRiderDeviceRequest);

    }//End of registerRiderDevice

    //Show Diaslog
    private void showDialog() {
        //if (!pDialog.isShowing()) {
            pDialog.setMessage("Please Wait....");
            pDialog.setTitle("Processing");
            pDialog.show();
        //}
    }

    //Hide Dialog
    private void hideDialog() {
        //if (pDialog.isShowing()) {
            pDialog.dismiss();
        //}
    }



}
