package com.example.geosaveapp.Admin;

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

import com.example.geosaveapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminChangePassword extends AppCompatActivity {

    ImageButton backbtn;
    private TextInputLayout oldPasswordLayout, newPasswordLayout, confirmPasswordLayout;
    private EditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button updatePasswordButton;

    private FirebaseAuth auth;
    private FirebaseUser user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_change_password);

        backbtn = findViewById(R.id.backbtn);
        oldPasswordLayout = findViewById(R.id.Textlayout1);
        newPasswordLayout = findViewById(R.id.Textlayout2);
        confirmPasswordLayout = findViewById(R.id.Textlayout3);

        oldPasswordEditText = findViewById(R.id.oldpassword);
        newPasswordEditText = findViewById(R.id.newpassword);
        confirmPasswordEditText = findViewById(R.id.confirmpassword);
        updatePasswordButton = findViewById(R.id.updatebtn);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        backButton();
        changePassword();
    }

    private void changePassword() {
        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                    showSnackbar(v, "All fields are required");
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    showSnackbar(v, "Passwords do not match");
                    return;
                }

                // Re-authenticate the user
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Update password
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        showSnackbar(v, "Password updated successfully");
//                                        Intent intent = new Intent(ChangePassword.this, Settings.class);
//                                        startActivity(intent);
//                                        finish();
                                    } else {
                                        showSnackbar(v, "Error updating password: " + task.getException().getMessage());
                                    }
                                }
                            });
                        } else {
                            showSnackbar(v, "Old password is incorrect: " + task.getException().getMessage());
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
                Intent intent = new Intent(AdminChangePassword.this, Adminprofile.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void backButton() {
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Adminprofile.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
