package com.example.geosaveapp.Components;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geosaveapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;

public class storeAdapter extends FirestoreRecyclerAdapter<storeModel, storeAdapter.StoreViewHolder> {

    private final Context context;
    private OnItemClickListener listener;

    public storeAdapter(@NonNull FirestoreRecyclerOptions<storeModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull StoreViewHolder holder, int position, @NonNull storeModel model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.storecardlayout, parent, false);
        return new StoreViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public class StoreViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView profilepictureholder;
        TextView titleTextView;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            profilepictureholder = itemView.findViewById(R.id.profilepictureholder);
            titleTextView = itemView.findViewById(R.id.storename);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }

        public void bind(storeModel model) {
            titleTextView.setText(model.getName());

            // Load profile picture using Glide
            if (model.getprofilepic() != null && !model.getprofilepic().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(model.getprofilepic())
                        .placeholder(R.drawable.defaultpfp)
                        .error(R.drawable.defaultpfp)
                        .into(profilepictureholder);
            } else {
                profilepictureholder.setImageResource(R.drawable.defaultpfp);
            }
        }
    }
}
