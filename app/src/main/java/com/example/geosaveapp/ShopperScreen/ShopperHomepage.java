package com.example.geosaveapp.ShopperScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.geosaveapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ShopperHomepage extends AppCompatActivity {


        private BottomNavigationView bottomnavview;

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_shopper_homepage);
            bottomnavview = findViewById(R.id.shopperhomebar);
            bottomNavView();


        }


    //replacing the fragment
        private void replaceFragment(Fragment fragment, boolean isAppInitialized) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if (isAppInitialized) {
                fragmentTransaction.add(R.id.shopperhomeframe, fragment);
            } else {
                fragmentTransaction.replace(R.id.shopperhomeframe, fragment);
            }
            fragmentTransaction.replace(R.id.shopperhomeframe, fragment);
            fragmentTransaction.commit();
        }

        //Bottom navigation for fragments
        private void bottomNavView(){
            bottomnavview.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int  itemId = item.getItemId();
                    if(itemId==R.id.shopperhome){
                        replaceFragment(new ShopperHomeIcon (), false);

                    }else if(itemId == R.id.shopperhistory){
                        replaceFragment(new ShopperHistoryIcon(),false);
                    }else{
                        replaceFragment(new ShopperProfileIcon(),false);
                    }

                    return true;
                }
            });

            replaceFragment(new ShopperHomeIcon (),true);

        }


    }