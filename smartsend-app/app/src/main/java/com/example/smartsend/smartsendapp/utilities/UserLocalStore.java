package com.example.smartsend.smartsendapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.smartsend.smartsendapp.utilities.app.Client;
import com.example.smartsend.smartsendapp.utilities.app.Rider;

/**
 * Created by AGM TAZIM on 12/29/2015.
 */
public class UserLocalStore {

    //Declaration
    public SharedPreferences userDB;
    public static final String userDBName = "userData";
    private static UserLocalStore instance;
    private static final Object userLocalStoreLock = new Object();
    //Constructor
    private UserLocalStore(Context ctx){
        userDB = ctx.getSharedPreferences(userDBName, 0);
    }

    public static UserLocalStore getInstance(Context ctx) {
        if (instance == null) {
            synchronized (userLocalStoreLock) {
                if (instance == null) {
                    instance = new UserLocalStore(ctx);
                }
            }
        }

        return instance;
    }

    public void clearData() {
        SharedPreferences.Editor riderSpEditor = userDB.edit();

        riderSpEditor.clear();
        riderSpEditor.commit();
    }

    //Store rider data in sharedpreference
    public void storeRiderData(Rider rider){
        SharedPreferences.Editor riderSpEditor = userDB.edit();
        riderSpEditor.putString("id", rider.getId());
        riderSpEditor.putString("status", rider.getStatus());
        riderSpEditor.putString("email", rider.getEmail());
        riderSpEditor.putString("password", rider.getPassword());
        riderSpEditor.putString("name", rider.getName());
        riderSpEditor.putString("bikeNumber", rider.getBikeNumber());
        riderSpEditor.putString("contactNumber", rider.getContactNumber());
        riderSpEditor.putString("createdDate", rider.getCreatedDate());
        riderSpEditor.putString("profilePicture", rider.getProfilePicture());
        riderSpEditor.commit();
    }

    //Store client  data in sharedpreference
    public void storeClientData(Client client){
        SharedPreferences.Editor clientSpEditor = userDB.edit();
        clientSpEditor.putString("id", client.getId());
        clientSpEditor.putString("companyName", client.getCompanyName());
        clientSpEditor.putString("email", client.getEmail());
        clientSpEditor.putString("password", client.getPassword());
        clientSpEditor.putString("companyPostalCode", client.getCompanyPostalCode());
        clientSpEditor.putString("companyUnitNumber", client.getCompanyUnitNumber());
        clientSpEditor.putString("location", client.getLocation());
        clientSpEditor.putString("contactNumber", client.getContactNumber());
        clientSpEditor.putString("billingAddress", client.getBillingAddress());
        clientSpEditor.putString("contactPersonName", client.getContactPersonName());
        clientSpEditor.putString("contactPersonNumber", client.getContactPersonNumber());
        clientSpEditor.putString("contactPersonEmail", client.getContactPersonEmail());
        clientSpEditor.putString("createdDate", client.getCreatedDate());
        clientSpEditor.putString("clientType", client.getClientType());
        clientSpEditor.commit();
    }

    //Get rider data
    public Rider getLoggedInRider(){
        String email = userDB.getString("email", "");
        String password = userDB.getString("password", "");
        String id = userDB.getString("id", "");
        String status = userDB.getString("status", "");
        String name = userDB.getString("name", "");
        String bikeNumber = userDB.getString("bikeNumber", "");
        String contactNumber = userDB.getString("contactNumber", "");
        String profilePicture = userDB.getString("profilePicture", "");
        String createdDate = userDB.getString("createdDate", "");

        Rider rider = new Rider(email, password);
        rider.setId(id);
        rider.setName(name);
        rider.setBikeNumber(bikeNumber);
        rider.setContactNumber(contactNumber);
        rider.setProfilePicture(profilePicture);
        rider.setStatus(status);
        rider.setBikeNumber(bikeNumber);

        return rider;
    }

    //Get client data
    public Client getLogedInClient (){
        String id = userDB.getString("id", "");
        String email = userDB.getString("email", "");
        String password = userDB.getString("password", "");
        String companyName = userDB.getString("companyName", "");
        String companyPostalCode = userDB.getString("companyPostalCode", "");
        String companyUnitNumber = userDB.getString("companyUnitNumber", "");
        String location = userDB.getString("location", "");
        String contactNumber = userDB.getString("contactNumber", "");
        String billingAddress = userDB.getString("billingAddress", "");
        String contactPersonName = userDB.getString("contactPersonName", "");
        String contactPersonNumber = userDB.getString("contactPersonNumber", "");
        String contactPersonEmail = userDB.getString("contactPersonEmail", "");
        String createdDate = userDB.getString("createdDate", "");
        String clientType = userDB.getString("clientType", "");

        //Create Client and put client data
        Client client = new Client();

        client.setId(id);
        client.setEmail(email);
        client.setPassword(password);
        client.setCompanyName(companyName);
        client.setCompanyPostalCode(companyPostalCode);
        client.setCompanyUnitNumber(companyUnitNumber);
        client.setLocation(location);
        client.setBillingAddress(billingAddress);
        client.setContactNumber(contactNumber);
        client.setContactPersonName(contactPersonName);
        client.setContactPersonEmail(contactPersonEmail);
        client.setContactPersonNumber(contactPersonNumber);
        client.setCreatedDate(createdDate);
        client.setClientType(clientType);

        return client;
    }

    //Set Login Rider
    public void setRiderLoggedIn(boolean loggedIn){
        SharedPreferences.Editor riderSpEditor = userDB.edit();
        riderSpEditor.putBoolean("loggedIn", loggedIn);
        riderSpEditor.putString("user", "rider");
        riderSpEditor.commit();
    }

    //Set Login Client
    public void setClientLoggedIn(boolean loggedIn){
        SharedPreferences.Editor clientSpEditor = userDB.edit();
        clientSpEditor.putBoolean("loggedIn", loggedIn);
        clientSpEditor.putString("user", "client");
        clientSpEditor.apply();
    }

    //get rider is logged in or not
    public boolean isRiderLoggedIn(){
        if(userDB.getBoolean("loggedIn", false)){
            return true;
        }else{
            return false;
        }
    }

    //get client is logged in or not
    public boolean isClientLoggedIn(){
        if(userDB.getBoolean("loggedIn", false)){
            return true;
        }else{
            return false;
        }
    }

    //Clear rider data
    public void clearRiderData(){
        SharedPreferences.Editor riderSpEditor = userDB.edit();
        riderSpEditor.clear();
        riderSpEditor.apply();
    }

    //Clear client data
    public void clearClientData(){
        SharedPreferences.Editor clientSpEditor = userDB.edit();
        clientSpEditor.clear();
        clientSpEditor.apply();
    }

    //Set Login Rider
    public String loggedInUser(){
        String user = userDB.getString("user", "");
        return  user;
    }
}
