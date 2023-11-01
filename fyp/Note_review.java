package com.example.fyp;

public class Note_review {

    private String review_content;

    private float event_rating;

    private String event_id;

    private String user_name;

    public String organizer_email;

    public Note_review(String review_content, float event_rating, String event_id, String user_name, String organizer_email) {
        this.review_content = review_content;
        this.event_rating = event_rating;
        this.event_id = event_id;
        this.user_name = user_name;
        this.organizer_email = organizer_email;
    }

    public Note_review(String review_content, float event_rating, String event_id, String user_name) {
        this.review_content = review_content;
        this.event_rating = event_rating;
        this.event_id = event_id;
        this.user_name = user_name;
    }

    public Note_review() {
    }


    public String getReview_content() {
        return review_content;
    }

    public void setReview_content(String review_content) {
        this.review_content = review_content;
    }

    public float getEvent_rating() {
        return event_rating;
    }

    public void setEvent_rating(int event_rating) {
        this.event_rating = event_rating;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getOrganizer_email() {
        return organizer_email;
    }

    public void setOrganizer_email(String organizer_email) {
        this.organizer_email = organizer_email;
    }
}
