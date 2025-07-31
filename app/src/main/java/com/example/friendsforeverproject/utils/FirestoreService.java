package com.example.friendsforeverproject.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Map;

public class FirestoreService {

    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();

    // ----------- USER ----------
    public static DocumentReference getCurrentUserRef() {
        return db.collection("users").document(auth.getUid());
    }

    public static CollectionReference getUsersCollection() {
        return db.collection("users");
    }

    public static void updateUserProfile(Map<String, Object> updates, OnCompleteCallback callback) {
        getCurrentUserRef().update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e));
    }

    // ----------- POSTS ----------
    public static CollectionReference getPostsCollection() {
        return db.collection("posts");
    }

    public static Query getAllPostsQuery() {
        return getPostsCollection().orderBy("timestamp", Query.Direction.DESCENDING);
    }

    public static Query getFriendsPostsQuery(String userId) {
        return getPostsCollection()
                .whereArrayContains("visibleTo", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING);
    }

    // ----------- EVENTS ----------
    public static CollectionReference getEventsCollection() {
        return db.collection("events");
    }

    public static CollectionReference getEventAttendees(String eventId) {
        return getEventsCollection().document(eventId).collection("attendees");
    }

    // ----------- MESSAGES ----------
    public static CollectionReference getMessageThread(String chatId) {
        return db.collection("messages").document(chatId).collection("messages");
    }

    // ----------- FRIENDS ----------
    public static CollectionReference getFriendsCollection(String userId) {
        return db.collection("users").document(userId).collection("friends");
    }

    public static CollectionReference getSuggestedFriends(String userId) {
        return db.collection("users").document(userId).collection("suggested");
    }

    // ----------- Callback Interface ----------
    public interface OnCompleteCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
