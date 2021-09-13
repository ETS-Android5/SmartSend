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

    //Store client  data in sharedpreference
    public void storeClientData(Client client){
        SharedPreferences.Editor clientSpEditor = userDB.edit();
        clientSpEditor.putString("id", client.getId());
        clientSpEditor.putString("companyName", client.getCompany_name());
        clientSpEditor.putString("email", client.getEmail());
        clientSpEditor.putString("password", client.getPassword());
        clientSpEditor.putString("location", client.getLocation());
        clientSpEditor.putString("contactNumber", client.getContact_number());
        clientSpEditor.putString("billingAddress", client.getBilling_address());
        clientSpEditor.putString("contactPersonName", client.getContact_person_name());
        clientSpEditor.putString("contactPersonNumber", client.getContact_person_number());
        clientSpEditor.putString("contactPersonEmail", client.getContact_person_email());
        clientSpEditor.putString("createdDate", client.getCreated_date());
        clientSpEditor.commit();
    }

    public void storeRiderData(Rider rider){
        SharedPreferences.Editor riderSpEditor = userDB.edit();
        riderSpEditor.putString("id", rider.getId());
        riderSpEditor.putString("status", rider.getStatus());
        riderSpEditor.putString("email", rider.getEmail());
        riderSpEditor.putString("password", rider.getPassword());
        riderSpEditor.putString("birthDate", rider.getBirth_date());
        riderSpEditor.putString("firstName", rider.getFirst_name());
        riderSpEditor.putString("lastName", rider.getLast_name());
        riderSpEditor.putString("bikeNumber", rider.getVehicle_number());
        riderSpEditor.putString("contactNumber", rider.getContact_number());
        riderSpEditor.putString("createdDate", rider.getCreated_date());
        riderSpEditor.putString("profilePicture", rider.getProfile_picture());
        riderSpEditor.commit();
    }

    //Get rider data
    public Rider getLoggedInRider(){
        String email = userDB.getString("email", "");
        String password = userDB.getString("password", "");
        String id = userDB.getString("id", "");
        String status = userDB.getString("status", "");
        String firstName = userDB.getString("firstName", "");
        String lastName = userDB.getString("lastName", "");
        String birthDate = userDB.getString("birthDate", "");
        String bikeNumber = userDB.getString("bikeNumber", "");
        String contactNumber = userDB.getString("contactNumber", "");
        String profilePicture = userDB.getString("profilePicture", "");
        String createdDate = userDB.getString("createdDate", "");

        Rider rider = new Rider(email, password);
        rider.setId(id);
        rider.setVehicle_number(bikeNumber);
        rider.setBirth_date(birthDate);
        rider.setFirst_name(firstName);
        rider.setLast_name(lastName);
        rider.setCreated_date(createdDate);
        rider.setVehicle_number(bikeNumber);
        rider.setContact_number(contactNumber);
        rider.setProfile_picture(profilePicture);
        rider.setStatus(status);
        rider.setVehicle_number(bikeNumber);

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
        client.setCompany_name(companyName);
        client.setLocation(location);
        client.setBilling_address(billingAddress);
        client.setContact_number(contactNumber);
        client.setContact_person_name(contactPersonName);
        client.setContact_person_email(contactPersonEmail);
        client.setContact_person_number(contactPersonNumber);
        client.setCreated_date(createdDate);

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
