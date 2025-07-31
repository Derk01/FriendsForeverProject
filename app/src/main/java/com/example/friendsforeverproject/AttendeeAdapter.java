package com.example.friendsforeverproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.AttendeeViewHolder> {

    private List<String> attendeeList;

    public AttendeeAdapter(List<String> attendeeList) {
        this.attendeeList = attendeeList;
    }

    @NonNull
    @Override
    public AttendeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new AttendeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeViewHolder holder, int position) {
        String attendeeId = attendeeList.get(position);
        holder.textView.setText(attendeeId);
    }

    @Override
    public int getItemCount() {
        return attendeeList.size();
    }

    static class AttendeeViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public AttendeeViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
