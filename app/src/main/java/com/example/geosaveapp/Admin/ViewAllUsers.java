package com.example.geosaveapp.Admin;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geosaveapp.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewAllUsers extends AppCompatActivity {
    private RecyclerView viewuserecyclerview;
    private ViewUsersAdapter viewUsersAdapter;
    private FirebaseFirestore db;
    private List<ViewUserModel> viewUserModelList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_users);

        viewuserecyclerview = findViewById(R.id.viewshopdealrecyclerview);
        viewuserecyclerview.setLayoutManager(new LinearLayoutManager(this));
        viewUserModelList = new ArrayList<>();
        viewUsersAdapter = new ViewUsersAdapter(this, viewUserModelList);
        viewuserecyclerview.setAdapter(viewUsersAdapter);
        db = FirebaseFirestore.getInstance();

        viewAllUsers();
    }

    private void viewAllUsers() {
        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                viewUserModelList.clear();
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    for (DocumentSnapshot documentSnapshot : querySnapshot) {
                        ViewUserModel user = documentSnapshot.toObject(ViewUserModel.class);
                        if (user != null) {
                            user.setId(documentSnapshot.getId());
                            viewUserModelList.add(user);
                        }
                    }
                }
                viewUsersAdapter.notifyDataSetChanged();
            } else {
                // Handle the error
                Snackbar.make(findViewById(android.R.id.content), "Failed to fetch user data: " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}

//private void fetchSignUpDate() {
//    FirebaseUser currentUser = mAuth.getCurrentUser();
//    if (currentUser != null) {
//        String userId = currentUser.getUid();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference userRef = db.collection("Users").document(userId);
//
//        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    Date signUpDate = documentSnapshot.getDate("signUpDate");
//                    if (signUpDate != null) {
//                        // Format the date to show only month and year
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
//                        String formattedDate = dateFormat.format(signUpDate);
//
//                        // Set the formatted date to the TextView
//                        date.setText(formattedDate);
//                    } else {
//                        date.setText("Sign-up date not available");
//                    }
//                } else {
//                    date.setText("User data not found");
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                date.setText("Failed to retrieve sign-up date");
//            }
//        });
//    } else {
//        date.setText("User not signed in");
//    }
//}
