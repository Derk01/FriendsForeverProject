package com.example.friendsforeverproject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;

import java.util.*;

public class CreatePostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 123;
    private EditText editPostContent;
    private ImageView imagePreview;
    private Button btnSelectImage, btnSubmitPost;
    private Uri imageUri = null;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        editPostContent = findViewById(R.id.editPostContent);
        imagePreview = findViewById(R.id.imagePreview);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        btnSelectImage.setOnClickListener(v -> openImagePicker());

        btnSubmitPost.setOnClickListener(v -> {
            String content = editPostContent.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "Post content cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri != null) {
                uploadImageAndPost(content);
            } else {
                savePostToFirestore(content, null);
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imagePreview.setVisibility(ImageView.VISIBLE);
            imagePreview.setImageURI(imageUri);
        }
    }

    private void uploadImageAndPost(String content) {
        String userId = mAuth.getCurrentUser().getUid();
        String filename = "posts/" + userId + "_" + System.currentTimeMillis();
        StorageReference imgRef = storageRef.child(filename);

        imgRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    savePostToFirestore(content, uri.toString());
                }))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show());
    }

    private void savePostToFirestore(String content, String imageUrl) {
        String userId = mAuth.getCurrentUser().getUid();
        String postId = db.collection("posts").document().getId();
        Timestamp timestamp = Timestamp.now();

        Map<String, Object> post = new HashMap<>();
        post.put("postId", postId);
        post.put("userId", userId);
        post.put("content", content);
        post.put("imageUrl", imageUrl != null ? imageUrl : "");
        post.put("timestamp", timestamp);

        db.collection("posts").document(postId).set(post)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Post created!", Toast.LENGTH_SHORT).show();
                    finish(); // go back to HomeFragment
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to create post", Toast.LENGTH_SHORT).show());
    }
}
