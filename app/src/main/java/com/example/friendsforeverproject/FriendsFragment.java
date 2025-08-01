/**
 * File name: FriendsFragment.java
 * Author: Derek Morales
 * Date: 2025-08-01
 * Description: Java class used in the Friends Forever Android app.
 */

package com.example.friendsforeverproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.*;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class FriendsFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendAdapter adapter;
    private List<User> friendList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button btnSuggested;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Toast.makeText(getContext(), "FriendsFragment loaded", Toast.LENGTH_SHORT).show();

        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerView = view.findViewById(R.id.recyclerFriendsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FriendAdapter(friendList, getContext());
        recyclerView.setAdapter(adapter);

        btnSuggested = view.findViewById(R.id.btnGoToSuggested);
        btnSuggested.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SuggestedFriendsActivity.class);
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        fetchFriendList();

        return view;
    }

    private void fetchFriendList() {
        friendList.clear();
        String currentUserId = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(currentUserId)
                .collection("friends")
                .get()
                .addOnSuccessListener(friendRefs -> {
                    for (DocumentSnapshot doc : friendRefs) {
                        String friendId = doc.getId();

                        db.collection("users")
                                .document(friendId)
                                .get()
                                .addOnSuccessListener(friendDoc -> {
                                    if (friendDoc.exists()) {
                                        String name = friendDoc.getString("name");
                                        String bio = friendDoc.getString("bio");
                                        String profileImageUrl = friendDoc.getString("profileImageUrl");
                                        List<String> interests = (List<String>) friendDoc.get("interests");

                                        if (interests == null) interests = new ArrayList<>();
                                        if (profileImageUrl == null) profileImageUrl = "";

                                        User user = new User(friendId, name, bio, profileImageUrl, interests);
                                        friendList.add(user);
                                        adapter.notifyDataSetChanged();
                                        Context context = getContext();
                                        if (context != null) {
                                            Toast.makeText(context, "Loaded: " + name, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load friends", Toast.LENGTH_SHORT).show();
                });
    }
}
