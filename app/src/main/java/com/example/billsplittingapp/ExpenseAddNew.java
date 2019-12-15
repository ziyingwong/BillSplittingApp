package com.example.billsplittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    Button btnSplit;
    TextView tvGroupName;
    EditText tvBillName;
    EditText tvPrice;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Map<String, Double> splitAmount;
    ArrayList<String> splitUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_expense);

        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");
//        splitAmount = (HashMap<String, Double>) getIntent().getSerializableExtra("user"); //GET FROM AMIRUL
        splitUser = getIntent().getStringArrayListExtra("splitUser");
        tvGroupName = findViewById(R.id.tvGroupName);
        tvBillName = findViewById(R.id.tvBillName);
        tvPrice = findViewById(R.id.tvPrice);

        tvGroupName.setText(groupName);

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference doc = db.collection("Groups").document();
                double total = Double.parseDouble(tvPrice.getText().toString());

                db.collection("Groups").document(groupId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            Map<String, Object> data = new HashMap<>();

                            Map<String, Double> user;
                            ArrayList<String> userArray;

                            user = (HashMap) documentSnapshot.get("user");
                            userArray = (ArrayList<String>) documentSnapshot.get("userArray");


//                            if (splitAmount == null) {
//                                for (int i = 0; i < user.size(); i++) {
//                                    splitAmount.put(userArray.get(i), total / userArray.size());
//                                    splitUser.add(userArray.get(i));
//                                }
//                            }

                            data.put("billId", doc.getId());
                            data.put("billName", tvBillName.getText().toString());
                            data.put("payer", auth.getCurrentUser().getUid());
                            data.put("price", total);
                            data.put("createTime", Timestamp.now());
//                            data.put("splitAmount", splitAmount);
                            data.put("splitUser", splitUser);

                            db.collection("Groups").document(groupId).collection("Payment").document(doc.getId()).set(data);

                            //Map 1 = userArray | Map 2 = splitAmount (Added to user)
//                                for (String key : splitAmount.keySet()) {
//                                    Double oldValue = user.get(key);
//                                    Double valueToAdd = splitAmount.get(key);
//                                    user.put(key, oldValue + valueToAdd);
//                                }

                            db.collection("Groups").document(groupId).update("user", user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                    Toast.makeText(getApplicationContext(), "Added Expense", Toast.LENGTH_SHORT).show();
                                }
                            });




                        }
                    }
                });
            }

        });

        btnSplit = findViewById(R.id.btnSplit);
        btnSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(ExpenseAddNew.this, );

            }
        });
    }
}
