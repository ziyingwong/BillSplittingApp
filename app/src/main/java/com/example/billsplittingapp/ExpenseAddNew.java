package com.example.billsplittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseAddNew extends AppCompatActivity {

    String groupId;
    String groupName;
    Button btnSave;
    TextView tvGroupName;
    EditText tvBillName;
    EditText tvPrice;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_expense);

        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");

        tvGroupName = findViewById(R.id.tvGroupName);
        tvBillName = findViewById(R.id.tvBillName);
        tvPrice = findViewById(R.id.tvPrice);

        tvGroupName.setText(groupName);

        Intent intent = getIntent();

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference doc = db.collection("Groups").document();


                Map<String, Object> data = new HashMap<>();
                final Map<String, Double>[] userArray = new Map[]{new HashMap<>()};
                List array = new ArrayList();

                db.collection("Groups").document(groupId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            userArray[0] = (HashMap) documentSnapshot.get("user");
                        }
                    }
                });

                //userArray.get();

                double temp = Double.parseDouble(tvPrice.getText().toString());


                data.put("billId", doc.getId());
                data.put("billName", tvBillName.getText().toString());
                data.put("payer", auth.getCurrentUser().getUid());
                data.put("price", temp);
                data.put("createTime", Timestamp.now());
                data.put("splitAmount", userArray);

                db.collection("Groups").document(groupId).collection("Payment").document(doc.getId()).set(data);


            }
        });
    }
}
