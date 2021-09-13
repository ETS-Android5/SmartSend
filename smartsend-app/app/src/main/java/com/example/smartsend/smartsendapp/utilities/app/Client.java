package com.example.smartsend.smartsendapp.utilities.app;

import com.example.smartsend.smartsendapp.R;

/**
 * Created by AGM TAZIM on 12/31/2015.
 */
public class Client {
    private String id = null, email = null, password = null, company_name = null,
            location = null, contact_number = null, billing_address = null,
            contact_person_name = null, contact_person_number = null, contact_person_email = null,
            created_date =null, profilePicture;

    //Consrtuctor
    public Client(){
        profilePicture = String.valueOf(R.drawable.logo);
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    //Set id
    public void setId(String id) {
        this.id = id;
    }

    //Get id
    public String getId() {
        return id;
    }

    //Set password
    public void setPassword(String password) {
        this.password = password;
    }

    //Get password
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(String billing_address) {
        this.billing_address = billing_address;
    }

    public String getContact_person_name() {
        return contact_person_name;
    }

    public void setContact_person_name(String contact_person_name) {
        this.contact_person_name = contact_person_name;
    }

    public String getContact_person_number() {
        return contact_person_number;
    }

    public void setContact_person_number(String contact_person_number) {
        this.contact_person_number = contact_person_number;
    }

    public String getContact_person_email() {
        return contact_person_email;
    }

    public void setContact_person_email(String contact_person_email) {
        this.contact_person_email = contact_person_email;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

}//End of Client Class
