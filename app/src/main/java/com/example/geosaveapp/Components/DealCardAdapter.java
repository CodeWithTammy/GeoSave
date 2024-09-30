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

public class DealCardAdapter extends FirestoreRecyclerAdapter<DealModel, DealCardAdapter.DealCardViewHolder> {

    private Context context;

    public DealCardAdapter(@NonNull FirestoreRecyclerOptions<DealModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull DealCardViewHolder holder, int position, @NonNull DealModel dealModel) {
        holder.storename.setText(dealModel.name);
        holder.desc.setText(dealModel.description);
        holder.timestampTextView.setText(Utility.timestampToString(dealModel.timestamp));

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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NewDealCard.class);
            intent.putExtra("title", dealModel.name);
            intent.putExtra("content", dealModel.description);
            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public DealCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dealrecyclerview, parent, false);
        return new DealCardViewHolder(view);
    }

    class DealCardViewHolder extends RecyclerView.ViewHolder {
        TextView storename, desc, timestampTextView;
        ShapeableImageView profilePic;  // Corrected variable name

        public DealCardViewHolder(@NonNull View itemView) {
            super(itemView);
            storename = itemView.findViewById(R.id.nameofstore);
            desc = itemView.findViewById(R.id.saledescription);
            timestampTextView = itemView.findViewById(R.id.note_timestamp_text_view);
            profilePic = itemView.findViewById(R.id.profilepictureholder);  // Corrected variable name
        }
    }
}
