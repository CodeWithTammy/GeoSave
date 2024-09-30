package com.example.geosaveapp.Components;

import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class DisplayDealsAdapter extends FirestoreRecyclerAdapter<DisplayDealModel, DisplayDealsAdapter.DisplayDealViewHolder> {

    private Context context;

    public DisplayDealsAdapter(@NonNull FirestoreRecyclerOptions<DisplayDealModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull DisplayDealViewHolder holder, int position, @NonNull DisplayDealModel displayDealModel) {
        holder.storename.setText(displayDealModel.getName());
        holder.desc.setText(displayDealModel.getDescription());
        Timestamp timestamp = displayDealModel.getTimestamp();
        holder.timestampTextView.setText(Utility.timestampToString(timestamp));

        Utility.getProfilePictureUrl(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String profilePicUrl = document.getString("profilePic");
                    if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                        Glide.with(context).load(profilePicUrl).into(holder.profilePic);
                    } else {
                        // Set a placeholder image if profilePic is null or empty
                        holder.profilePic.setImageResource(R.drawable.defaultpfp);
                    }
                }
            } else {
                // Set a placeholder image if task fails
                holder.profilePic.setImageResource(R.drawable.defaultpfp);
            }
        });

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(context, NewDealCard.class);
            intent.putExtra("title", displayDealModel.getName());
            intent.putExtra("content", displayDealModel.getDescription());
            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public DisplayDealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dealrecyclerview, parent, false);
        return new DisplayDealViewHolder(view);
    }

    class DisplayDealViewHolder extends RecyclerView.ViewHolder {

        TextView storename, desc, timestampTextView;
        ShapeableImageView profilePic;

        public DisplayDealViewHolder(@NonNull View itemView) {
            super(itemView);
            storename = itemView.findViewById(R.id.nameofstore);
            desc = itemView.findViewById(R.id.saledescription);
            timestampTextView = itemView.findViewById(R.id.note_timestamp_text_view);
            profilePic = itemView.findViewById(R.id.profilepictureholder);
        }
    }
}
