package com.example.geosaveapp.UtilityScreens;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.geosaveapp.R;
import com.example.geosaveapp.StoreScreen.StoreProfile;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ShapeableImageView profileImageView;
    private EditText nameEditText, phoneEditText, addressEditText;
    private Button updateButton;
    private Uri imageUri;
    private String userId;
    private FirebaseAuth mAuth;
    private FloatingActionButton fButton;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    if (imageUri != null) {
                        Log.d(TAG, "Selected image URI: " + imageUri.toString());
                        Glide.with(this).load(imageUri).into(profileImageView);
                    } else {
                        Toast.makeText(EditProfile.this, "Failed to get image URI", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        profileImageView = findViewById(R.id.imageViewProfile);
        nameEditText = findViewById(R.id.editTextName);
        phoneEditText = findViewById(R.id.editTextPhone2);
        addressEditText = findViewById(R.id.editTextAddress);
        updateButton = findViewById(R.id.updateButton);
        fButton = findViewById(R.id.imagefloatingbutton);

        loadUserProfile();

        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
    }

    private void requestStoragePermission() {
        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
    }

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openFileChooser();
                } else {
                    Toast.makeText(EditProfile.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private void openFileChooser() {
        ImagePicker.with(EditProfile.this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent(intent -> {
                    activityResultLauncher.launch(intent);
                    return null;
                });
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            db.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        nameEditText.setText(document.getString("Name"));
                        phoneEditText.setText(document.getString("phone"));
                        addressEditText.setText(document.getString("address"));
                        String imageUrl = document.getString("profilePic");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(EditProfile.this).load(imageUrl).into(profileImageView);
                        }
                    } else {
                        Log.d(TAG, "Failed to load user profile", task.getException());
                    }
                }
            });
        }
    }

    private void updateUserProfile() {
        final String name = nameEditText.getText().toString();
        final String phone = phoneEditText.getText().toString();
        final String address = addressEditText.getText().toString();

        if (imageUri != null) {
            StorageReference fileReference = storage.getReference("profile_pictures").child(userId + ".jpg");
            fileReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    String profileImageUrl = downloadUri.toString();

                                    // Load the new profile image into the profileImageView
                                    Glide.with(EditProfile.this).load(profileImageUrl).into(profileImageView);

                                    // Save the updated profile information to Firestore
                                    saveUserProfile(name, phone, address, profileImageUrl);
                                } else {
                                    Log.d(TAG, "Failed to get download URL", task.getException());
                                    Toast.makeText(EditProfile.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "Failed to upload profile image", task.getException());
                        Toast.makeText(EditProfile.this, "Failed to upload profile image", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            saveUserProfile(name, phone, address, null);
        }
    }

    private void saveUserProfile(String name, String phone, String address, @Nullable String profileImageUrl) {
        Map<String, Object> updates = new HashMap<>();
        if (name != null && !name.isEmpty()) {
            updates.put("Name", name);
        }
        if (phone != null && !phone.isEmpty()) {
            updates.put("phone", phone);
        }
        if (address != null && !address.isEmpty()) {
            updates.put("address", address);
        }
        if (profileImageUrl != null) {
            updates.put("profilePic", profileImageUrl);
        }

        db.collection("Users").document(userId).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    View rootLayout = findViewById(android.R.id.content);
                    Snackbar.make(rootLayout, "Profile  successfully updated", Snackbar.LENGTH_SHORT)
                            .addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(@NonNull Snackbar snackbar, int event) {
                                    super.onDismissed(snackbar, event);
                                    // Navigate back after Snackbar is dismissed
                                    finish(); // This will close the current activity and return to the previous one
                                }
                            })
                            .show();

                } else {
                    Log.d(TAG, "Failed to update user profile", task.getException());
                }
            }
        });
    }
}
