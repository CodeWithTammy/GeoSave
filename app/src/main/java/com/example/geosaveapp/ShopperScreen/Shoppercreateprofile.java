package com.example.geosaveapp.ShopperScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.geosaveapp.R;
import com.example.geosaveapp.SignUpScreen.ProfilePicture;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Shoppercreateprofile extends AppCompatActivity {

    EditText phone;
    CheckBox stationery, department, restaurant, supermarket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppercreateprofile);

        AppCompatButton button = findViewById(R.id.submitbtn);
        phone  = findViewById(R.id.phonenumber);
        stationery = findViewById(R.id.stationery);
        restaurant = findViewById(R.id.restaurant);
        supermarket = findViewById(R.id.Supermarket);
        department = findViewById(R.id.department);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get phone number input from EditText
                String phonenumber = phone.getText().toString().trim();

                // Validate checkbox selection
                if (!(stationery.isChecked() || department.isChecked() || restaurant.isChecked() || supermarket.isChecked())) {
                    showSnackbar(v, "Select Store Type");
                    return;
                }

                // Check if phone number is not empty
                if (!phonenumber.isEmpty()) {
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
                        data.put("phone", phonenumber);
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
                                        Intent intent = new Intent(Shoppercreateprofile.this, ProfilePicture.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Error occurred while adding data
                                        Toast.makeText(Shoppercreateprofile.this, "Error adding data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // User is not signed in
                        // Redirect to sign-in or registration activity
                        // Example: startActivity(new Intent(Shoppercreateprofile.this, SignInActivity.class));
                    }
                } else {
                    // Phone number input is empty
                    // Show error message or handle accordingly
                    Toast.makeText(Shoppercreateprofile.this, "Phone number cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }
}
