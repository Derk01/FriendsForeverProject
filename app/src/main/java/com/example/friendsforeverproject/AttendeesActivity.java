package com.example.friendsforeverproject;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AttendeesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendeeAdapter attendeeAdapter;
    private ArrayList<String> attendeeList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendees);

        recyclerView = findViewById(R.id.attendeesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        attendeeList = new ArrayList<>();
        attendeeAdapter = new AttendeeAdapter(attendeeList);
        recyclerView.setAdapter(attendeeAdapter);

        db = FirebaseFirestore.getInstance();

        // Get event ID passed from EventAdapter
        String eventId = getIntent().getStringExtra("eventId");

        if (eventId == null) {
            Toast.makeText(this, "Event ID missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadAttendees(eventId);
        System.out.println("Event ID received: " + eventId);
    }

    private void loadAttendees(String eventId) {
        Toast.makeText(this, "Loading attendees for event: " + eventId, Toast.LENGTH_SHORT).show();

        db.collection("events")
                .document(eventId)
                .collection("attendees")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    attendeeList.clear();
                    if (querySnapshot.isEmpty()) {
                        Toast.makeText(this, "No attendees found", Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            String userId = doc.getId();

                            db.collection("users")
                                    .document(userId)
                                    .get()
                                    .addOnSuccessListener(userDoc -> {
                                        if (userDoc.exists()) {
                                            String name = userDoc.getString("name");
                                            if (name != null) {
                                                attendeeList.add(name);
                                            } else {
                                                attendeeList.add("User: " + userId);
                                            }
                                        } else {
                                            attendeeList.add("User: " + userId);
                                        }

                                        attendeeAdapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        attendeeList.add("Error loading user: " + userId);
                                        attendeeAdapter.notifyDataSetChanged();
                                    });
                        }
                    }
                    attendeeAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load attendees: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }


}

