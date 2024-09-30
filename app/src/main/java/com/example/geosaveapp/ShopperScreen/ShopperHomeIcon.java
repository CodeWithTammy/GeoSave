package com.example.geosaveapp.ShopperScreen;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geosaveapp.Components.storeAdapter;
import com.example.geosaveapp.Components.storeModel;
import com.example.geosaveapp.Geofence.GeofenceHelper;
import com.example.geosaveapp.Geofence.NotificationHelper;
import com.example.geosaveapp.Geofence.NotificationService;
import com.example.geosaveapp.R;
import com.example.geosaveapp.ShopperViewStoreDeals;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ShopperHomeIcon extends Fragment {

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseFirestore mFirestore;
    private RecyclerView dealsRecyclerView;
    private storeAdapter adapter;
    private TextView name, noRecentPostTextView;
    private ImageView notfound;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private static final int REQUEST_LOCATION_PERMISSION = 123;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopper_home_icon, container, false);

        dealsRecyclerView = view.findViewById(R.id.storeRecyclerView);
        name = view.findViewById(R.id.name);
        mAuth = FirebaseAuth.getInstance();
        noRecentPostTextView = view.findViewById(R.id.no_recent_post);
        notfound = view.findViewById(R.id.notfound);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();
        geofencingClient = LocationServices.getGeofencingClient(getContext());
        geofenceHelper = new GeofenceHelper(getContext());

        fetchGeofences(); // Fetch and set up geofences

        return view;
    }


    private void setUpRecyclerView(String geofenceId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Query query = mFirestore.collection("Users")
                    .whereEqualTo(FieldPath.documentId(), geofenceId);

            FirestoreRecyclerOptions<storeModel> options = new FirestoreRecyclerOptions.Builder<storeModel>()
                    .setQuery(query, storeModel.class)
                    .build();

            adapter = new storeAdapter(options, requireContext());
            dealsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            dealsRecyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener((documentSnapshot, position) -> {
                String storeId = documentSnapshot.getId();
                String storeName = documentSnapshot.getString("Name"); // Replace with your actual field name
                String profilePic = documentSnapshot.getString("profilePic"); // Replace with your actual field name


                // Store located store deal as history
                storeDealAsHistory(storeId, storeName, profilePic);

                // Start new activity to display store details
                Intent intent = new Intent(requireContext(), ShopperViewStoreDeals.class);
                intent.putExtra("storeId", storeId);
                intent.putExtra("storeName", storeName);
                intent.putExtra("profilePic", profilePic);
                startActivity(intent);
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
        loadUserName();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void loadUserName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mFirestore.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("Name");
                            name.setText("Hi " + userName + "!");
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                    });
        }
    }

    private void fetchGeofenceId(String userId, OnGeofenceIdFetchedListener listener) {
        mFirestore.collection("geofences").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String geofenceId = documentSnapshot.getString("storeId");
                        listener.onGeofenceIdFetched(geofenceId);
                    } else {
                        listener.onGeofenceIdFetched(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("GeofenceID", "Error fetching geofence ID", e);
                    listener.onGeofenceIdFetched(null);
                });
    }

    interface OnGeofenceIdFetchedListener {
        void onGeofenceIdFetched(String geofenceId);
    }

    private void fetchGeofences() {
        mFirestore.collection("geofences").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Check if all required fields exist and are not null
                            if (document.contains("latitude") && document.contains("longitude")
                                    && document.contains("radius") && document.contains("transitionType")) {

                                // Fetch geofence details
                                double latitude = document.getDouble("latitude");
                                double longitude = document.getDouble("longitude");
                                float radius = document.getDouble("radius").floatValue();
                                int transitionType = document.getLong("transitionType").intValue();
                                String geofenceId = document.getId();

                                // Create geofence and geofencing request
                                LatLng location = new LatLng(latitude, longitude);
                                Geofence geofence = geofenceHelper.getGeofence(geofenceId, location, radius, transitionType);
                                GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
                                PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

                                // Add geofence
                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                                    return;
                                }

                                geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "Geofence added: " + geofenceId);

                                            // Fetch geofence ID and set up RecyclerView
                                            fetchGeofenceId(geofenceId, fetchedGeofenceId -> {
                                                if (fetchedGeofenceId != null && fetchedGeofenceId.equals(geofenceId)) {
                                                    setUpRecyclerView(fetchedGeofenceId);
                                                }
                                            });

                                        })
                                        .addOnFailureListener(e -> Log.e(TAG, "Failed to add geofence: " + geofenceId, e));

                            } else {
                                Log.e(TAG, "One or more geofence fields are null or missing in Firestore document: " + document.getId());
                            }
                        }
                    } else {
                        Log.e(TAG, "Error fetching geofences", task.getException());
                    }
                });
    }
    // Method to store located store deal as history
    private void storeDealAsHistory(String storeId, String storeName, String profilePic) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Map<String, Object> historyData = new HashMap<>();
            historyData.put("shopperId", currentUser.getUid());
            historyData.put("storeId", storeId);
            historyData.put("Name", storeName);
            historyData.put("profilePic", profilePic);
            historyData.put("timestamp", FieldValue.serverTimestamp());

            mFirestore.collection("ShopperHistory").document().set(historyData)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Stored store deal as history"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to store store deal as history", e));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchGeofences();
            } else {
                Toast.makeText(getContext(), "Location permission is required for geofencing", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
