package com.example.geosaveapp.LoginScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.geosaveapp.R;
import com.example.geosaveapp.UtilityScreens.ChangePassword;
import com.example.geosaveapp.UtilityScreens.Settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailEditText;
    private Button resetPasswordButton;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    ImageButton backbtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.email);
        resetPasswordButton = findViewById(R.id.resetbtn);
        progressBar = findViewById(R.id.progressBar);
        backbtn = findViewById(R.id.backbtn);

        auth = FirebaseAuth.getInstance();

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(intent);
                finish();
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    showSnackbar(v, "Please enter your registered email");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            showSnackbar(v, "Password reset email sent!");
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                showSnackbar(v, "No account found with this email.");
                            } else {
                                showSnackbar(v, "Failed to send reset email: " + task.getException().getMessage());
                            }
                        }
                    }
                });
            }
        });
    }

    private void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();

        // Adding a callback to handle Intent after Snackbar is dismissed
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                // Start Settings activity after Snackbar is dismissed
                Intent intent = new Intent(ForgotPassword.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
