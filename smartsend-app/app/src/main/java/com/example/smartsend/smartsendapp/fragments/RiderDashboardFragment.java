package com.example.smartsend.smartsendapp.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.UserLocalStore;
import com.example.smartsend.smartsendapp.utilities.ConnectivityDetector;
import com.example.smartsend.smartsendapp.utilities.GCMController;
import com.example.smartsend.smartsendapp.utilities.Rider;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

/**
 * Created by pict-xx on 10/3/2016.
 */

public class RiderDashboardFragment extends Fragment {

    private String serverUrl;
    private Toolbar toolbar;
    private ToggleButton tbRiderStatus;
    private GoogleCloudMessaging gcm = null;
    private String projectNumber;
    private String deviceRegIdForGCM = null;
    private Context ctx;
    private ProgressDialog pDialog;
    private ConnectivityDetector connectivityDetector;
    private UserLocalStore sessionManager;
    private Rider loggedInRider;
    private TextView tvRiderProfileLoginTime, tvRiderProfileLocation, tvRiderProfileOrder, tvProfileName, btnProfileLoginTime,
            btnBikeNumber, btnProfileOrder;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View riderDashboardFragment = inflater.inflate(R.layout.layout_rider_dashboard_fragment, container,false);
        ctx = getActivity();

        tbRiderStatus = riderDashboardFragment.findViewById(R.id.tbRiderStatus);
        tvRiderProfileLoginTime = riderDashboardFragment.findViewById(R.id.tvProfileLogin);
        tvRiderProfileLocation = riderDashboardFragment.findViewById(R.id.tvProfileDuty);
        tvRiderProfileOrder = riderDashboardFragment.findViewById(R.id.tvProfileOrder);
        btnProfileLoginTime = riderDashboardFragment.findViewById(R.id.btnProfileLoginTime);
        btnBikeNumber = riderDashboardFragment.findViewById(R.id.btnProfileDuty);
        btnProfileOrder = riderDashboardFragment.findViewById(R.id.btnProfileOrder);

        sessionManager = UserLocalStore.getInstance(ctx);
        loggedInRider = sessionManager.getLoggedInRider();
        projectNumber = GCMController.getProjectNumber(getActivity());
        serverUrl = FirebaseManager.getServerUrl(getActivity());

        tvProfileName = riderDashboardFragment.findViewById(R.id.tvProfileName);
        tvProfileName.setText(loggedInRider.getName());
        btnBikeNumber = riderDashboardFragment.findViewById(R.id.btnBikeNumber);
        btnBikeNumber.setText(loggedInRider.getBikeNumber());

        pDialog = new ProgressDialog(getActivity());
        connectivityDetector = new ConnectivityDetector(getActivity());

        tbRiderStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setRiderStatus("1");
                    activateRiderProfile();
                }else{
                    setRiderStatus("0");
                    inactiveRiderProfile();
                }
            }
        });

        return riderDashboardFragment;
    }

    public void getRiderStatus(){
        String MSG;

        final String[] tag_string_req = {"req_login"};

        showDialog();
        try {
            DatabaseReference databaseRef = FirebaseManager.getInstance().getFirebaseDatabase().getReference("/riders/" + loggedInRider.getId());
            databaseRef.get().addOnCompleteListener(refTask -> {
                if (refTask.isSuccessful()) {
                    HashMap<String, String> riderData = ((HashMap<String, String>) refTask.getResult().getValue());

                    int riderStatus = Integer.parseInt(riderData.get("status"));

                    if (riderStatus == 0) {
                        inactiveRiderProfile();
                    } else if (riderStatus == 1) {
                        activateRiderProfile();
                    }
                }
            });
        } catch (Exception e){
            Toast.makeText(ctx, "Error Message : "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
        hideDialog();

    }//End of checkRiderStatus

    public void setRiderStatus(String status){
        String MSG;

        // Tag used to cancel the request
        final String[] tag_string_req = {"req_login"};

        //Show progress dialog
        showDialog();

        try {
            DatabaseReference databaseRef = FirebaseManager.getInstance().getFirebaseDatabase().getReference("/riders/" + loggedInRider.getId());
            databaseRef.child("/status").setValue(status);
        } catch (Exception ex) {
            Toast.makeText(ctx, "Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        hideDialog();
    }//End of checkRiderStatus


    //Inactive rider profile
    public void inactiveRiderProfile(){
        btnProfileLoginTime.setBackgroundResource(R.drawable.bg_profile_item_inactive);
        btnProfileLoginTime.setTextColor(Color.parseColor("#514E4D"));
        btnBikeNumber.setBackgroundResource(R.drawable.bg_profile_item_inactive);
        btnBikeNumber.setTextColor(Color.parseColor("#514E4D"));
        btnProfileOrder.setBackgroundResource(R.drawable.bg_profile_item_inactive);
        btnProfileOrder.setTextColor(Color.parseColor("#514E4D"));
        btnProfileOrder.setWidth(400);
    }

    //Active rider profile
    public void activateRiderProfile(){
        btnProfileLoginTime.setBackgroundResource(R.drawable.bg_profile_item);
        btnProfileLoginTime.setTextColor(Color.parseColor("#ffffff"));
        btnBikeNumber.setBackgroundResource(R.drawable.bg_profile_item);
        btnBikeNumber.setTextColor(Color.parseColor("#ffffff"));
        btnProfileOrder.setBackgroundResource(R.drawable.bg_profile_item);
        btnProfileOrder.setTextColor(Color.parseColor("#ffffff"));
        btnProfileOrder.setWidth(400);
    }


    //Show Diaslog
    private void showDialog() {
        //if (!pDialog.isShowing()) {
        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Proccessing");
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
