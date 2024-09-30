package com.example.geosaveapp.StoreScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.geosaveapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AboutAccount extends AppCompatActivity {

    TextView date;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    StorageReference mStorageRef;
    ShapeableImageView profileimage;
    ImageView backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_account);

        date = findViewById(R.id.date);
        profileimage = findViewById(R.id.profilepictureholder);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        backbtn = findViewById(R.id.backbtn);
        mFirestore = FirebaseFirestore.getInstance();

        // Retrieve the sign-up date from Firestore
        fetchSignUpDate();
        backButton();
    }
    @Override
    public void onStart() {
        super.onStart();
        loadProfileImage();
    }

    private void backButton() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(AboutAccount.this, StoreProfile.class);
                startActivity(intent);


            }
        });
    }



    private void loadProfileImage() {
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
                                    .into(profileimage);
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



    private void fetchSignUpDate() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("Users").document(userId);

            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Date signUpDate = documentSnapshot.getDate("signUpDate");
                        if (signUpDate != null) {
                            // Format the date to show only month and year
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                            String formattedDate = dateFormat.format(signUpDate);

                            // Set the formatted date to the TextView
                            date.setText(formattedDate);
                        } else {
                            date.setText("Sign-up date not available");
                        }
                    } else {
                        date.setText("User data not found");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    date.setText("Failed to retrieve sign-up date");
                }
            });
        } else {
            date.setText("User not signed in");
        }
    }
}
