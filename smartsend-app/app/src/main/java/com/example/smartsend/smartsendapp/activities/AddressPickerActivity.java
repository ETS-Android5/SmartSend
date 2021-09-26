package com.example.smartsend.smartsendapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartsend.smartsendapp.R;
import com.example.smartsend.smartsendapp.interfaces.ApiInterface;
import com.example.smartsend.smartsendapp.utilities.autocomplete.Listclass;
import com.example.smartsend.smartsendapp.utilities.autocomplete.MainPojo;
import com.example.smartsend.smartsendapp.adapters.PlaceListAdapter;
import com.example.smartsend.smartsendapp.utilities.location.SmartSendLocationManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddressPickerActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;
    private EditText tiPickUpAddress, tiDropOffAddress;
    private SmartSendLocationManager locationManager;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_picker);

        mRecyclerView = (RecyclerView) findViewById(R.id.searchedPlaces);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .build();

        apiInterface = retrofit.create(ApiInterface.class);

        tiPickUpAddress = findViewById(R.id.tiPickUpAddress);
        tiDropOffAddress = findViewById(R.id.tiDropOfAddress);

        tiPickUpAddress.setOnClickListener(view -> {
            tiPickUpAddress.setText("");
        });

        tiDropOffAddress.setOnClickListener(view -> {
            tiDropOffAddress.setText("");
        });

        tiPickUpAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getData(editable.toString());
            }
        });

        tiDropOffAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getData(editable.toString());
            }
        });

        initializeLocationManager();
    }

    private void getData(String text) {
        if (!text.equals("Current Location")) {
            apiInterface.getPlace(text, "AIzaSyBUiecg0U9MpA9SNXI-UoPSUpvZV8tXYTg").enqueue(new Callback<MainPojo>() {
                @Override
                public void onResponse(Call<MainPojo> call, Response<MainPojo> response) {
                    if (response.isSuccessful()) {
                        mRecyclerView.setVisibility(View.VISIBLE);

                        PlaceListAdapter placeListAdapter = new PlaceListAdapter(response.body().getPredictions());

                        placeListAdapter.setOnItemClickListener((position -> {
                            Listclass clickedOption = placeListAdapter.getItem(position);

                            if (tiPickUpAddress.isFocused()) {
                                tiPickUpAddress.setText(clickedOption.getDescription());
                            } else {
                                tiDropOffAddress.setText(clickedOption.getDescription());
                            }
                        }));

                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        mRecyclerView.setAdapter(placeListAdapter);
                    } else {
                        mRecyclerView.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<MainPojo> call, Throwable t) {
                    Toast.makeText(AddressPickerActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void goToOrderSummaryActivity(View v) {
        Intent intent = new Intent(this,
                OrderSummaryActivity.class);
        intent.putExtra("pickUpAddress", tiPickUpAddress.getText().toString());
        intent.putExtra("dropOffAddress", tiDropOffAddress.getText().toString());
        startActivity(intent);
    }

    private void initializeLocationManager() {
        locationManager = new SmartSendLocationManager(this, 60000, 10);
        locationManager.setLocationManagerAndListener();
    }


    public void closeActivity(View view) {
        finish();
    }

    public void useCurrentLocation(View view) {
        if (tiPickUpAddress.isFocused() ||
                (!tiPickUpAddress.isFocused() && !tiDropOffAddress.isFocused() && tiPickUpAddress.getText().toString().isEmpty())) {
            tiPickUpAddress.setText("Current Location");
        }
        else {
            tiDropOffAddress.setText("Current Location");
        }
    }
}

