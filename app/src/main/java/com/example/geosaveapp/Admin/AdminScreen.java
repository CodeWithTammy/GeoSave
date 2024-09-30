package com.example.geosaveapp.Admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.geosaveapp.Components.StoreHomeIconFragment;
import com.example.geosaveapp.Components.storeaddicon;
import com.example.geosaveapp.R;
import com.example.geosaveapp.StoreScreen.StoreProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminScreen extends AppCompatActivity {
    private BottomNavigationView bottomnavview;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);


        bottomnavview = findViewById(R.id.adminnavbar);
        bottomNavView();

    }
        //replacing the fragment
        private void replaceFragment(Fragment fragment, boolean isAppInitialized) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if (isAppInitialized) {
                fragmentTransaction.add(R.id.adminframe, fragment);
            } else {
                fragmentTransaction.replace(R.id.adminframe, fragment);
            }
            fragmentTransaction.replace(R.id.adminframe, fragment);
            fragmentTransaction.commit();
        }

        //Bottom navigation for fragments
        private void bottomNavView(){
            bottomnavview.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int  itemId = item.getItemId();
                    if(itemId==R.id.adminhome){
                        replaceFragment(new Adminhome(), false);

                    }else if(itemId == R.id.adminprofile){
                        replaceFragment(new Adminprofile(),false);
                    }

                    return true;
                }
            });

            replaceFragment(new Adminhome (),true);

        }
}