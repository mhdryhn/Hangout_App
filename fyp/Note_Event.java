package com.example.fyp;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

public class Note_Event {

    private String event_name;
    private int max_members;

    private String additional_info;
    private String date;
    private String time;

    private String category;

    private String imageurl;

    private long time_value;

    private int current_members;

    private String organizer_email;
    private String documentId;
    private GeoPoint location;

    public Note_Event(){
        //Public no-arg needed as Firestore will crash without it
    }

    public Note_Event(String event_name, int max_members, String additional_info, String time, String date, String organizer_email, long time_value, String category, String imageurl, GeoPoint location){
        this.event_name = event_name;
        this.max_members = max_members;
        this.time = time;
        this.date = date;
        this.additional_info = additional_info;
        this.organizer_email = organizer_email;
        this.time_value= time_value;
        current_members = 0;
        this.category = category;
        this.imageurl = imageurl;
        this.location = location;
    }
    @Exclude
    public String getDocumentId(){
        return documentId;
    }

    public void setDocumentId(String documentId){
        this.documentId = documentId;
    }

    public String getEvent_name(){
        return event_name;
    }

    public int getMax_members(){
        return max_members;
    }

    public String getDate(){
        return date;
    }

    public String getTime() { return time; }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAdditional_info() { return additional_info;}

    public String getOrganizer_email() {
        return organizer_email;
    }

    public void setMax_members(int max_members) {
        this.max_members = max_members;
    }

    public long getTime_value() {
        return time_value;
    }

    public int getCurrent_members() {
        return current_members;
    }

    public void setCurrent_members(int current_members){
        this.current_members = current_members;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

}
