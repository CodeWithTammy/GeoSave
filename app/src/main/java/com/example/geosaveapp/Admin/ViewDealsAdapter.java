package com.example.geosaveapp.Admin;


import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geosaveapp.R;

import java.util.List;


public class ViewDealsAdapter extends RecyclerView.Adapter<ViewDealsAdapter.ShopDealViewHolder>{

        private Context context;
        private List<ViewDealsModel> shopDeals;

        public ViewDealsAdapter(Context context, List<ViewDealsModel> shopDeals) {
            this.context = context;
            this.shopDeals = shopDeals;
        }

        @NonNull
        @Override
        public ShopDealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.viewshopdealsdesign, parent, false);
            return new ShopDealViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ShopDealViewHolder holder, int position) {
            ViewDealsModel deal = shopDeals.get(position);
            holder.dealTitleTextView.setText(deal.getname());
            holder.dealDescriptionTextView.setText(deal.getdescription());
            // Check if timestamp is null
            if (deal.gettimestamp() != null) {
                holder.dateposted.setText(deal.gettimestamp().toString());
            } else {
                holder.dateposted.setText("Date not available"); // Set a default value or handle as needed
            }
            Glide.with(context).load(deal.getprofilePic()).into(holder.dealImageView);
        }

        @Override
        public int getItemCount() {
            return shopDeals.size();
        }

        public static class ShopDealViewHolder extends RecyclerView.ViewHolder {
            ImageView dealImageView;
            TextView dealTitleTextView;
            TextView dealDescriptionTextView;
            TextView dateposted;

            public ShopDealViewHolder(@NonNull View itemView) {
                super(itemView);
                dealImageView = itemView.findViewById(R.id.profilepictureholder);
                dealTitleTextView = itemView.findViewById(R.id.storenamedeal);
                dealDescriptionTextView = itemView.findViewById(R.id.storedealdesc);
                dateposted = itemView.findViewById(R.id.dateposted);
            }
        }
    }


