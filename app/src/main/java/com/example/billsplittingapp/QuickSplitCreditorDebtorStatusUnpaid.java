package com.example.billsplittingapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuickSplitCreditorDebtorStatusUnpaid extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<QuickSplitDebtor> arrayList;
    RecyclerView recyclerView;
    QuickSplitPaidStatusListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ziying_quick_creditor_viewstatuslist);
        final String billId = getIntent().getStringExtra("billId");
        final String billName = getIntent().getStringExtra("billName");

        final Button buttonDeleteBill = findViewById(R.id.buttonCalculate);
        buttonDeleteBill.setText("DELETE BILL");
        buttonDeleteBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(QuickSplitCreditorDebtorStatusUnpaid.this)
                        .setMessage("Are you sure you want to delete bill?")
                        .setPositiveButton("Yes", null)
                        .setNegativeButton("Cancel", null)
                        .setTitle("Add item")
                        .setCancelable(false)
                        .create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.collection("QuickSplit").document(billId).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(QuickSplitCreditorDebtorStatusUnpaid.this, "Deleted " + billName, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(QuickSplitCreditorDebtorStatusUnpaid.this, "Fail to delete : " + e, Toast.LENGTH_SHORT).show();
                                        Log.e("mytag", e.toString());
                                    }
                                });
                    }
                });
            }
        });

        //toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(billName);

        //hide icon
        ImageView qrCode = findViewById(R.id.qrCodeButton);
        qrCode.setVisibility(View.INVISIBLE);


        recyclerView = findViewById(R.id.debtorList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();
        adapter = new QuickSplitPaidStatusListAdapter(arrayList, this, billId);
        recyclerView.setAdapter(adapter);


        db.collection("QuickSplit").document(billId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        final Map<String, Number> debtorAmount = (HashMap) documentSnapshot.get("debtorAmount");
                        Log.e("mytag", debtorAmount.toString());

                        db.collection("QuickSplit").document(billId).collection("splitWith")
                                .orderBy("status")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        arrayList.clear();
                                        List<QuickSplitDebtor> unpaidlist = new ArrayList<>();
                                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                            QuickSplitDebtor debtor = new QuickSplitDebtor();
                                            debtor.uid = (String) doc.get("uid");
                                            debtor.amount = Double.parseDouble(debtorAmount.get(debtor.uid).toString());
                                            debtor.displayName = (String) doc.get("displayName");
                                            debtor.status = Integer.parseInt(doc.get("status").toString());
                                            if (debtor.status != QuickSplitMemberStatus.PAID) {
                                                unpaidlist.add(debtor);
                                            }
                                            Log.e("mytag", debtor.displayName + debtor.amount + debtor.status);
                                            arrayList.add(debtor);
                                            Log.e("mytag", arrayList.toString());
                                        }
                                        adapter.notifyDataSetChanged();

                                        if (unpaidlist.size() < 1) {
                                            buttonDeleteBill.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }


}
