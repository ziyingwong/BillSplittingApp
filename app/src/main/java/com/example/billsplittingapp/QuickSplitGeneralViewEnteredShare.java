package com.example.billsplittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuickSplitGeneralViewEnteredShare extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ArrayList<QuickSplitItemsPortion> arrayList = new ArrayList<>();
    String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ziying_quick_general_viewenteredshare);
        final String billId = getIntent().getStringExtra("billId");
        final String billName = getIntent().getStringExtra("billName");
        final boolean creditor = getIntent().getBooleanExtra("creditor", false);
        uid = auth.getCurrentUser().getUid();
        if (creditor) {
            uid = getIntent().getStringExtra("debtorUid");
        }

        //toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(billName);

        Button doneButton = findViewById(R.id.buttonDone);
        doneButton.setText("Edit");
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Integer> statusmap = new HashMap<>();
                statusmap.put("status", QuickSplitMemberStatus.NOTHING_SELECTED);
                db.collection("QuickSplit").document(billId).collection("splitWith").document(uid)
                        .set(statusmap, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Map<String, Object> billStatus = new HashMap<>();
                                billStatus.put("status", "pending");
                                db.collection("QuickSplit").document(billId).set(billStatus, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.e("mytag", "pending");
                                        Intent intent = new Intent(QuickSplitGeneralViewEnteredShare.this, QuickSplitGeneralEnterShare.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                        intent.putExtra("billName", billName);
                                        intent.putExtra("billId", billId);
                                        intent.putExtra("edit", true);
                                        intent.putExtra("creditor", creditor);
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("mytag", e.toString());
                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuickSplitGeneralViewEnteredShare.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        RecyclerView recyclerView = findViewById(R.id.itemListRecycler);
        final QuickSplitGeneralViewPortionAdapter adapter = new QuickSplitGeneralViewPortionAdapter(arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        db.collection("QuickSplit").document(billId).collection("splitWith").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Long> items = (HashMap) documentSnapshot.get("itemPortion");
                    if (items == null||items.isEmpty()) {
                        db.collection("QuickSplit").document(billId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot doc) {
                                Map<String, Double> itemsWithPrice = (HashMap) doc.get("items");
                                for (String key : itemsWithPrice.keySet()) {
                                    arrayList.add(new QuickSplitItemsPortion(key, 0));
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(QuickSplitGeneralViewEnteredShare.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        for (String key : items.keySet()) {
                            arrayList.add(new QuickSplitItemsPortion(key, items.get(key)));
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(QuickSplitGeneralViewEnteredShare.this, "Bill does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(QuickSplitGeneralViewEnteredShare.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
