package com.example.geosaveapp.StoreScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.geosaveapp.Components.StoreHomeIconFragment;
import com.example.geosaveapp.R;
import com.example.geosaveapp.Components.storeaddicon;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StoreHomePage extends AppCompatActivity {
    private BottomNavigationView bottomnavview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_home_page);
        bottomnavview = findViewById(R.id.storehomenavbar);
        bottomNavView();

    }





//replacing the fragment
    private void replaceFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized) {
            fragmentTransaction.add(R.id.storehomeframe, fragment);
        } else {
            fragmentTransaction.replace(R.id.storehomeframe, fragment);
        }
        fragmentTransaction.replace(R.id.storehomeframe, fragment);
        fragmentTransaction.commit();
    }

    //Bottom navigation for fragments
    private void bottomNavView(){
        bottomnavview.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int  itemId = item.getItemId();
                if(itemId==R.id.storehome){
                    replaceFragment(new StoreHomeIconFragment(), false);

                }else if(itemId == R.id.storeadd){
                    replaceFragment(new storeaddicon(),false);
                }else{
                    replaceFragment(new StoreProfile(),false);
                }

                return true;
            }
        });

        replaceFragment(new StoreHomeIconFragment (),true);

    }


}