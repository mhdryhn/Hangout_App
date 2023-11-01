package com.example.fyp;

import com.google.firebase.firestore.Exclude;

public class Note_member_ticket {
    private String name;
    private long number;
    private String email;

    private String number_string;

    private String gender;
    private String event_id;
    private int s_no;

    public Note_member_ticket(){
        //Public no-arg needed as Firestore will crash without it
    }

    public Note_member_ticket(String name, long number, String email, String gender,String event_id){
        this.name = name;
        this.number = number;
        this.email = email;
        this.gender = gender;
        this.event_id = event_id;
    }

    public Note_member_ticket(String name, String numberstring, int i){
        this.name = name;
        this.number_string = numberstring;
        s_no = i;
    }


    public String getEvent_id(){
        return event_id;
    }

    public void setEvent_id(String event_id){
        this.event_id = event_id;
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

    public String getNumber_string() {
        return number_string;
    }

    public void setNumber_string(String number_string) {
        this.number_string = number_string;
    }

    public int getS_no() {
        return s_no;
    }

    public void setS_no(int s_no) {
        this.s_no = s_no;
    }
}

