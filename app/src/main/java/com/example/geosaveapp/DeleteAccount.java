package com.example.geosaveapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.geosaveapp.StartingScreens.GetStarted;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteAccount extends AppCompatActivity {

    private static final String TAG = "DeleteUserDataActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mStorage;

    private Button deleteAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        deleteAccountButton = findViewById(R.id.deletebutton);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUserData();
            }
        });
    }

    private void deleteUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Delete profile picture from Firebase Storage (if exists)
            deleteProfilePicture(userId);

            // Delete user document from Firestore
            deleteFirestoreDocument(userId);

            // Delete user from Firebase Authentication
            deleteUserFromAuth(user);
        } else {
            // User is not authenticated or already signed out
            showSnackbar("User not authenticated");
        }
    }

    private void deleteProfilePicture(String userId) {
        // Assuming profile pictures are stored under "profile_pictures" folder in Firebase Storage
        StorageReference storageRef = mStorage.getReference().child("profile_pictures").child(userId + ".jpg");
        storageRef.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Profile picture deleted successfully");
                        } else {
                            Log.w(TAG, "Failed to delete profile picture", task.getException());
                        }
                    }
                });
    }

    private void deleteFirestoreDocument(String userId) {
        DocumentReference docRef = mFirestore.collection("Users").document(userId);
        docRef.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "DocumentSnapshot successfully deleted from Firestore!");
                        } else {
                            Log.w(TAG, "Error deleting document", task.getException());
                        }
                    }
                });
    }

    private void deleteUserFromAuth(FirebaseUser user) {
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted from Firebase Authentication.");
                            showSnackbar("Account successfully deleted");
                            // Navigate to GetStarted screen
                            startActivity(new Intent(DeleteAccount.this, GetStarted.class));
                            finish();
                        } else {
                            Log.w(TAG, "Error deleting user account from Firebase Authentication", task.getException());
                            showSnackbar("Failed to delete user account");
                        }
                    }
                });
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
