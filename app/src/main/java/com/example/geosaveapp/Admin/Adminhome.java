package com.example.geosaveapp.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.geosaveapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.util.ArrayList;
import java.util.List;

public class Adminhome extends Fragment {
    CardView block, viewusers, viewstoredeals;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adminhome, container, false);

        block = view.findViewById(R.id.block);
        viewusers = view.findViewById(R.id.viewusers);
        viewstoredeals = view.findViewById(R.id.viewdeals);

        BlockAccount();
        ViewUsers();
        ViewDeals();
        return view;
    }

    private void BlockAccount() {
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), BlockUserPage.class);
                startActivity(intent);

            }
        });
    }
    private void ViewUsers() {
        viewusers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(),ViewAllUsers.class);
                startActivity(intent);

            }
        });
    }
    private void ViewDeals() {
        viewstoredeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(),ViewAllShopDeals.class);
                startActivity(intent);

            }
        });
    }


}
