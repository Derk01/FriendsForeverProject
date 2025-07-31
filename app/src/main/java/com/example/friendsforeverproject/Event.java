package com.example.friendsforeverproject;

import com.google.firebase.Timestamp;

public class Event {
    private String title;
    private String description;
    private Timestamp date;
    private String eventId; // ✅ Added field

    public Event() {
        // Needed for Firestore
    }

    public Event(String title, String description, Timestamp date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
