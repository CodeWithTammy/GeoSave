package com.example.geosaveapp.SignUpScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.geosaveapp.R;
import com.example.geosaveapp.SignupShopper;

public class OptionsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_page);
        AppCompatButton shopperbtn = findViewById(R.id.shopperbtn);
        AppCompatButton storebtn = findViewById(R.id.storebtn);

//        storebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replaceFragment(new SignUpFragmentStore());
//            }
//        });

        storebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignupStore.class);
                startActivity(intent);
                finish();
            }
        });

        shopperbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupShopper.class);
                startActivity(intent);
                finish();
            }
        });
    }

//    private void replaceFragment(SignUpFragmentStore signUpFragmentStore) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragmentcontainer,signUpFragmentStore);
//        fragmentTransaction.commit();
//    }
}