/**
 * File name: PostAdapter.java
 * Author: Derek Morales
 * Date: 2025-08-01
 * Description: Java class used in the Friends Forever Android app.
 */

package com.example.friendsforeverproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private Context context;
    private List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.contentTextView.setText(post.getContent());

        Timestamp timestamp = post.getTimestamp();
        if (timestamp != null) {
            String formattedTime = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
                    .format(timestamp.toDate());
            holder.timestampTextView.setText(formattedTime);
        }

        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(post.getImageUrl())
                    .into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView contentTextView, timestampTextView;
        ImageView imageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.textPostContent);
            timestampTextView = itemView.findViewById(R.id.textPostTimestamp);
            imageView = itemView.findViewById(R.id.imagePost);
        }
    }
}
