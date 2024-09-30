package com.example.geosaveapp.Admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geosaveapp.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.UserViewHolder> {

    private final List<BlockModel> users;
    private final Context context;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "AppPrefs";
    private static final String PREF_BLOCKED_PREFIX = "isBlocked_";

    public BlockAdapter(Context context, List<BlockModel> users) {
        this.context = context;
        this.users = users;
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blockrecyclerview, parent, false);
        db = FirebaseFirestore.getInstance();
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        BlockModel user = users.get(position);
        holder.textViewUserName.setText(user.getUserEmail());

        // Retrieve the button state from SharedPreferences
        boolean isBlocked = sharedPreferences.getBoolean(PREF_BLOCKED_PREFIX + user.getId(), user.isBlocked());
        holder.buttonBlock.setText(isBlocked ? "Unblock" : "Block");

        holder.buttonBlock.setOnClickListener(v -> {
            boolean newStatus = !isBlocked;
            user.setBlocked(newStatus);
            db.collection("Users").document(user.getId()).update("isBlocked", newStatus)
                    .addOnSuccessListener(aVoid -> {
                        // Save the new state to SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(PREF_BLOCKED_PREFIX + user.getId(), newStatus);
                        editor.apply();

                        // Update button text and notify adapter
                        holder.buttonBlock.setText(newStatus ? "Unblock" : "Block");
                        notifyItemChanged(position);

                        // Show Snackbar
                        Snackbar.make(v, newStatus ? "User blocked" : "User unblocked", Snackbar.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure and show Snackbar
                        Snackbar.make(v, "Failed to update status", Snackbar.LENGTH_SHORT).show();
                    });
        });

        // Load profile picture using Glide
        Glide.with(context).load(user.getProfilePic()).into(holder.profilePic);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        TextView textViewUserName;
        Button buttonBlock;
        ShapeableImageView profilePic;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.username);
            buttonBlock = itemView.findViewById(R.id.buttonBlock);
            profilePic = itemView.findViewById(R.id.profilepictureholder);
        }
    }
}