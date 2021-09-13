package com.example.smartsend.smartsendapp.utilities;

import android.app.Application;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartsend.smartsendapp.utilities.location.LocationRequestActivity;

/**
 * Created by AGM TAZIM on 12/30/2015.
 */
public class AppController extends Application{
    public static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static void requestPremissions(AppCompatActivity callingActivity) {
        Intent intent = new Intent(callingActivity,
                LocationRequestActivity.class);
        callingActivity.startActivity(intent);
    }
}