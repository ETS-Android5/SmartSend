package com.example.smartsend.smartsendapp.activities.containers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.activities.ClientMapActivity;
import com.example.smartsend.smartsendapp.activities.ClientOrderHistoryActivity;
import com.example.smartsend.smartsendapp.activities.ClientProfileActivity;
import com.example.smartsend.smartsendapp.activities.ContactUsActivity;
import com.example.smartsend.smartsendapp.activities.LoginActivity;
import com.example.smartsend.smartsendapp.fragments.CustomDialog;
import com.example.smartsend.smartsendapp.utilities.ConnectivityDetector;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.example.smartsend.smartsendapp.utilities.UserLocalStore;
import com.example.smartsend.smartsendapp.utilities.app.Client;
import com.google.android.material.navigation.NavigationView;


public class ClientMenuContainerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected Context ctx = this;
    protected CustomDialog pDialog;
    protected ConnectivityDetector connectivityDetector;
    protected UserLocalStore sessionManager;
    protected Client loggedInClient;
    protected DrawerLayout drawer;

    private NavigationView navigationView;
    private ImageView profilePicture;
    private TextView tvName;
    private TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_menu_container);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sessionManager = UserLocalStore.getInstance(this);
        loggedInClient = sessionManager.getLogedInClient();
        pDialog = new CustomDialog(this);
        connectivityDetector = new ConnectivityDetector(getBaseContext());

        drawer = findViewById(R.id.drawer_layout);
        drawer.open();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view_client_dashboard);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().performIdentifierAction(R.id.nav_place_order, 0);

        View navHeader = getLayoutInflater().inflate(R.layout.nav_client_header_main, navigationView);
        profilePicture = navHeader.findViewById(R.id.ivProfilePicture);
        tvName = navHeader.findViewById(R.id.tvName);
        tvName.setText(loggedInClient.getCompany_name());
        tvEmail = navHeader.findViewById(R.id.tvEmail);
        tvEmail.setText(loggedInClient.getEmail());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_place_order: {
                if (!(this instanceof ClientMapActivity)) {
                    goClientMapActivity();
                    finish();
                }
                break;
            }
            case R.id.nav_client_order_history: {
                goClientOrderHistoryActivity();
                break;
            }
            case R.id.nav_my_account: {
                goUserProfileActivity();
                break;
            }
            case R.id.nav_client_signout: {
                FirebaseManager firebaseManager = FirebaseManager.getInstance();

                firebaseManager.signOut(ctx);
                Intent intent = new Intent(this,
                        LoginActivity.class);
                startActivity(intent);

                finish();
                break;
            }
            case R.id.nav_share: {
                Intent intent = new Intent(Intent.ACTION_SEND);

                intent.setType("text/plain");
                String body = "Download This App";
                String sub = "http://play.google.com";
                intent.putExtra(Intent.EXTRA_TEXT, body);
                intent.putExtra(Intent.EXTRA_TEXT, sub);
                startActivity(intent.createChooser(intent, "Share using"));
                break;
            }
            case R.id.nav_contact_us: {
                goContactUsActivity();
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void goContactUsActivity() {
        Intent intent = new Intent(ClientMenuContainerActivity.this,
                ContactUsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected void showDialog() {
        pDialog.show();
    }

    protected void hideDialog() {
        pDialog.dismiss();
    }

    public void goClientMapActivity(){
        Intent intent = new Intent(ClientMenuContainerActivity.this,
                ClientMapActivity.class);
        startActivity(intent);
    }

    private void goClientOrderHistoryActivity() {
        Intent intent = new Intent(ClientMenuContainerActivity.this,
                ClientOrderHistoryActivity.class);
        intent.putExtra("clientID", loggedInClient.getId());
        startActivity(intent);
    }

    private void goUserProfileActivity() {
        Intent intent = new Intent(ClientMenuContainerActivity.this,
                ClientProfileActivity.class);
        intent.putExtra("clientID", loggedInClient.getId());
        startActivity(intent);
    }
}
