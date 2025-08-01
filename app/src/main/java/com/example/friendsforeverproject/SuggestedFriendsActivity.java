/**
 * File name: SuggestedFriendsActivity.java
 * Author: Derek Morales
 * Date: 2025-08-01
 * Description: Java class used in the Friends Forever Android app.
 */

package com.example.friendsforeverproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class SuggestedFriendsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private RecyclerView recyclerView;
    private SuggestedFriendsAdapter adapter;
    private List<User> suggestedUsers = new ArrayList<>();
    private ProgressBar progressBar;

    private String currentUserId;
    private List<String> currentUserInterests = new ArrayList<>();
    private List<String> currentUserFriends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_friends);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerSuggestedFriends);
        progressBar = findViewById(R.id.progressBarSuggested);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SuggestedFriendsAdapter(suggestedUsers, this);
        recyclerView.setAdapter(adapter);

        currentUserId = mAuth.getCurrentUser().getUid();
        fetchCurrentUser();
    }

    private void fetchCurrentUser() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("users").document(currentUserId).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        currentUserInterests = (List<String>) document.get("interests");
                        currentUserFriends = (List<String>) document.get("friends");
                        if (currentUserFriends == null) currentUserFriends = new ArrayList<>();
                        fetchSuggestedFriends();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void fetchSuggestedFriends() {
        db.collection("users").get()
                .addOnSuccessListener(querySnapshot -> {
                    suggestedUsers.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        String uid = doc.getId();
                        if (uid.equals(currentUserId) || currentUserFriends.contains(uid)) {
                            continue;
                        }

                        List<String> theirInterests = (List<String>) doc.get("interests");
                        if (theirInterests != null && !Collections.disjoint(currentUserInterests, theirInterests)) {
                            String name = doc.getString("name");
                            String bio = doc.getString("bio");
                            suggestedUsers.add(new User(uid, name, bio, null, theirInterests));
                        }
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch suggestions", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }
}
