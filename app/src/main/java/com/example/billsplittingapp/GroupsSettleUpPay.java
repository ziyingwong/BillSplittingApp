package com.example.billsplittingapp;

import android.graphics.Color;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class GroupsSettleUpPay extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String payerId;
    String payeeId;
    Double amount;
    String groupId;
    String payerName;
    String payeeName;
    String groupName;
    Double oriamountpayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_settleup_pay);

        payerId = getIntent().getStringExtra("payerId");  //+
        payeeId = getIntent().getStringExtra("payeeId");  //-
        amount = getIntent().getDoubleExtra("amount", 0.00);
        groupId = getIntent().getStringExtra("groupId");

        EditText amountPaid = findViewById(R.id.amountPaid);
        String stringAmountFormat = String.format("%,.2f", amount);
        amountPaid.setText(stringAmountFormat);

        db.collection("contactList").document(payerId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    payerName = documentSnapshot.get("userName").toString();
                    TextView payername = findViewById(R.id.payerName);
                    payername.setText(payerName);
                }
            }
        });

        db.collection("contactList").document(payeeId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    payeeName = documentSnapshot.get("userName").toString();
                    TextView paymentdetails = findViewById(R.id.payingdetails);
                    paymentdetails.setText(payeeName);
                }
            }
        });

        db.collection("Groups").document(groupId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    groupName = documentSnapshot.get("groupName").toString();
                    Button groupNameButton = findViewById(R.id.groupName);
                    groupNameButton.setText(groupName);
                }
            }
        });

        Button payButton = findViewById(R.id.paybutton);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = amountPaid.getText().toString();
                Double doubleAmount = Double.parseDouble(amount);

                db.collection("Groups").document(groupId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map = (HashMap) documentSnapshot.get("user");

                        Object oriAmountPayer = map.get(payerId);
                        Double oriAmountDoublePayer = Double.parseDouble(oriAmountPayer.toString());
                        Double newAmountPayer = oriAmountDoublePayer + doubleAmount;

                        Object oriAmountPayee = map.get(payeeId);
                        Double oriAmountDoublePayee = Double.parseDouble(oriAmountPayee.toString());
                        Double newAmountPayee = oriAmountDoublePayee - doubleAmount;

                        map.put(payerId, newAmountPayer);

                        map.put(payeeId, newAmountPayee);
                        db.collection("Groups").document(groupId).update("user", map);

                        String text = payerName + " paid RM" + doubleAmount.toString() + " to " + payeeName;

                        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                        finish();

                    }
                });

            }
        });
    }
}
