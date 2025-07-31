package com.example.friendsforeverproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private TextView textName, textBio;
    private ChipGroup chipGroupInterests;
    private Button messageButton;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profileImageView = findViewById(R.id.profileImageView);
        textName = findViewById(R.id.textName);
        textBio = findViewById(R.id.textBio);
        chipGroupInterests = findViewById(R.id.chipGroupInterests);
        messageButton = findViewById(R.id.buttonMessage); // ✅ linked to layout button

        db = FirebaseFirestore.getInstance();

        String userId = getIntent().getStringExtra("userId");

        // ✅ Launch chat if message button clicked
        messageButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
            intent.putExtra("otherUserId", userId);
            startActivity(intent);
        });

        if (userId != null && !userId.isEmpty()) {
            loadUserProfile(userId);
        } else {
            Toast.makeText(this, "User ID not provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadUserProfile(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String name = snapshot.getString("name");
                        String bio = snapshot.getString("bio");
                        String profileImageUrl = snapshot.getString("profileImageUrl");
                        List<String> interests = (List<String>) snapshot.get("interests");

                        textName.setText(name != null ? name : "Unknown");
                        textBio.setText(bio != null ? bio : "");

                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(this).load(profileImageUrl).into(profileImageView);
                        }

                        chipGroupInterests.removeAllViews();
                        if (interests != null) {
                            for (String interest : interests) {
                                Chip chip = new Chip(this);
                                chip.setText(interest);
                                chip.setClickable(false);
                                chip.setCheckable(false);
                                chipGroupInterests.addView(chip);
                            }
                        }
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}
