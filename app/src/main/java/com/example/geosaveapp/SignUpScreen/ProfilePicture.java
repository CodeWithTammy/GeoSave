package com.example.geosaveapp.SignUpScreen;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.geosaveapp.R;
import com.example.geosaveapp.ShopperScreen.ShopperHomepage;
import com.example.geosaveapp.StoreScreen.StoreHomePage;
import com.example.geosaveapp.ShopperScreen.ShopperHomepage; // Make sure this import matches your actual package structure
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProfilePicture extends AppCompatActivity {

    private ShapeableImageView imgview;
    private FloatingActionButton fabbtn;
    private Uri selectedImageUri;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        imgview = findViewById(R.id.imageViewProfile);
        fabbtn = findViewById(R.id.imagefloatingbutton);
        Button nextbtn = findViewById(R.id.buttonNext);

        fabbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(ProfilePicture.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });

        findViewById(R.id.uploadbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToHomePage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imgview.setImageURI(selectedImageUri);
            }
        }
    }

    private void uploadImage() {
        if (selectedImageUri != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                StorageReference fileReference = mStorageRef.child("profile_pictures")
                        .child(userId + ".jpg");

                fileReference.putFile(selectedImageUri)
                        .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            saveImageUrlToFirestore(downloadUrl);
                            Toast.makeText(ProfilePicture.this, "Profile picture uploaded successfully", Toast.LENGTH_SHORT).show();
                        }))
                        .addOnFailureListener(e -> {
                            Toast.makeText(ProfilePicture.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            Toast.makeText(ProfilePicture.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageUrlToFirestore(String downloadUrl) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("profilePic", downloadUrl);

            db.collection("Users").document(userId).update(userProfile)
                    .addOnSuccessListener(aVoid -> Toast.makeText(ProfilePicture.this, "Profile image URL saved to Firestore", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ProfilePicture.this, "Failed to save profile image URL", Toast.LENGTH_SHORT).show());
        }
    }

    private void navigateToHomePage() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userType = documentSnapshot.getString("userType");
                            if (userType != null) {
                                if (userType.equals("isShopper")) {
                                    Intent intent = new Intent(ProfilePicture.this, ShopperHomepage.class);
                                    startActivity(intent);
                                } else if (userType.equals("isStore")) {
                                    Intent intent = new Intent(ProfilePicture.this, StoreHomePage.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(ProfilePicture.this, "User type is undefined", Toast.LENGTH_SHORT).show();
                                }
                                finish();
                            } else {
                                Toast.makeText(ProfilePicture.this, "User type is not set", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfilePicture.this, "User document does not exist", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProfilePicture.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
