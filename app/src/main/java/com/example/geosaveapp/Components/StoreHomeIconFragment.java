package com.example.geosaveapp.Components;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geosaveapp.Geofence.GeofenceHelper;
import com.example.geosaveapp.Geofence.NotificationHelper;
import com.example.geosaveapp.R;
import com.example.geosaveapp.Components.Utility;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StoreHomeIconFragment extends Fragment {

    private TextView textView, username, noRecentPostTextView;
    private ImageView notfound;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseFirestore mFirestore;
    RecyclerView duplicaterecyclerview;
    DisplayDealsAdapter dealCardAdapter;
    AppCompatButton geofencebtn, geofenceoff;
    private static final int REQUEST_LOCATION_PERMISSION = 123;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private boolean isGeofenceEnabled = false;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storehomeicon, container, false);

        textView = view.findViewById(R.id.textView);
        noRecentPostTextView = view.findViewById(R.id.no_recent_post);
        notfound = view.findViewById(R.id.notfound);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();
        duplicaterecyclerview = view.findViewById(R.id.dealsRecyclerView);
        geofencebtn = view.findViewById(R.id.geofencebtn);
        geofenceoff = view.findViewById(R.id.geofenceoffbtn);
        geofencingClient = LocationServices.getGeofencingClient(requireContext());
        geofenceHelper = new GeofenceHelper(requireContext());

        setUpRecyclerView();

        // Set geofence button click listener
        geofencebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGeofenceEnabled) {
                    disableGeofence();
                } else {
                    enableGeofence();
                }
            }
        });

        geofenceoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableGeofence();
            }
        });

        return view;
    }

    private void setUpRecyclerView() {
        Query query = Utility.getCollectionReferenceForDeals().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<DisplayDealModel> options = new FirestoreRecyclerOptions.Builder<DisplayDealModel>()
                .setQuery(query, DisplayDealModel.class).build();
        duplicaterecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        dealCardAdapter = new DisplayDealsAdapter(options, requireContext());
        duplicaterecyclerview.setAdapter(dealCardAdapter);

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
            noRecentPostTextView.setVisibility(View.VISIBLE);
            notfound.setVisibility(View.VISIBLE);
            duplicaterecyclerview.setVisibility(View.GONE);
            Log.d(TAG, "Posts not available");
        } else {
            noRecentPostTextView.setVisibility(View.GONE);
            notfound.setVisibility(View.GONE);
            duplicaterecyclerview.setVisibility(View.VISIBLE);
            Log.d(TAG, "Posts available, hiding 'no recent post' message.");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadUserName();
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

    private void loadUserName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mFirestore.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("Name");
                            textView.setText("Hi " + userName + "!");
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                    });
        }
    }

    private void enableGeofence() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mFirestore.collection("Users").document(userId).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                String address = documentSnapshot.getString("address");
                                if (address != null && !address.isEmpty()) {
                                    // Convert address to coordinates
                                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                                    try {
                                        List<Address> addresses = geocoder.getFromLocationName(address, 1);
                                        if (addresses != null && !addresses.isEmpty()) {
                                            Address location = addresses.get(0);
                                            LatLng storeLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                            float GEOFENCE_RADIUS = 200;

                                            // Create geofence
                                            Geofence geofence = geofenceHelper.getGeofence("GEOFENCE_ID", storeLocation, GEOFENCE_RADIUS,
                                                    Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
                                            GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
                                            PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

                                            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                return;
                                            }

                                            // Add geofence to the geofencing client
                                            geofencingClient.addGeofences(geofencingRequest, pendingIntent).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Save geofence details to Firestore
                                                        Map<String, Object> geofenceData = new HashMap<>();
                                                        geofenceData.put("storeId", userId);
                                                        geofenceData.put("storeName", documentSnapshot.getString("Name"));
                                                        geofenceData.put("profilepicurl", documentSnapshot.getString("profilePic"));
                                                        geofenceData.put("latitude", storeLocation.latitude);
                                                        geofenceData.put("longitude", storeLocation.longitude);
                                                        geofenceData.put("radius", GEOFENCE_RADIUS);
                                                        geofenceData.put("transitionType", Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);

                                                        mFirestore.collection("geofences").document(userId).set(geofenceData)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(requireContext(), "Geofence enabled and saved to Firestore", Toast.LENGTH_SHORT).show();
                                                                        NotificationHelper notificationHelper = new NotificationHelper(requireContext());
                                                                        notificationHelper.showNotification("Geofence", "Geofence is enabled at your store location.", userId);
                                                                        updateGeofenceButton(true);
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.e(TAG, "Failed to save geofence to Firestore", e);
                                                                        Toast.makeText(requireContext(), "Failed to save geofence to Firestore", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    } else {
                                                        String errorMessage = geofenceHelper.getErrorString(task.getException());
                                                        Log.e(TAG, "Geofence failed to add: " + errorMessage);
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(requireContext(), "Unable to find location for the address", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (IOException e) {
                                        Log.e(TAG, "Geocoding failed", e);
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "Address is empty", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Failed to get user address", e);
                        }
                    });
        }
    }

    private void disableGeofence() {
        geofencingClient.removeGeofences(geofenceHelper.getPendingIntent()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "Geofence disabled", Toast.LENGTH_SHORT).show();
                    mFirestore.collection("geofences").document(mAuth.getCurrentUser().getUid())
                            .delete()
                            .addOnSuccessListener(aVoid -> updateGeofenceButton(false))
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to delete geofence from Firestore", e));
                } else {
                    Log.e(TAG, "Failed to remove geofence: " + task.getException().getMessage());
                }
            }
        });
    }

    private void updateGeofenceButton(boolean isEnabled) {
        isGeofenceEnabled = isEnabled;
        if (isEnabled) {
            geofenceoff.setVisibility(View.VISIBLE);
            geofencebtn.setVisibility(View.GONE);

        } else {
            geofenceoff.setVisibility(View.GONE);
            geofencebtn.setVisibility(View.VISIBLE);
        }
    }
}
