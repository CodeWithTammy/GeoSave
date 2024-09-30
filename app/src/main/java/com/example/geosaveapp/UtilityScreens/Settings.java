package com.example.geosaveapp.UtilityScreens;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.geosaveapp.PrivacyPolicy;
import com.example.geosaveapp.R;
import com.example.geosaveapp.StoreScreen.StoreProfile;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Settings extends AppCompatActivity {

    FirebaseAuth mAuth;
    StorageReference mStorageRef;
    FirebaseFirestore mFirestore;
    TextView username, email;
    ShapeableImageView profile;
    LinearLayout changepassword, privacypolicy;
    Switch switchnotification;
    ImageView backbtn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profile = findViewById(R.id.profilepictureholder);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        changepassword = findViewById(R.id.changepassword);
        switchnotification = findViewById(R.id.switch_notifications);
        privacypolicy = findViewById(R.id.privacypolicy);
        backbtn = findViewById(R.id.back_button);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, StoreProfile.class);
                startActivity(intent);

            }
        });

        changePassword();
        privacypolicy();
    }

    private void privacypolicy() {
        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, PrivacyPolicy.class);
                startActivity(intent);
            }
        });
    }

    private void changePassword() {
        changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this, ChangePassword.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        loadProfilePic();
        loadUsername();
        loadEmail();
    }

    private void loadEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!= null){
            String userId = user.getUid();
            mFirestore.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()){
                            String userEmail = documentSnapshot.getString("UserEmail");
                            email.setText(userEmail);
                        }
                    }).addOnFailureListener(e -> {

                    });
        }
    }

    private void loadUsername() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!= null){
            String userId = user.getUid();
            mFirestore.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()){
                            String userName = documentSnapshot.getString("Name");
                            username.setText(userName);
                        }
                    }).addOnFailureListener(e -> {

                    });
        }
    }

    private void loadProfilePic() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid(); // Use UID as it is unique
            mFirestore.collection("Users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String imageUrl = document.getString("profilePic");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(imageUrl)
                                    .into(profile);
                        } else {
                            // Handle case where profilePic is not set
                        }
                    } else {
                        // Handle case where document does not exist
                    }
                } else {
                    // Handle task failure
                }
            }).addOnFailureListener(e -> {
                // Handle any errors
            });
        }
    }


}