package com.example.friendsforeverproject;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String name;
    private String bio;
    private String profileImageUrl;  // ✅ NEW FIELD
    private List<String> interests;

    // ✅ Required empty constructor for Firestore
    public User() {}

    // ✅ Constructor without image
    public User(String userId, String name, String bio) {
        this.userId = userId;
        this.name = name;
        this.bio = bio;
        this.interests = new ArrayList<>();
    }

    // ✅ Constructor with image and interests
    public User(String userId, String name, String bio, String profileImageUrl, List<String> interests) {
        this.userId = userId;
        this.name = name;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.interests = interests;
    }


    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;  // ✅ Getter for adapter
    }

    public List<String> getInterests() {
        return interests;
    }
}
