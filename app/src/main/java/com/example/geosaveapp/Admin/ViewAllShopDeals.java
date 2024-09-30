package com.example.geosaveapp.Admin;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.geosaveapp.R;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.geosaveapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewAllShopDeals extends AppCompatActivity {

    private RecyclerView shopDealsRecyclerView;
    private ViewDealsAdapter shopDealsAdapter;
    private FirebaseFirestore db;
    private List<ViewDealsModel> shopDealList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_shop_deals);


            shopDealsRecyclerView = findViewById(R.id.viewshopdealrecyclerview);
            shopDealsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            shopDealList = new ArrayList<>();
            shopDealsAdapter = new ViewDealsAdapter(this, shopDealList);
            shopDealsRecyclerView.setAdapter(shopDealsAdapter);
            db = FirebaseFirestore.getInstance();

        fetchUserAccounts();
        }

    private void fetchUserAccounts() {
        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot documentSnapshot : querySnapshot) {
                    String userId = documentSnapshot.getId();
                    String userType = documentSnapshot.getString("userType");
                    if ("isStore".equals(userType)) {
                        String profilePic = documentSnapshot.getString("profilePic");
                        fetchStoreDeals(userId, profilePic);
                    }
                }
            }
        });
    }

    private void fetchStoreDeals(String userId, String profilePic) {
        db.collection("Users").document(userId).collection("storedeals").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot documentSnapshot : querySnapshot) {
                    ViewDealsModel deal = documentSnapshot.toObject(ViewDealsModel.class);
                    deal.setprofilePic(profilePic); // Set the profile picture URL for each deal
                    shopDealList.add(deal);
                }
                shopDealsAdapter.notifyDataSetChanged();
            }
        });
    }

}