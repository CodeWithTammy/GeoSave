package com.example.geosaveapp.StoreScreen;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.geosaveapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class PersonalDetails extends AppCompatActivity {

    TextView email, number, addresstxt, addresstitle;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    ImageView backbtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        email = findViewById(R.id.email);
        addresstitle = findViewById(R.id.address);
        number = findViewById(R.id.number);
        addresstxt = findViewById(R.id.addresstxt);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        backbtn = findViewById(R.id.backbtn);

        backButton();
        loadPhoneNumber();
        loadAddress();
    }

    private void backButton() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(PersonalDetails.this, StoreProfile.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadEmail();
    }

    private void loadEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            firestore.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String useremail = documentSnapshot.getString("UserEmail");
                            email.setText(useremail);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                    });
        }
    }

    private void loadAddress(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            firestore.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String address = documentSnapshot.getString("address");
                            if (address != null && !address.isEmpty()) {
                                addresstxt.setText(address);
                            } else {
                                addresstxt.setVisibility(View.GONE);
                                addresstitle.setVisibility(View.GONE);
                            }
                        } else {
                            addresstxt.setVisibility(View.GONE);
                            addresstitle.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                    });
        }
    }

    private void loadPhoneNumber() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            firestore.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String phone = documentSnapshot.getString("phone");
                            if (phone != null && !phone.isEmpty()) {
                                number.setText(phone);
                            } else {
                                number.setVisibility(View.GONE);
                            }
                        } else {
                            number.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors
                    });
        }
    }
}
