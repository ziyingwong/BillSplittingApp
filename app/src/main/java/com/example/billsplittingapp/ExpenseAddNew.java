package com.example.billsplittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ExpenseAddNew extends AppCompatActivity {

    String groupId;
    String groupName;
    Button btnSave;
    TextView tvGroupName;
    EditText tvBillName;
    //EditText tvPayer;
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
        //tvPayer = findViewById(R.id.tvPayer);
        tvPrice = findViewById(R.id.tvPrice);

        tvGroupName.setText(groupName);

        Intent intent = getIntent();
        HashMap<String, String>[] hashMap = new HashMap[]{(HashMap<String, String>) intent.getSerializableExtra("map")};

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DocumentReference doc = db.collection("Groups").document();
                Map<String, String> data = new HashMap<>();

                data.put("billId", doc.getId());
                data.put("billName", tvBillName.getText().toString());
                data.put("payer", auth.getCurrentUser().getUid());
                data.put("price", tvPrice.getText().toString());

                db.collection("Groups").document(groupId).collection("Payment").document(doc.getId()).set(data);


                /*double price = Double.parseDouble(tvPrice.getText().toString());
                if (hashMap[0] == null){
                    hashMap[0] = new HashMap<>();
                }*/

            }
        });
    }
}
