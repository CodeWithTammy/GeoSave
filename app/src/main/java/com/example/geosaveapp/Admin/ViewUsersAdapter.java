package com.example.geosaveapp.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.geosaveapp.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ViewUsersAdapter extends RecyclerView.Adapter<ViewUsersAdapter.ViewUserViewHolder> {

    private Context context;
    private List<ViewUserModel> userList;

    public ViewUsersAdapter(Context context, List<ViewUserModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewuserdesign, parent, false);
        return new ViewUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewUserViewHolder holder, int position) {
        ViewUserModel user = userList.get(position);
        holder.Name.setText(user.getName());
        holder.UserEmail.setText(user.getUserEmail());
        holder.userType.setText(user.getUserType());
        holder.datejoined.setText(user.getsignUpDate().toString());
        Glide.with(context).load(user.getprofilePic()).into(holder.profilePic);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewUserViewHolder extends RecyclerView.ViewHolder {

        TextView Name, UserEmail, userType, datejoined;
        ShapeableImageView profilePic;

        public ViewUserViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.nameofuser);
            UserEmail = itemView.findViewById(R.id.emailofuser);
            userType = itemView.findViewById(R.id.typeofuser);
            datejoined = itemView.findViewById(R.id.dateposted);
            profilePic = itemView.findViewById(R.id.profilepictureholder);
        }
    }
}
