package com.example.geosaveapp.StoreScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.geosaveapp.SignUpScreen.ProfilePicture;
import com.example.geosaveapp.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.SetOptions;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class StoreCreateProfile extends AppCompatActivity {

    EditText addressInput, phonenumber;
    CheckBox stationery, department, restaurant, supermarket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_create_profile);

        // Initialize EditText and CheckBoxes
        addressInput = findViewById(R.id.addressinput);
        phonenumber = findViewById(R.id.phonenumber);
        stationery = findViewById(R.id.stationery);
        restaurant = findViewById(R.id.restaurant);
        supermarket = findViewById(R.id.Supermarket);
        department = findViewById(R.id.department);

        // Set click listener for submit button
        AppCompatButton submitbtn = findViewById(R.id.submitbtn);
        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get address and phone number input from EditText
                String address = addressInput.getText().toString().trim();
                String phone = phonenumber.getText().toString().trim();

                // Validate checkbox selection
                if (!(stationery.isChecked() || department.isChecked() || restaurant.isChecked() || supermarket.isChecked())) {
                    showSnackbar(v, "Select Store Type");
                    return;
                }

                // Get current user
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    // Get user ID
                    String userId = currentUser.getUid();

                    // Access Firestore instance
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    // Create a new document reference for the user's profile
                    DocumentReference userRef = db.collection("Users").document(userId);

                    // Prepare data to update in Firestore
                    Map<String, Object> data = new HashMap<>();
                    data.put("address", address);
                    data.put("phone", phone);
                    data.put("stationery", stationery.isChecked());
                    data.put("department", department.isChecked());
                    data.put("restaurant", restaurant.isChecked());
                    data.put("supermarket", supermarket.isChecked());

                    // Update Firestore document by merging the new data with existing data
                    userRef.set(data, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Data added successfully
                                    // Navigate to the next activity
                                    Intent intent = new Intent(StoreCreateProfile.this, ProfilePicture.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Error occurred while adding data
                                    Toast.makeText(StoreCreateProfile.this, "Error adding data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // User is not signed in
                    // Redirect to sign-in or registration activity
                    // Example: startActivity(new Intent(StoreCreateProfile.this, SignInActivity.class));
                }
            }
        });
    }

    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}
