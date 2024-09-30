package com.example.geosaveapp.LoginScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.geosaveapp.Admin.AdminScreen;
import com.example.geosaveapp.R;
import com.example.geosaveapp.ShopperScreen.ShopperHomepage;
import com.example.geosaveapp.SignUpScreen.SignupStore;
import com.example.geosaveapp.StoreScreen.StoreHomePage;
import com.example.geosaveapp.StartingScreens.GetStarted;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.snackbar.Snackbar;

public class LoginPage extends AppCompatActivity {

    private EditText email, password;
    private AppCompatButton loginBtn;
    private ImageButton backbtn;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private TextView forgotPassword, signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        initializeViews();
        initializeFirebase();

        loginBtn.setOnClickListener(v -> {
            if (validateFields()) {
                fAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(authResult -> checkUserStatus(authResult.getUser().getUid()))
                        .addOnFailureListener(e -> showSnackbar("Login failed: " + e.getMessage()));
            }
        });

        signup.setOnClickListener(v -> navigateTo(SignupStore.class));
        backbtn.setOnClickListener(v -> navigateTo(GetStarted.class));
        forgotPassword.setOnClickListener(v -> navigateTo(ForgotPassword.class));
    }

    private void initializeViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginpagebtn);
        backbtn = findViewById(R.id.backbtn);
        forgotPassword = findViewById(R.id.forgotpassword);
        signup = findViewById(R.id.signuptxt);
    }

    private void initializeFirebase() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }

    private boolean validateFields() {
        boolean valid = true;

        if (email.getText().toString().isEmpty()) {
            email.setError("Email is required");
            valid = false;
        }

        if (password.getText().toString().isEmpty()) {
            password.setError("Password is required");
            valid = false;
        }

        return valid;
    }

    private void checkUserStatus(String uid) {
        // Check if the user is in the Users collection first
        fStore.collection("Users").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Boolean isBlocked = document.getBoolean("isBlocked");
                    if (isBlocked != null && isBlocked) {
                        handleBlockedUser();
                    } else {
                        checkUserAccessLevel(uid);
                    }
                } else {
                    // User not found in Users collection, check if they are an admin
                    checkAdminAccessLevel();
                }
            } else {
                showSnackbar("Failed to fetch user data: " + task.getException().getMessage());
            }
        });
    }

    private void checkUserAccessLevel(String uid) {
        fStore.collection("Users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String userType = documentSnapshot.getString("userType");
                        if (userType != null) {
                            switch (userType) {
                                case "isStore":
                                    navigateTo(StoreHomePage.class);
                                    break;
                                case "isShopper":
                                    navigateTo(ShopperHomepage.class);
                                    break;
                                default:
                                    checkAdminAccessLevel();
                                    showSnackbar("User type is not recognized");
                                    break;
                            }
                        } else {
                            showSnackbar("User type field not found");
                        }
                    } else {
                        checkAdminAccessLevel();
                        showSnackbar("User document not found");
                    }
                })
                .addOnFailureListener(e -> showSnackbar("Failed to get user data: " + e.getMessage()));
    }

    private void checkAdminAccessLevel() {
        fStore.collection("Admin").document("admin").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Admin found, navigate to AdminScreen
                            navigateTo(AdminScreen.class);
                        } else {
                            showSnackbar("Failed Login");
                        }
                    } else {
                        showSnackbar("Failed to get admin data: " + task.getException().getMessage());
                    }
                });
    }

    private void handleBlockedUser() {
        fAuth.signOut();
        new AlertDialog.Builder(this)
                .setTitle("Account Blocked")
                .setMessage("Your account has been blocked because of a violation in the terms and conditions. Please contact support @geosaveapp@gmail.com for further assistance.")
                .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void navigateTo(Class<?> destinationClass) {
        startActivity(new Intent(LoginPage.this, destinationClass));
        finish();
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (fAuth.getCurrentUser() != null) {
            checkUserStatus(fAuth.getCurrentUser().getUid());
        }
    }
}
