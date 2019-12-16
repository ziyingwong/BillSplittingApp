package com.example.billsplittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SplitMethod_EqualValue extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    MethodSplitAdapterEqually adapter;
    RecyclerView recyclerView2;
    ArrayList<MethodSplitObject> arrayList = new ArrayList<>();
    Map<String, Double> user;
    ArrayList<String> userArray;

    Button buttonEqualconfirmed;
    String groupId;
    String groupName;
    String billName2;
    String totaltopaywrite;
    TextView totaltopay;
    double total;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.method_equally);


        final Toolbar toolbar = findViewById(R.id.toolbarequal);
        setSupportActionBar(toolbar);
        setTitle("equal value");

        groupName = getIntent().getStringExtra("groupName");
        billName2 = getIntent().getStringExtra("billName");
        groupId = getIntent().getStringExtra("groupId");
        total = Double.parseDouble(getIntent().getStringExtra("total"));

        totaltopay = findViewById(R.id.ettotaltopay);
        totaltopaywrite = String.format("%.2f", total);
        totaltopay.setText("RM" + totaltopaywrite);



        db.collection("Groups").document(groupId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    user = (HashMap) documentSnapshot.get("user");
                    userArray = (ArrayList<String>) documentSnapshot.get("userArray");
                    for (int i = 0; i < user.size(); i++) {
                        MethodSplitObject userProfile = new MethodSplitObject();
                        userProfile.uid = userArray.get(i);
                        arrayList.add(userProfile);
                    }
                    adapter.notifyDataSetChanged();

                }
            }
        });

        recyclerView2 = findViewById(R.id.equalvaluelist);
        adapter = new MethodSplitAdapterEqually(arrayList,total);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.setAdapter(adapter);

        buttonEqualconfirmed = findViewById(R.id.buttonEqualconfirmed);
        buttonEqualconfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Double> newAmount = new HashMap<>();
                for (int i = 0; i < userArray.size(); i++) {
                    View view = recyclerView2.getChildAt(i);
                    TextView amount = view.findViewById(R.id.tvtopay);
                    Double portion = Double.parseDouble(amount.getText().toString());
                    newAmount.put(userArray.get(i), portion);
                }


                Intent intent = new Intent(SplitMethod_EqualValue.this, ExpenseAddNew.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupName", groupName);
                intent.putExtra("splitAmount", newAmount);
                intent.putExtra("splitUser", userArray);
                intent.putExtra("price", total);
                intent.putExtra("billName2", billName2);
                startActivity(intent);
            }
        });


    }
}
