package com.example.geosaveapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.geosaveapp.Components.DealCardAdapter;
import com.example.geosaveapp.Components.DealModel;
import com.example.geosaveapp.Components.DisplayDealModel;
import com.example.geosaveapp.Components.DisplayDealsAdapter;
import com.example.geosaveapp.ShopperScreen.ShopperHomeIcon;
import com.example.geosaveapp.ShopperScreen.ShopperHomepage;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ShopperViewStoreDeals extends AppCompatActivity {

    private String storeId;
    private String storeName;
    private String profilePics;
    private FirebaseFirestore mFirestore;
    ImageView backbtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopper_view_store_deals);

        // Initialize views and set store details
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ShapeableImageView profilePicture = findViewById(R.id.profilepictureholder);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView storeNameTextView = findViewById(R.id.storename);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        RecyclerView dealsRecyclerView = findViewById(R.id.dealsRecyclerView);
        mFirestore = FirebaseFirestore.getInstance();
        backbtn = findViewById(R.id.backbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopperViewStoreDeals.this, ShopperHomepage.class);
                startActivity(intent);
                finish();

            }
        });

        // Retrieve store details from intent or savedInstanceState
        if (getIntent() != null) {
            storeId = getIntent().getStringExtra("storeId");
            storeName = getIntent().getStringExtra("storeName");
            profilePics = getIntent().getStringExtra("profilePic");
        }

        // Load profile picture using Glide
        Glide.with(this /* context */)
                .load(profilePics)
                .placeholder(R.drawable.defaultpfp) // Placeholder image while loading
                .error(R.drawable.defaultpfp) // Error image if loading fails
                .into(profilePicture);
        storeNameTextView.setText(storeName);

        // Set up RecyclerView for store deals
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        // Initialize RecyclerView
        RecyclerView dealsRecyclerView = findViewById(R.id.dealsRecyclerView);

        // Fetch deals for the store using storeId
        Query query = mFirestore.collection("Users")
                .document(storeId)
                .collection("storedeals");

        FirestoreRecyclerOptions<DisplayDealModel> options = new FirestoreRecyclerOptions.Builder<DisplayDealModel>()
                .setQuery(query, DisplayDealModel.class)
                .build();

        DisplayDealsAdapter adapter = new DisplayDealsAdapter(options, this);
        dealsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        dealsRecyclerView.setAdapter(adapter);

        adapter.startListening();
    }
}
