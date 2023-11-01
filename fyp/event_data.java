package com.example.fyp;

public class  event_data {

    String event_name;
    String date;
    String time;
    String members;
    String image_url;
    String doc_id;


    public event_data(String event_name, String date, String time, String image_url, String doc_id) {
        this.event_name = event_name;
        this.date = date;
        this.time = time;
        this.image_url = image_url;
        this.doc_id = doc_id;
    }

    public event_data(String event_name, String date, String time, String members, String image_url, String doc_id) {
        this.event_name = event_name;
        this.date = date;
        this.time = time;
        this.members = members;
        this.image_url = image_url;
        this.doc_id = doc_id;
    }


    public String getEvent_name() {
        return event_name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
    public String getMembers() {return members;}

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }


}
