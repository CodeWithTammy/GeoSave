package com.example.geosaveapp.SignUpScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geosaveapp.StartingScreens.GetStarted;
import com.example.geosaveapp.LoginScreen.LoginPage;
import com.example.geosaveapp.R;
import com.example.geosaveapp.ShopperScreen.Shoppercreateprofile;
import com.example.geosaveapp.StoreScreen.StoreCreateProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignupStore extends AppCompatActivity {

    EditText name, email, password, confirmpassword;
    boolean valid = true;
    FirebaseFirestore fstore;
    FirebaseAuth fAuth;
    CheckBox storecheck, shoppercheck;
    Dialog dialog;
    TextView terms;
    AppCompatButton btnDialogClose;
    private static final int SNACKBAR_DURATION = 3000; // Snackbar duration in milliseconds (3 seconds)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_store);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        name = findViewById(R.id.storeName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.confirmPassword);
        AppCompatButton signupbtn = findViewById(R.id.signupbtn);
        storecheck = findViewById(R.id.storecheckbox);
        shoppercheck = findViewById(R.id.shoppercheckbox);
        terms = findViewById(R.id.termstxt);

        dialog = new Dialog(SignupStore.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);

        btnDialogClose = dialog.findViewById(R.id.closedialog);

        btnDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        // Check boxes logic
        storecheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    shoppercheck.setChecked(false);
                }
            }
        });

        shoppercheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    storecheck.setChecked(false);
                }
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkField(name);
                checkField(email);
                checkField(password);
                checkField(confirmpassword);

                if (!password.getText().toString().equals(confirmpassword.getText().toString())) {
                    showSnackbar(v, "Passwords do not match");
                    return;
                }

                // Checkbox validation
                if (!(storecheck.isChecked() || shoppercheck.isChecked())) {
                    showSnackbar(v, "Select Role Type");
                    return;
                }

                if (valid) {
                    fAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    FirebaseUser user = fAuth.getCurrentUser();
                                    showSnackbar(v, "Account Created");
                                    DocumentReference df = fstore.collection("Users").document(user.getUid());
                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("Name", name.getText().toString());
                                    userInfo.put("UserEmail", email.getText().toString());
                                    userInfo.put("signUpDate", new Date()); // Store the sign-up date

                                    // Specify if the user is store owner (admin)
                                    if (storecheck.isChecked()) {
                                        userInfo.put("userType", "isStore");
                                    }
                                    if (shoppercheck.isChecked()) {
                                        userInfo.put("userType", "isShopper");
                                    }

                                    df.set(userInfo);

                                    // Delay the intent to show the Snackbar
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (storecheck.isChecked()) {
                                                startActivity(new Intent(getApplicationContext(), StoreCreateProfile.class));
                                                finish();
                                            }
                                            if (shoppercheck.isChecked()) {
                                                startActivity(new Intent(getApplicationContext(), Shoppercreateprofile.class));
                                                finish();
                                            }
                                        }
                                    }, SNACKBAR_DURATION);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showSnackbar(v, "Account Creation Failed: " + e.getMessage());
                                }
                            });
                }
            }
        });

        // Intent function call
        signupintents();
    }

    public boolean checkField(EditText textField) {
        if (textField.getText().toString().isEmpty()) {
            textField.setError("Fields are empty");
            valid = false;
        } else {
            valid = true;
        }
        return valid;
    }

    private void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    // Intents for sign up page
    private void signupintents() {
        ImageView backbutton = findViewById(R.id.backbtn);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView logintxt = findViewById(R.id.logintxt);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GetStarted.class);
                startActivity(intent);
                finish();
            }
        });
        logintxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
