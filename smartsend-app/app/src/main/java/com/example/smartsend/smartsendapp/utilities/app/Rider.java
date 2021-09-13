package com.example.smartsend.smartsendapp.utilities.app;

import java.io.Serializable;

/**
 * Created by AGM TAZIM on 12/29/2015.
 */
public class Rider implements Serializable {

    //Declaration
    private String id = null, status = null,
            email = null, password = null,
            first_name = null, last_name = null, vehicle_number = null,
            birth_date = null, contact_number = null, profile_picture = null,
            created_date = null;

    //Constructor
    public Rider(){

    }

    public Rider(String email, String password){
        this.email = email;
        this.password = password;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    //Set id
    public void setId(String id) {
        this.id = id;
    }

    //get id
    public String getId() {
        return this.id;
    }

    //Set Email
    public void setEmail(String email) {
        this.email = email;
    }

    //get email
    public String getEmail() {
        return this.email;
    }

    //Set password
    public void setPassword(String password){
        this.password = password;
    }

    //get passsword
    public String getPassword(){
        return this.password;
    }

    //Set name
    public void setFirst_name(String first_name){
        this.first_name = first_name;
    }

    //get name
    public String getFirst_name(){
        return this.first_name;
    }

    //Set biken umber
    public void setVehicle_number(String vehicle_number){
        this.vehicle_number = vehicle_number;
    }

    //get bike number
    public String getVehicle_number(){
        return this.vehicle_number;
    }

    //Set contact umber
    public void setContact_number(String contact_number){
        this.contact_number = contact_number;
    }

    //get contact number
    public String getContact_number(){
        return this.contact_number;
    }

    //Set profile picture
    public void setProfile_picture(String profile_picture){
        this.profile_picture = profile_picture;
    }

    //get profile picture
    public String getProfile_picture(){
        return this.profile_picture;
    }

    //Set created date
    public void setCreated_date(String created_date){
        this.created_date = created_date;
    }

    //get created Date
    public String getCreated_date(){
        return this.created_date;
    }

    //Set status
    public void setStatus(String status){
        this.status = status;
    }

    //get status
    public String getStatus(){
        return this.status;
    }

}
