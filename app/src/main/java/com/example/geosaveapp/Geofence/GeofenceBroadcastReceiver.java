package com.example.geosaveapp.Geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.geosaveapp.Components.DealCardAdapter;
import com.example.geosaveapp.Components.storeModel;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, "GeofencingEvent Error: " + errorMessage);
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            if (triggeringGeofences != null) {
                for (Geofence geofence : triggeringGeofences) {
                    String geofenceId = geofence.getRequestId();
                    fetchStoreNameAndNotify(context, geofenceId);
                    fetchStoreInfoAndShowNotification(context, geofenceId);
                }
            }
        }
    }

    private void fetchStoreNameAndNotify(Context context, String geofenceId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("geofences").document(geofenceId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String storeName = documentSnapshot.getString("storeName");
                        NotificationHelper notificationHelper = new NotificationHelper(context);
                        notificationHelper.showNotification(storeName, "You've entered " + storeName + "'s geofence", geofenceId);
                    } else {
                        Log.e(TAG, "Store document not found");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching store document", e));
    }
private void fetchStoreInfoAndShowNotification(Context context, String geofenceId) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("Users").document(geofenceId).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String storeName = documentSnapshot.getString("Name");
                    String profilePictureUrl = documentSnapshot.getString("profilePic");
                    String text = storeName + " is having a sale! Check out the latest deals.";

                    // Create store model and save to Firestore (if needed)
                    storeModel storeInfo = new storeModel(profilePictureUrl, storeName);
                    saveStoreInfoToFirestore(storeInfo);

                    // Trigger notification
                    NotificationService notificationService = new NotificationService(context);
                    notificationService.showNotification("GeoSave", text, geofenceId);

                } else {
                    Log.e("GeofenceBroadcastReceiver", "Store document not found for geofenceId: " + geofenceId);
                }
            })
            .addOnFailureListener(e -> {
                Log.e("GeofenceBroadcastReceiver", "Error fetching store document", e);
            });
}



    private void saveStoreInfoToFirestore(storeModel storeInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("Users").document(userId).collection("storedeals").add(storeInfo)
                    .addOnSuccessListener(documentReference -> Log.d("GeofenceBroadcastReceiver", "Store info saved successfully"))
                    .addOnFailureListener(e -> Log.e("GeofenceBroadcastReceiver", "Error saving store info", e));
        }
    }
}
