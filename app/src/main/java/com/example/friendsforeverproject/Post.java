package com.example.friendsforeverproject;

import com.google.firebase.Timestamp;

public class Post {
    private String postId;
    private String userId;
    private String content;
    private String imageUrl;
    private Timestamp timestamp;

    public Post() {} // Required for Firestore

    public Post(String postId, String userId, String content, String imageUrl, Timestamp timestamp) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    // Getters
    public String getPostId() { return postId; }
    public String getUserId() { return userId; }
    public String getContent() { return content; }
    public String getImageUrl() { return imageUrl; }
    public Timestamp getTimestamp() { return timestamp; }

    // Setters
    public void setPostId(String postId) { this.postId = postId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setContent(String content) { this.content = content; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
