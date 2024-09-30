package com.example.geosaveapp.Components;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.geosaveapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

public class storeaddicon extends Fragment {

    FloatingActionButton fab;
    RecyclerView recyclerView;
    DealCardAdapter dealCardAdapter;
    TextView nodealstxt;
    ImageView nodeals;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_storeaddicon, container, false);
        fab = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.addmarketingrecyclerview);
        nodeals = view.findViewById(R.id.nodeals);
        nodealstxt = view.findViewById(R.id.nodealstxt);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), NewDealCard.class);
                requireContext().startActivity(intent); // Use requireContext().startActivity(intent) to start the activity


            }
        });
        setUpRecyclerView();
        return view;

    }

    private void setUpRecyclerView() {
        Query query = Utility.getCollectionReferenceForDeals().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<DealModel> options = new FirestoreRecyclerOptions.Builder<DealModel>()
                .setQuery(query,DealModel.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        dealCardAdapter = new DealCardAdapter(options, requireContext());
        recyclerView.setAdapter(dealCardAdapter);

        dealCardAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
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

        // Initial check to see if the adapter is empty or not
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        if (dealCardAdapter == null || dealCardAdapter.getItemCount() == 0) {
            nodealstxt.setVisibility(View.VISIBLE);
            nodeals.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            Log.d(TAG, "Posts not available");
        } else {
            nodealstxt.setVisibility(View.GONE);
            nodeals.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            Log.d(TAG, "Posts available, hiding 'no recent post' message.");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        dealCardAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        dealCardAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        dealCardAdapter.notifyDataSetChanged();
    }
}