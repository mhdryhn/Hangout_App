package com.example.fyp;

import com.google.firebase.firestore.Exclude;

public class Note {
    private String name;
    private long number;
    private String email;
    private String documentId;

    private String profile_pic;

    public Note(){
        //Public no-arg needed as Firestore will crash without it
    }

    public Note(String name, long number, String email, String profile_pic){
        this.name = name;
        this.number = number;
        this.email = email;
        this.profile_pic = profile_pic;
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

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }
}
