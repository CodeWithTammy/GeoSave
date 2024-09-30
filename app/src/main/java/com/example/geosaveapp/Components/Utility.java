package com.example.geosaveapp.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {

    static void showToast(Context context, String message){
        Toast.makeText(context, message,Toast.LENGTH_SHORT).show();
    }

    public static CollectionReference getCollectionReferenceForDeals(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance().collection("Users")
                .document(currentUser.getUid()).collection("storedeals");
    }

    public static String timestampToString(Timestamp timestamp) {
        if (timestamp == null) {
            return ""; // or return a default date string if you prefer
        }
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    public static void getProfilePictureUrl(OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DocumentReference userDoc = FirebaseFirestore.getInstance().collection("Users").document(currentUser.getUid());
            userDoc.get().addOnCompleteListener(onCompleteListener);
        }
    }
}
