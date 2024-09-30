package com.example.geosaveapp.StartingScreens;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.geosaveapp.Admin.AdminScreen;
import com.example.geosaveapp.LoginScreen.LoginPage;
import com.example.geosaveapp.R;
import com.example.geosaveapp.ShopperScreen.ShopperHomepage;
import com.example.geosaveapp.SignUpScreen.SignupStore;
import com.example.geosaveapp.StoreScreen.StoreHomePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GetStarted extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is already signed in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, determine user type and navigate accordingly
            determineUserTypeAndNavigate(currentUser);
            return; // Return to prevent further execution of the onCreate method
        }

        // User is not signed in, continue with GetStarted activity setup
        setContentView(R.layout.activity_get_started);

        AppCompatButton login = findViewById(R.id.loginbutton);
        AppCompatButton signupbtn = findViewById(R.id.signupbtn);
        signupbtn.setOnClickListener(v -> {
            Intent intent = new Intent(GetStarted.this, SignupStore.class);
            startActivity(intent);
        });

        login.setOnClickListener(v -> {
            Intent intent = new Intent(GetStarted.this, LoginPage.class);
            startActivity(intent);
        });
    }

    private void determineUserTypeAndNavigate(FirebaseUser user) {
        String userId = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String userType = document.getString("userType");
                            Intent intent;
                            if ("isStore".equals(userType)) {
                                intent = new Intent(GetStarted.this, StoreHomePage.class);
                            } else if ("isShopper".equals(userType)) {
                                intent = new Intent(GetStarted.this, ShopperHomepage.class);
                            } else {
                                // Handle unexpected userType value or default to a specific type
                                // For example, default to ShopperHomepage if userType is undefined
                                intent = new Intent(GetStarted.this, ShopperHomepage.class);
                            }
                            startActivity(intent);
                            finish(); // Finish the GetStarted activity
                        } else {
                            // Check if the user is an admin if not found in Users collection
                            checkAdminAccessLevel();
                        }
                    } else {
                        // Handle task failure
                        // For example, show an error message
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    // For example, show an error message
                });
    }

    private void checkAdminAccessLevel() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Admin").document("admin").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Intent intent = new Intent(GetStarted.this, AdminScreen.class);
                            startActivity(intent);
                            finish(); // Finish the GetStarted activity
                        } else {
                            // Handle case where the document does not exist in Admin collection
                            // For example, show a message or redirect to a sign-up page
                        }
                    } else {
                        // Handle task failure
                        // For example, show an error message
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    // For example, show an error message
                });
    }
}
