/**
 * File name: EventAdapter.java
 * Author: Derek Morales
 * Date: 2025-08-01
 * Description: Adapter for displaying events in a RecyclerView.
 */

package com.example.friendsforeverproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private ArrayList<Event> eventList;
    private Context context;

    public EventAdapter(ArrayList<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        String eventId = event.getEventId();

        holder.titleTextView.setText(event.getTitle());
        holder.descriptionTextView.setText(event.getDescription());

        Timestamp ts = event.getDate();
        if (ts != null) {
            String formattedDate = new SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
                    .format(ts.toDate());
            holder.dateTextView.setText(formattedDate);
        } else {
            holder.dateTextView.setText("Date not set");
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        // Load RSVP count
        db.collection("events")
                .document(eventId)
                .collection("attendees")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int count = querySnapshot.size();
                    holder.rsvpCountTextView.setText(count + " going");
                })
                .addOnFailureListener(e -> {
                    holder.rsvpCountTextView.setText("0 going");
                });

        // RSVP button logic
        db.collection("events")
                .document(eventId)
                .collection("attendees")
                .document(userId)
                .get()
                .addOnSuccessListener(attendeeDoc -> {
                    if (attendeeDoc.exists()) {
                        holder.rsvpButton.setText("✔ RSVP'd");
                        holder.rsvpButton.setEnabled(false);
                    } else {
                        holder.rsvpButton.setText("RSVP");
                        holder.rsvpButton.setEnabled(true);

                        holder.rsvpButton.setOnClickListener(v -> {
                            db.collection("events")
                                    .document(eventId)
                                    .collection("attendees")
                                    .document(userId)
                                    .set(new HashMap<>())
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "RSVP successful", Toast.LENGTH_SHORT).show();
                                        holder.rsvpButton.setText("✔ RSVP'd");
                                        holder.rsvpButton.setEnabled(false);
                                        // Optionally update RSVP count after RSVP
                                        db.collection("events")
                                                .document(eventId)
                                                .collection("attendees")
                                                .get()
                                                .addOnSuccessListener(qs -> {
                                                    int updatedCount = qs.size();
                                                    holder.rsvpCountTextView.setText(updatedCount + " going");
                                                });
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(context, "RSVP failed", Toast.LENGTH_SHORT).show()
                                    );
                        });
                    }
                });

        // View Attendees button logic
        holder.viewAttendeesButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AttendeesActivity.class);
            intent.putExtra("eventId", eventId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, dateTextView, rsvpCountTextView;
        Button rsvpButton, viewAttendeesButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.eventTitle);
            descriptionTextView = itemView.findViewById(R.id.eventDescription);
            dateTextView = itemView.findViewById(R.id.eventDate);
            rsvpButton = itemView.findViewById(R.id.rsvpButton);
            viewAttendeesButton = itemView.findViewById(R.id.viewAttendeesButton);
            rsvpCountTextView = itemView.findViewById(R.id.rsvpCountTextView);
        }
    }
}
