package com.example.fyp;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

public class Note_member {
    private String name;
    private long number;
    private String email;

    private String gender;
    private String documentId;

    private String profile_pic;


    private int age;



    private GeoPoint location  ;

    public Note_member(){
        //Public no-arg needed as Firestore will crash without it
    }

    public Note_member(String name, long number, String email, String gender, String profile_pic, int age,  GeoPoint location){
        this.name = name;
        this.number = number;
        this.email = email;
        this.gender = gender;
        this.profile_pic = profile_pic;
        this.location = location;
        this.age = age;
    }

    @Exclude
    public String getDocumentId(){
        return documentId;
    }

    public void setDocumentId(String documentId){
        this.documentId = documentId;
    }

    public String getName(){
        return name;
    }

    public long getNumber(){
        return number;
    }

    public String getEmail(){
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


}
