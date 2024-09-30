package com.example.geosaveapp.Components;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.geosaveapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;


public class NewDealCard extends AppCompatActivity {

//    private ActivityNewDealCardBinding binding;
//    private String dealId;

    EditText name, desc;
    AppCompatButton submitbtn;
    TextView canceltxt;
    TextView addtitle;
    String storename, description,docId;
    boolean isEditMode = false;
    ImageButton deletebtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_deal_card);

        name = findViewById(R.id.nameofstoreedttxt);
        desc = findViewById(R.id.descriptionedttxt);
        submitbtn = findViewById(R.id.submitbtn);
        addtitle = findViewById(R.id.addtitle);
        deletebtn = findViewById(R.id.deletebtn);
        //receive data
        storename = getIntent().getStringExtra("title");
        description= getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        name.setText(storename);
        desc.setText(description);

        if(isEditMode){
            addtitle.setText("Edit your deal");
            deletebtn.setVisibility(View.VISIBLE);
        } else {
            // Hide the deletebtn ImageButton in non-edit mode
            deletebtn.setVisibility(View.GONE);
        }




        submitbtn.setOnClickListener((v) -> submitDeal());
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletebtn.setImageResource(R.drawable.round_delete_24);
                deleteNoteFromFirebase();
            }
        });

    }

 void submitDeal() {
        String storetitle = name.getText().toString();
        String description = desc.getText().toString();
        if (storetitle == null || description.isEmpty()) {
            name.setError("Name of store is required");
            desc.setError("Description of deal is required");
            return;
        }

        DealModel dealModel = new DealModel();
        dealModel.setName(storetitle);
        dealModel.setDescription(description);
        dealModel.setTimestamp(Timestamp.now());

        saveDealToFirebase(dealModel);

    }
    void saveDealToFirebase(DealModel dealModel){
        DocumentReference documentReference;
        if(isEditMode){
            //update the note
            documentReference = Utility.getCollectionReferenceForDeals().document(docId);
        }else{
            //create new note
            documentReference = Utility.getCollectionReferenceForDeals().document();
        }

        documentReference.set(dealModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(NewDealCard.this,"Deal Added Succesfully");
                    finish();
                }else{
                    Utility.showToast(NewDealCard.this,"Failure to add deal");

                }
            }
        });
    }
    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForDeals().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //note is deleted
                    Utility.showToast(NewDealCard.this,"Deal deleted successfully");
                    finish();
                }else{
                    Utility.showToast(NewDealCard.this,"Failed while deleting deal");
                }
            }
        });
    }



}

