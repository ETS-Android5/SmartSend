package com.example.smartsend.smartsendapp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.UserLocalStore;
import com.example.smartsend.smartsendapp.utilities.app.Client;
import com.example.smartsend.smartsendapp.utilities.ConnectivityDetector;
import com.example.smartsend.smartsendapp.utilities.gcm.GCMController;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by pict-xx on 10/4/2016.
 */

public class ClientDashboardFragment extends Fragment {

    private Button btnPlaceOrder;
    private GoogleCloudMessaging gcm;
    private String projectNumber;
    private String deviceRegIdForGCM = null;
    private Context ctx;
    private CustomDialog pDialog;
    private ConnectivityDetector connectivityDetector;
    private UserLocalStore sessionManager;
    private Client loggedInClient;
    private String serverUrl;
    private TextView btnProfileDuty;
    private TextView tvProfileName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View clientDashboardFragment = inflater.inflate(R.layout.layout_client_dashboard_fragment, container, false);
        ctx = getActivity();

        //initialize objects
        btnPlaceOrder= (Button) clientDashboardFragment.findViewById(R.id.btnPlaceOrder);

        sessionManager = UserLocalStore.getInstance(ctx);
        loggedInClient = sessionManager.getLogedInClient();
        projectNumber = GCMController.getProjectNumber(getActivity());
        serverUrl = FirebaseManager.getServerUrl(getActivity());

        btnProfileDuty = clientDashboardFragment.findViewById(R.id.btnProfileDuty);
        tvProfileName = clientDashboardFragment.findViewById(R.id.tvProfileName);

        btnProfileDuty.setText(loggedInClient.getLocation());
        tvProfileName.setText(loggedInClient.getCompanyName());

        // Progress dialog
        pDialog = new CustomDialog(getActivity());

        //Connectivity detector
        connectivityDetector = new ConnectivityDetector(getActivity());

        //Change button color when click
        btnPlaceOrder.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.getBackground().setAlpha(150);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.getBackground().setAlpha(255);
            }
            return false;
        });

        //Go to Place order fragment
        btnPlaceOrder.setOnClickListener(v -> {
            PlaceOrderFragment placeOrderFragment =  new PlaceOrderFragment();
            getFragmentManager().beginTransaction().replace(R.id.flMain, placeOrderFragment).addToBackStack(null).commit();
        });
        return clientDashboardFragment;
    }

    //Show Diaslog
    private void showDialog() {
        pDialog.setMessage("Please Wait....");
        pDialog.setTitle("Proccessing");
        pDialog.show();
    }

    //Hide Dialog
    private void hideDialog() {
        pDialog.dismiss();
    }
}
