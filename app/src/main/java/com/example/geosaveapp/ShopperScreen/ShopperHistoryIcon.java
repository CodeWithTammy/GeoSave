package com.example.geosaveapp.ShopperScreen;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geosaveapp.Components.storeAdapter;
import com.example.geosaveapp.Components.storeModel;
import com.example.geosaveapp.R;
import com.example.geosaveapp.ShopperViewStoreDeals;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ShopperHistoryIcon extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private RecyclerView dealsRecyclerView;
    private storeAdapter adapter;
    private TextView name, noRecentPostTextView;
    private ImageView notfound;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopper_history_icon, container, false);

        dealsRecyclerView = view.findViewById(R.id.addmarketingrecyclerview);
        name = view.findViewById(R.id.name);
        mAuth = FirebaseAuth.getInstance();
        noRecentPostTextView = view.findViewById(R.id.nodealstxt);
        notfound = view.findViewById(R.id.nodeals);
        mFirestore = FirebaseFirestore.getInstance();

        setUpRecyclerView();

        return view;
    }

    private void setUpRecyclerView() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Query query = mFirestore.collection("ShopperHistory");

            FirestoreRecyclerOptions<storeModel> options = new FirestoreRecyclerOptions.Builder<storeModel>()
                    .setQuery(query, storeModel.class)
                    .build();

            adapter = new storeAdapter(options, requireContext());
            dealsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            dealsRecyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new storeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                    String storeId = documentSnapshot.getId();
                    String storeName = documentSnapshot.getString("Name"); // Replace with your actual field name
                    String profilePic = documentSnapshot.getString("profilePic"); // Replace with your actual field name

                    // Start new activity to display store details

                }
            });

            // Start listening to Firestore changes
            adapter.startListening();

            // Optional: Register data observer to handle empty state
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    checkIfEmpty();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    checkIfEmpty();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    checkIfEmpty();
                }
            });

            // Check initial state
            checkIfEmpty();
        }
    }

    private void checkIfEmpty() {
        if (adapter != null && adapter.getItemCount() == 0) {
            noRecentPostTextView.setVisibility(View.VISIBLE);
            notfound.setVisibility(View.VISIBLE);
            dealsRecyclerView.setVisibility(View.GONE);
            Log.d(TAG, "Posts not available");
        } else {
            noRecentPostTextView.setVisibility(View.GONE);
            notfound.setVisibility(View.GONE);
            dealsRecyclerView.setVisibility(View.VISIBLE);
            Log.d(TAG, "Posts available, hiding 'no recent post' message.");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }


}
