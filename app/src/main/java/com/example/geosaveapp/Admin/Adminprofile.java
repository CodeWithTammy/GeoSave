package com.example.geosaveapp.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.geosaveapp.LoginScreen.LoginPage;
import com.example.geosaveapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Adminprofile extends Fragment {

AppCompatButton logout;
FirebaseFirestore db;
TextView adminpass, adminemail;
LinearLayout layout;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_adminprofile, container, false);

        logout = view.findViewById(R.id.logoutbtn);
        adminpass = view.findViewById(R.id.adminpassword);
        adminemail = view. findViewById(R.id.adminemail);
        layout = view.findViewById(R.id.changepasslayout);
        db = FirebaseFirestore.getInstance();
        logout();
        loadAdminProfile();
        changePassword();
        return view;
    }

    private void changePassword() {
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), AdminChangePassword.class);
                requireContext().startActivity(intent);
            }
        });


    }

    private void logout() {
        logout.setOnClickListener(new View.OnClickListener() {
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
    private void loadAdminProfile() {
        // Assuming you have the admin ID, replace "adminId" with actual admin ID

        db.collection("Admin").document("admin").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String email = document.getString("email");
                    String password = document.getString("password");

                    adminemail.setText(email);
                    adminpass.setText(password);
                }
            }
        });
    }
}