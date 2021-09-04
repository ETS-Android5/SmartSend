package com.example.smartsend.smartsendapp.activities;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.utilities.ConnectivityDetector;
import com.example.smartsend.smartsendapp.utilities.FirebaseManager;
import com.google.firebase.auth.FirebaseAuthException;

public class ForgotPasswordActivity extends AppCompatActivity {
    private Button btnResetSubmit, btnBackArrow;
    private String userEmail;
    private ConnectivityDetector connectivityDetector;
    private TextView etUserEmail;
    private FirebaseManager firebaseManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnResetSubmit = findViewById(R.id.btnResetSubmit);
        btnBackArrow = findViewById(R.id.btnArrowBack);
        etUserEmail = findViewById(R.id.etResetUserEmail);
        firebaseManager = FirebaseManager.getInstance();

        btnResetSubmit.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                v.getBackground().setAlpha(150);
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                v.getBackground().setAlpha(255);
            }
            return false;
        });

        btnResetSubmit.setOnClickListener(v -> {
            userEmail = etUserEmail.getText().toString().trim();

            if(userEmail.isEmpty()){
                Toast.makeText(ForgotPasswordActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
            }else{

                try {
                    connectivityDetector = new ConnectivityDetector(getBaseContext());
                    if(connectivityDetector.checkConnectivityStatus()){
                        firebaseManager.resetPassword(this, userEmail);
                    }else{
                        connectivityDetector.showAlertDialog(ForgotPasswordActivity.this, "Password Reset Failed","No internet connection");
                    }
                } catch (Exception ex) {
                    Toast.makeText(ForgotPasswordActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } //End of else
        });

        btnBackArrow.setOnClickListener(v -> finish());
    }
}
