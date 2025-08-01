/**
 * File name: HomeFragment.java
 * Author: Derek Morales
 * Date: 2025-08-01
 * Description: Java class used in the Friends Forever Android app.
 */

package com.example.friendsforeverproject;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import android.content.Intent;

import java.util.*;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private MaterialButtonToggleGroup postFilterToggleGroup;
    private Set<String> friendIds = new HashSet<>();
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        recyclerView = view.findViewById(R.id.recyclerPostFeed);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        postFilterToggleGroup = view.findViewById(R.id.postFilterToggleGroup);
        postFilterToggleGroup.check(R.id.btnAllPosts); // default

        postFilterToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (checkedId == R.id.btnAllPosts) {
                loadAllPosts();
            } else if (checkedId == R.id.btnFriendsPosts) {
                loadFriendPosts();
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fabCreatePost);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
            startActivity(intent);
        });

        // Load initial state
        loadAllPosts();

        return view;
    }

    private void loadAllPosts() {
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Post post = doc.toObject(Post.class);
                        postList.add(post);
                    }
                    postAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show());
    }

    private void loadFriendPosts() {
        db.collection("users")
                .document(currentUserId)
                .collection("friends")
                .get()
                .addOnSuccessListener(friendDocs -> {
                    friendIds.clear();
                    for (DocumentSnapshot doc : friendDocs) {
                        friendIds.add(doc.getId());
                    }

                    db.collection("posts")
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .get()
                            .addOnSuccessListener(postDocs -> {
                                postList.clear();
                                for (DocumentSnapshot doc : postDocs) {
                                    Post post = doc.toObject(Post.class);
                                    if (friendIds.contains(post.getUserId())) {
                                        postList.add(post);
                                    }
                                }
                                postAdapter.notifyDataSetChanged();
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to load friends' posts", Toast.LENGTH_SHORT).show());
    }
}
