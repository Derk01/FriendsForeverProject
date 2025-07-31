package com.example.friendsforeverproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;

import com.example.friendsforeverproject.utils.FirestoreService;

import java.util.*;

public class ProfileFragment extends Fragment {

    private TextView displayName, displayBio;
    private ChipGroup chipGroupInterests;
    private EditText editName, editBio, editInterests;
    private Button editProfileButton, saveButton, cancelButton, logoutButton, btnChangePhoto;
    private LinearLayout editButtonGroup;
    private ImageView profileImageView;

    private static final int PICK_IMAGE_REQUEST = 1001;
    private Uri imageUri;
    private String uploadedImageUrl = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        displayName = view.findViewById(R.id.displayName);
        displayBio = view.findViewById(R.id.displayBio);
        chipGroupInterests = view.findViewById(R.id.chipGroupInterests);
        editName = view.findViewById(R.id.editName);
        editBio = view.findViewById(R.id.editBio);
        editInterests = view.findViewById(R.id.editInterests);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        saveButton = view.findViewById(R.id.saveButton);
        cancelButton = view.findViewById(R.id.cancelButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        btnChangePhoto = view.findViewById(R.id.btnChangePhoto);
        editButtonGroup = view.findViewById(R.id.editButtonGroup);
        profileImageView = view.findViewById(R.id.profileImageView);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("profileImages");
        userId = mAuth.getCurrentUser().getUid();

        loadUserProfile();

        editProfileButton.setOnClickListener(v -> toggleEditMode(true));

        cancelButton.setOnClickListener(v -> toggleEditMode(false));

        btnChangePhoto.setOnClickListener(v -> openGallery());

        saveButton.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String bio = editBio.getText().toString().trim();
            String[] interests = editInterests.getText().toString().trim().split(",");

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("bio", bio);
            updates.put("interests", Arrays.asList(interests));
            if (uploadedImageUrl != null) {
                updates.put("profileImageUrl", uploadedImageUrl);
            }

            FirestoreService.updateUserProfile(updates, new FirestoreService.OnCompleteCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    toggleEditMode(false);
                    loadUserProfile();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        });

        return view;
    }

    private void toggleEditMode(boolean editable) {
        editName.setVisibility(editable ? View.VISIBLE : View.GONE);
        editBio.setVisibility(editable ? View.VISIBLE : View.GONE);
        editInterests.setVisibility(editable ? View.VISIBLE : View.GONE);
        editButtonGroup.setVisibility(editable ? View.VISIBLE : View.GONE);
        btnChangePhoto.setVisibility(editable ? View.VISIBLE : View.GONE);

        displayName.setVisibility(!editable ? View.VISIBLE : View.GONE);
        displayBio.setVisibility(!editable ? View.VISIBLE : View.GONE);
        chipGroupInterests.setVisibility(!editable ? View.VISIBLE : View.GONE);
        editProfileButton.setVisibility(!editable ? View.VISIBLE : View.GONE);
    }

    private void loadUserProfile() {
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String bio = documentSnapshot.getString("bio");
                List<String> interests = (List<String>) documentSnapshot.get("interests");
                String imageUrl = documentSnapshot.getString("profileImageUrl");

                displayName.setText(name);
                displayBio.setText(bio);
                editName.setText(name);
                editBio.setText(bio);
                editInterests.setText(String.join(",", interests != null ? interests : new ArrayList<>()));

                chipGroupInterests.removeAllViews();
                if (interests != null) {
                    for (String interest : interests) {
                        Chip chip = new Chip(getContext());
                        chip.setText(interest.trim());
                        chipGroupInterests.addView(chip);
                    }
                }

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this).load(imageUrl).into(profileImageView);
                }
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference fileRef = storageRef.child(userId + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> uploadedImageUrl = uri.toString()))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show());
        }
    }
}
