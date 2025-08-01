/**
 * File name: SuggestedFriendsAdapter.java
 * Author: Derek Morales
 * Date: 2025-08-01
 * Description: Java class used in the Friends Forever Android app.
 */

package com.example.friendsforeverproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.friendsforeverproject.utils.NotificationManager;

public class SuggestedFriendsAdapter extends RecyclerView.Adapter<SuggestedFriendsAdapter.ViewHolder> {

    private List<User> suggestedUsers;
    private Context context;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public SuggestedFriendsAdapter(List<User> suggestedUsers, Context context) {
        this.suggestedUsers = suggestedUsers;
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public SuggestedFriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_suggested_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestedFriendsAdapter.ViewHolder holder, int position) {
        User user = suggestedUsers.get(position);

        holder.tvName.setText(user.getName());
        holder.tvBio.setText(user.getBio());

        holder.btnAddFriend.setOnClickListener(v -> addFriend(user, holder.btnAddFriend));
    }

    private void addFriend(User friendUser, Button button) {
        String currentUserId = mAuth.getCurrentUser().getUid();
        String friendUserId = friendUser.getUserId();

        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", FieldValue.serverTimestamp());

        // Add friend to current user's friends subcollection
        db.collection("users")
                .document(currentUserId)
                .collection("friends")
                .document(friendUserId)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    // Optional: Also add current user to friend's friends list (mutual)
                    db.collection("users")
                            .document(friendUserId)
                            .collection("friends")
                            .document(currentUserId)
                            .set(data)
                            .addOnSuccessListener(inner -> {
                                NotificationManager.showToast(context, "Friend added!");
                                button.setEnabled(false);
                                button.setText("✓ Added");
                            })
                            .addOnFailureListener(e -> {
                                NotificationManager.showToast(context, "Friend added (one-way only)");
                            });
                })
                .addOnFailureListener(e -> {
                    NotificationManager.showToast(context, "Failed to add friend");
                });
    }


    @Override
    public int getItemCount() {
        return suggestedUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBio;
        Button btnAddFriend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFriendName);
            tvBio = itemView.findViewById(R.id.tvFriendBio);
            btnAddFriend = itemView.findViewById(R.id.btnAddFriend);
        }
    }
}
