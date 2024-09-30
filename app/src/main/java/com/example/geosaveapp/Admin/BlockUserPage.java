package com.example.geosaveapp.Admin;

import static java.security.AccessController.getContext;

import android.bluetooth.BluetoothClass;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geosaveapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class BlockUserPage extends AppCompatActivity {
    private RecyclerView recyclerViewUsers;
    private BlockAdapter usersAdapter;
    private List<BlockModel> userList;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_user_page);

        recyclerViewUsers = findViewById(R.id.blockuserrecyclerview);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        usersAdapter = new BlockAdapter(this, userList);
        recyclerViewUsers.setAdapter(usersAdapter);
        db = FirebaseFirestore.getInstance();
        loadUsers();



    }


    private void loadUsers() {
        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userList.clear();
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot documentSnapshot : querySnapshot) {
                    BlockModel user = documentSnapshot.toObject(BlockModel.class);
                    user.setId(documentSnapshot.getId());
                    userList.add(user);
                }
                usersAdapter.notifyDataSetChanged();
            }
        });
    }

}
