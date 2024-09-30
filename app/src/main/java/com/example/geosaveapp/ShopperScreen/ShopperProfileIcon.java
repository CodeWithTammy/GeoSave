package com.example.geosaveapp.ShopperScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.geosaveapp.DeleteAccount;
import com.example.geosaveapp.LoginScreen.LoginPage;
import com.example.geosaveapp.R;
import com.example.geosaveapp.StoreScreen.AboutAccount;
import com.example.geosaveapp.StoreScreen.PersonalDetails;
import com.example.geosaveapp.UtilityScreens.EditProfile;
import com.example.geosaveapp.UtilityScreens.Settings;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ShopperProfileIcon extends Fragment {
    private ShapeableImageView profileImageView;
    private TextView shoppername, email,deletetxt;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseFirestore mFirestore;
    AppCompatButton logoutbtn, editprofilebtn;
    ImageButton settingbtn, personalbtn, aboutbtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_shopper_profile_icon, container, false);

        profileImageView = view.findViewById(R.id.profilepictureholder);
        shoppername = view.findViewById(R.id.shoppername);
        deletetxt = view.findViewById(R.id.deletetxt);
        email = view.findViewById(R.id.shopperemail);
        mAuth = FirebaseAuth.getInstance();
        logoutbtn = view.findViewById(R.id.logoutbtn);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();
        editprofilebtn = view.findViewById(R.id.edtprofilebtn);
        settingbtn = view.findViewById(R.id.setingbtn);
        personalbtn = view.findViewById(R.id.personalbtn);
        aboutbtn = view.findViewById(R.id.aboutbtn);

        logout();
        settingsButton();
        personalButton();
        profileEdit();
        aboutButton();
        deleteButton();

        return view;
    }
    private void deleteButton() {
        deletetxt.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), DeleteAccount.class);
            requireContext().startActivity(intent);
        });
    }
    private void aboutButton() {
        aboutbtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AboutAccount.class);
            requireContext().startActivity(intent);
        });
    }

    private void settingsButton() {
        settingbtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), Settings.class);
            requireActivity().startActivity(intent);
        });
    }

    private void personalButton() {
        personalbtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PersonalDetails.class);
            requireContext().startActivity(intent);
        });
    }

    private void profileEdit() {
        editprofilebtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditProfile.class);
            startActivity(intent);
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
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .into(profileImageView);
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
    @Override
    public void onStart() {
        super.onStart();
        loadProfileImage();
        loadUserName();
        loadUserEmail();
    }
    private void loadUserEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mFirestore.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String emailAdd = documentSnapshot.getString("UserEmail");
                            email.setText(emailAdd);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                    });
        }
    }
    private void loadUserName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mFirestore.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("Name");
                            shoppername.setText(userName);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                    });
        }
    }
    private void logout() {
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                // Optionally, navigate to the login screen or perform any other actions
                Intent intent = new Intent(requireContext(), LoginPage.class);
                requireContext().startActivity(intent);
                requireActivity().finish();

            }
        });
    }
}