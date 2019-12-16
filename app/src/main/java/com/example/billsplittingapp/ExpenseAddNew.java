package com.example.billsplittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.firebase.firestore.remote.WatchChange.WatchTargetChangeType.Added;

public class ExpenseAddNew extends AppCompatActivity {

    String groupId;
    String groupName;
    String billName2;
    Double price;
    String price2;
    Button btnSave;
    Button btnSplit;
    Button btnDelete;
    TextView tvGroupName;
    EditText tvBillName;
    EditText tvPrice;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Map<String, Double> splitAmount;
    ArrayList<String> splitUser;
    private Double total;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_expense);


        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");
        splitAmount = (Map<String, Double>) getIntent().getSerializableExtra("splitAmount"); //GET FROM AMIRUL
        splitUser = getIntent().getStringArrayListExtra("splitUser");
        billName2 = getIntent().getStringExtra("billName2");
        price = getIntent().getDoubleExtra("price", 0.00);

        price2 = price.toString();
        tvGroupName = findViewById(R.id.tvGroupName);



        tvBillName = findViewById(R.id.tvBillName);
        tvPrice = findViewById(R.id.tvPrice);

        if (billName2 != null && !billName2.isEmpty()) {
            tvBillName.setText(billName2, TextView.BufferType.EDITABLE);
            tvPrice.setText(price2, TextView.BufferType.EDITABLE);
        }


        tvGroupName.setText(groupName);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String regex = "[0-9]+";

                if (tvBillName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(view.getContext(), "Bill name cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (tvPrice.getText().toString().trim().isEmpty() || Double.parseDouble(tvPrice.getText().toString()) == 0) {
                    Toast.makeText(view.getContext(), "Amount cannot be empty or 0", Toast.LENGTH_SHORT).show();
                } else {
                    btnSave.setClickable(false);
                    btnSplit.setClickable(false);
                    DocumentReference doc = db.collection("Groups").document();
                    total = Double.parseDouble(tvPrice.getText().toString());

                    db.collection("Groups").document(groupId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {

                                Map<String, Object> data = new HashMap<>();

                                Map<String, Double> user;
                                ArrayList<String> userArray;

                                user = (HashMap) documentSnapshot.get("user");
                                userArray = (ArrayList<String>) documentSnapshot.get("userArray");

                                //when splitAmount is not passed from splitting method
                                if (splitAmount == null) {

                                    splitAmount = new HashMap<>();
                                    splitUser = new ArrayList<>();
                                    double temp = total / userArray.size();
                                    for (int i = 0; i < userArray.size(); i++) {
                                        splitAmount.put(userArray.get(i), (temp * -1));
                                        splitUser.add(userArray.get(i));
                                    }
                                }

                                data.put("billId", doc.getId());
                                data.put("billName", tvBillName.getText().toString());
                                data.put("payer", auth.getCurrentUser().getUid());
                                data.put("price", total);
                                data.put("createTime", Timestamp.now());
                                data.put("splitAmount", splitAmount);
                                data.put("splitUser", splitUser);

                                db.collection("Groups").document(groupId).collection("Payment").document(doc.getId()).set(data);

                                // Map 1 = userArray | Map 2 = splitAmount (Added to user)
                                for (String key : splitAmount.keySet()) {
                                    Object objectamount = user.get(key);
                                    Double oldValue = Double.parseDouble(objectamount.toString());
                                    if (key.equals(auth.getCurrentUser().getUid())) {
                                        oldValue += total;
                                    }
                                    Double valueToAdd = splitAmount.get(key);
                                    user.put(key, oldValue + valueToAdd);
                                }

                                db.collection("Groups").document(groupId).update("user", user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Added Expense", Toast.LENGTH_SHORT).show();
                                        finish();

                                    }
                                });


                            }
                        }
                    });
                }
            }

        });
        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setVisibility(View.INVISIBLE);


        btnSplit = findViewById(R.id.btnSplit);

        btnSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String regex = "[0-9]+";

                if (tvPrice.getText().toString().trim().isEmpty() || Double.parseDouble(tvPrice.getText().toString()) == 0) {
                    Toast.makeText(view.getContext(), "Amount cannot be empty or 0", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(ExpenseAddNew.this, SplitMethod_Menu.class);
                    intent.putExtra("groupId", groupId);
                    intent.putExtra("groupName", groupName);
                    intent.putExtra("billName", tvBillName.getText().toString());
                    intent.putExtra("total", tvPrice.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }


}
