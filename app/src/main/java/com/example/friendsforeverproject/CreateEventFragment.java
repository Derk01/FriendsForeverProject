/**
 * File name: CreateEventFragment.java
 * Author: Derek Morales
 * Date: 2025-08-01
 * Description: Fragment for creating a new event.
 */

package com.example.friendsforeverproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class CreateEventFragment extends Fragment {

    private EditText titleEditText, descriptionEditText;
    private Button dateButton, timeButton, createButton;
    private TextView selectedDateTime;

    private Calendar selectedDate;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public CreateEventFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);

        titleEditText = view.findViewById(R.id.titleEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        dateButton = view.findViewById(R.id.dateButton);
        timeButton = view.findViewById(R.id.timeButton);
        createButton = view.findViewById(R.id.createEventButton);
        selectedDateTime = view.findViewById(R.id.selectedDateTime);

        selectedDate = Calendar.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        dateButton.setOnClickListener(v -> showDatePicker());
        timeButton.setOnClickListener(v -> showTimePicker());
        createButton.setOnClickListener(v -> createEvent());

        return view;
    }

    private void showDatePicker() {
        DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTimeDisplay();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePicker = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDate.set(Calendar.MINUTE, minute);
                    updateDateTimeDisplay();
                },
                selectedDate.get(Calendar.HOUR_OF_DAY),
                selectedDate.get(Calendar.MINUTE),
                false);
        timePicker.show();
    }

    private void updateDateTimeDisplay() {
        String formatted = new SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
                .format(selectedDate.getTime());
        selectedDateTime.setText(formatted);
    }

    private void createEvent() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            titleEditText.setError("Title required");
            return;
        }

        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("description", description);
        event.put("date", new Timestamp(selectedDate.getTime()));
        event.put("creatorId", auth.getCurrentUser().getUid());
        event.put("timestamp", Timestamp.now());

        db.collection("events")
                .add(event)
                .addOnSuccessListener(docRef ->
                        Toast.makeText(getContext(), "Event created!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to create event.", Toast.LENGTH_SHORT).show());
    }
}
