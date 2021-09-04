package com.example.smartsend.smartsendapp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.smartsend.smartsendapp.R;

public class RiderAccountSettingsFragment extends Fragment {
    private Context ctx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View accountSettingsFragment = inflater.inflate(R.layout.layout_rider_dashboard_fragment, container,false);
        ctx = getActivity();

        return accountSettingsFragment;
    }

}
