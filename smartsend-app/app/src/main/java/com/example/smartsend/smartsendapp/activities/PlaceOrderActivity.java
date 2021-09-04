package com.example.smartsend.smartsendapp.activities;

import androidx.appcompat.app.AppCompatActivity;

public class PlaceOrderActivity extends AppCompatActivity{

//    private Client loggedInClient;
//    private FirebaseDatabase firebaseDatabase;
//    private Button btnOrder;
//    private Spinner spOutlet;
//    private Context ctx = this;
//    private CustomDialog pDialog;
//    private ConnectivityDetector connectivityDetector;
//    private EditText etPickupDateTime, etDeliverDateTime, etMobileNumber, etCustomerName, etPostalCode, etAddress, etUnitNumberFirst, etUnitNumberLast, etFoodCost, etReceiptNumber;
//    private String pickupDateTime, deliverDateTime, mobileNumber, customerName, postalCode, address, unitNumberFirst, unitNumberLast, foodCost, receiptNumber;
//    private String serverUrl;
//
//    public PlaceOrderActivity(Client loggedInClient) {
//        this.loggedInClient = loggedInClient;
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        setContentView(R.activity_order_details_accepted.temp);
//
//        firebaseDatabase = FirebaseManager.getInstance().getFirebaseDatabase();
//        btnOrder = (Button) findViewById(R.id.btnOrder);
//        spOutlet = (Spinner) findViewById(R.id.spOutlet);
//        etPickupDateTime = (EditText) findViewById(R.id.etPickupDateAndTime);
//        etDeliverDateTime = (EditText) findViewById(R.id.etDeliverDateTime);
//        etMobileNumber = (EditText) findViewById(R.id.etMobileNumber);
//        etCustomerName = (EditText) findViewById(R.id.etCustomerName);
//        etPostalCode = (EditText) findViewById(R.id.etPostalCode);
//        etAddress = (EditText) findViewById(R.id.etAddress);
//        etUnitNumberFirst = (EditText) findViewById(R.id.etUnitNoFirst);
//        etUnitNumberLast = (EditText) findViewById(R.id.etUnitNumberLast);
//        etFoodCost = (EditText) findViewById(R.id.etFoodCost);
//        etReceiptNumber = (EditText) findViewById(R.id.etReceiptNumber);
//
//        pDialog = new CustomDialog(this);
//        pDialog.setCancelable(false);
//
//        connectivityDetector = new ConnectivityDetector(ctx);
//
//        UserLocalStore userLocalStore = UserLocalStore.getInstance(this);
//        final Client loggedInClient = userLocalStore.getLogedInClient();
//        final String loggedInClientId = loggedInClient.getId();
//        final int maxLengthOfPostCode = 6;
////        getOutletsbyClientId(loggedInClientId,  spOutlet);
//
//        btnOrder.setOnTouchListener((v, event) -> {
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                v.getBackground().setAlpha(150);
//            } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                v.getBackground().setAlpha(255);
//            }
//            return false;
//        });
//
//        //Validation after btnOrder is clicked
//        btnOrder.setOnClickListener(v -> {
//            //Initialiaze the string
//            pickupDateTime = etPickupDateTime.getText().toString().trim();
//            deliverDateTime = etDeliverDateTime.getText().toString().trim();
//            mobileNumber = etMobileNumber.getText().toString().trim();
//            customerName = etCustomerName.getText().toString().trim();
//            postalCode = etPostalCode.getText().toString().trim();
//            address = etAddress.getText().toString().trim();
//            unitNumberFirst = etUnitNumberFirst.getText().toString().trim();
//            unitNumberLast = etUnitNumberFirst.getText().toString().trim();
//            foodCost = etFoodCost.getText().toString().trim();
//            receiptNumber = etReceiptNumber.getText().toString().trim();
//
//
//            if(pickupDateTime.isEmpty() || deliverDateTime.isEmpty() || mobileNumber.isEmpty() || customerName.isEmpty() ||
//                    postalCode.isEmpty() || address.isEmpty() || unitNumberFirst.isEmpty() || unitNumberLast.isEmpty() || foodCost.isEmpty()){
//
//                Toast.makeText(PlaceOrderActivity.this, "Please fill-up all fields", Toast.LENGTH_SHORT).show();
//
//            }else{
//                Order clientOrder = new Order(loggedInClient, pickupDateTime, deliverDateTime, mobileNumber, customerName, postalCode,
//                        address, unitNumberFirst, unitNumberLast, foodCost, receiptNumber);
//
//                sendOrderToRider(clientOrder);
//            }
//        });
//
//        //Get address by postcode
//        etPostalCode.setFocusable(true);
//
//        //Get address by postcode
//        etPostalCode.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence pCode, int start, int before, int count) {
//                postalCode = etPostalCode.getText().toString().trim();
//
//                if( pCode.length() == maxLengthOfPostCode){
//                    getAddressByPostalCode(postalCode);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//    }
//
//    //Get address by postal code and check distance between outlet and deliver address
//    public void getAddressByPostalCode(String postalCode){
//        // Tag used to cancel the request
//        pDialog.setMessage("Please Wait....");
//        pDialog.setTitle("Proccessing");
//        pDialog.setCancelable(false);
//        showDialog();
//        getClosestDriver();
//    }
//
//    private int radius = 1;
//    private boolean driverFound = false;
//    GeoQuery geoQuery;
//    private void getClosestDriver() {
//        DatabaseReference driverLocation = FirebaseManager.getInstance().
//                getFirebaseDatabase().getReference().child("driversAvailable");
//
//        GeoFire geoFire = new GeoFire(driverLocation);
////        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude), radius);
//        geoQuery.removeAllListeners();
//
//        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
//            @Override
//            public void onKeyEntered(String key, GeoLocation location) {
////                if (!driverFound && requestBol){
////                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
////                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(DataSnapshot dataSnapshot) {
////                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
////                                Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
////                                if (driverFound){
////                                    return;
////                                }
////
////                                if(driverMap.get("service").equals(requestService)){
////                                    driverFound = true;
////                                    driverFoundID = dataSnapshot.getKey();
////
////                                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
////                                    HashMap map = new HashMap();
////                                    map.put("customerRideId", customerId);
////                                    map.put("destination", destination);
////                                    map.put("destinationLat", destinationLatLng.latitude);
////                                    map.put("destinationLng", destinationLatLng.longitude);
////                                    driverRef.updateChildren(map);
////
////                                    getDriverLocation();
////                                    getDriverInfo();
////                                    getHasRideEnded();
////                                    mRequest.setText("Looking for Driver Location....");
////                                }
////                            }
////                        }
////                        @Override
////                        public void onCancelled(DatabaseError databaseError) {
////                        }
////                    });
////                }
//            }
//
//            @Override
//            public void onKeyExited(String key) {
//
//            }
//
//            @Override
//            public void onKeyMoved(String key, GeoLocation location) {
//
//            }
//
//            @Override
//            public void onGeoQueryReady() {
//                if (!driverFound)
//                {
//                    radius++;
//                    getClosestDriver();
//                }
//            }
//
//            @Override
//            public void onGeoQueryError(DatabaseError error) {
//
//            }
//        });
//    }
//
//    public void sendOrderToRider(Order clientOrder){
//        pDialog.setMessage("Please Wait While Searching For Rider....");
//        pDialog.setTitle("Processing");
//        pDialog.setCancelable(false);
//        showDialog();
//        getClosestDriver();
//        hideDialog();
//    }
//
//
//
//    //Generate full address
//    public String  genrateAddress(String zipBuldingNo, String zipBuldingName, String zipStreetName, String zipCode,
//                                  String unitNoFirst, String unitNoLast){
//
//        String address = zipBuldingNo+" ";
//        String unitNumber = "";
//
//        if(!zipBuldingName.isEmpty()){
//            address = address + '('+zipBuldingName+')';
//        }
//
//        address = address + ", ";
//        address = address + zipStreetName +", ";
//
//        if(!unitNoFirst.isEmpty()){
//            unitNumber = unitNoFirst;
//        }
//
//        if(!unitNoLast.isEmpty()){
//            unitNumber = unitNumber+"-"+unitNoLast;
//        }
//
//        if(!unitNumber.isEmpty()){
//            address = address + '#' + unitNumber +",  ";
//        }
//
//        address = address + "Singapore-" + zipCode;
//        return address;
//    }
//
//    private void showDialog() {
//        if (!pDialog.isShowing())
//            pDialog.show();
//    }
//
//    private void hideDialog() {
//        if (pDialog.isShowing())
//            pDialog.dismiss();
//    }
}
