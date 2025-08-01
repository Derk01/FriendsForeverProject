/**
 * File name: FriendAdapter.java
 * Author: Derek Morales
 * Date: 2025-08-01
 * Description: Java class used in the Friends Forever Android app.
 */

package com.example.friendsforeverproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private List<User> friendList;
    private Context context;

    public FriendAdapter(List<User> friendList, Context context) {
        this.friendList = friendList;
        this.context = context;
    }

    @NonNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.ViewHolder holder, int position) {
        User friend = friendList.get(position);
        holder.nameTextView.setText(friend.getName());
        holder.bioTextView.setText(friend.getBio());

        String imageUrl = friend.getProfileImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(holder.profileImageView);
        } else {
            holder.profileImageView.setImageResource(R.drawable.ic_baseline_person_24);
        }

        // ✅ Open profile on click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("userId", friend.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImageView;
        TextView nameTextView, bioTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.friendImageView);
            nameTextView = itemView.findViewById(R.id.friendNameTextView);
            bioTextView = itemView.findViewById(R.id.friendBioTextView);
        }
    }
}
