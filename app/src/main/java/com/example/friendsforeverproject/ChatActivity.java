package com.example.friendsforeverproject;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private EditText editMessage;
    private Button buttonSend;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String currentUserId;
    private String otherUserId;

    private List<Message> messageList = new ArrayList<>();
    private MessageAdapter adapter; // You'll build this in Step 3

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerChat = findViewById(R.id.recyclerChat);
        editMessage = findViewById(R.id.editMessage);
        buttonSend = findViewById(R.id.buttonSend);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        otherUserId = getIntent().getStringExtra("otherUserId");

        adapter = new MessageAdapter(this, messageList, currentUserId); // Build in Step 3
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(adapter);

        buttonSend.setOnClickListener(v -> sendMessage());

        listenForMessages();
    }

    private void sendMessage() {
        String text = editMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        String conversationId = getConversationId(currentUserId, otherUserId);
        Message message = new Message(currentUserId, otherUserId, text, Timestamp.now());

        db.collection("messages")
                .document(conversationId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(aVoid -> editMessage.setText(""));
    }

    private void listenForMessages() {
        String conversationId = getConversationId(currentUserId, otherUserId);
        db.collection("messages")
                .document(conversationId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        messageList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Message msg = doc.toObject(Message.class);
                            messageList.add(msg);
                        }
                        adapter.notifyDataSetChanged();
                        recyclerChat.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    private String getConversationId(String uid1, String uid2) {
        return uid1.compareTo(uid2) < 0 ? uid1 + "_" + uid2 : uid2 + "_" + uid1;
    }
}
