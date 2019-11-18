package com.example.billsplittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class QuickSplitGeneralEnterShare extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ArrayList<QuickSplitItemsPrice> arrayList = new ArrayList<>();
    boolean editState;
    RecyclerView recyclerView;
    String billId;
    String billName;
    String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ziying_quick_general_entershare);
        billId = getIntent().getStringExtra("billId");
        billName = getIntent().getStringExtra("billName");
        editState = getIntent().getBooleanExtra("edit", false);
        boolean creditor = getIntent().getBooleanExtra("creditor", false);
        uid = auth.getCurrentUser().getUid();
        if (creditor && editState) {
            uid = getIntent().getStringExtra("debtorUid");
        }

        Button doneButton = findViewById(R.id.buttonDone);
        LinearLayout editStateButtons = findViewById(R.id.editStateButtons);
        Button cancelButton = findViewById(R.id.buttonCancel);
        Button editdoneButton = findViewById(R.id.buttonDoneEdit);
        if (editState) {
            editStateButtons.setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.INVISIBLE);

        }

        //toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //items
        recyclerView = findViewById(R.id.itemListRecycler);
        final QuickSplitGeneralEnterPortionAdapter adapter = new QuickSplitGeneralEnterPortionAdapter(arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        db.collection("QuickSplit").document(billId).collection("splitWith").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        db.collection("QuickSplit").document(billId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    setTitle(documentSnapshot.get("billName").toString());
                    Map<String, Double> items = (HashMap) documentSnapshot.get("items");
                    for (String key : items.keySet()) {
                        arrayList.add(new QuickSplitItemsPrice(key, items.get(key)));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(QuickSplitGeneralEnterShare.this, "Bill does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(QuickSplitGeneralEnterShare.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuickSplitGeneralEnterShare.this.onBackPressed();
            }
        });

        editdoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePortion();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updatePortion();
            }

        });

    }

    public void updatePortion() {
        Map<String, Integer> itemPortion = new HashMap<>();
        for (int i = 0; i < arrayList.size(); i++) {
            View v = recyclerView.getChildAt(i);
            EditText editText = v.findViewById(R.id.portionEt);
            int portion;
            if (editText.getText().toString().isEmpty()) {
                portion = 0;
            } else {
                portion = Integer.parseInt(editText.getText().toString());
            }
            itemPortion.put(arrayList.get(i).name, portion);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("itemPortion", itemPortion);
        data.put("status", QuickSplitMemberStatus.SELECTED_ITEM);

        db.collection("QuickSplit").document(billId)
                .collection("splitWith").document(auth.getCurrentUser().getUid())
                .set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                db.collection("QuickSplit").document(billId).collection("splitWith")
                        .whereEqualTo("status", QuickSplitMemberStatus.NOTHING_SELECTED).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (queryDocumentSnapshots.isEmpty()) {
                                    Map<String, Object> billStatus = new HashMap<>();
                                    billStatus.put("status", "Ready to split");
                                    db.collection("QuickSplit").document(billId).set(billStatus, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.e("mytag", "Ready to split");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("mytag", e.toString());
                                        }
                                    });

                                }
                                Toast.makeText(QuickSplitGeneralEnterShare.this, "Submitted", Toast.LENGTH_SHORT).show();
                                finish();
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
                Log.e("mytag", e.toString());
                Toast.makeText(QuickSplitGeneralEnterShare.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (editState) {
            Log.e("mytag", "entered backpress edit state");
            Map<String, Object> data = new HashMap<>();
            data.put("status", QuickSplitMemberStatus.SELECTED_ITEM);

            db.collection("QuickSplit").document(billId)
                    .collection("splitWith").document(auth.getCurrentUser().getUid())
                    .set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    db.collection("QuickSplit").document(billId).collection("splitWith")
                            .whereEqualTo("status", QuickSplitMemberStatus.NOTHING_SELECTED).get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (queryDocumentSnapshots.isEmpty()) {
                                        Map<String, Object> billStatus = new HashMap<>();
                                        billStatus.put("status", "Ready to split");
                                        db.collection("QuickSplit").document(billId).set(billStatus, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.e("mytag", "Ready to split");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("mytag", e.toString());
                                            }
                                        });

                                    }
                                    finish();
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
                    Log.e("mytag", e.toString());
                    Toast.makeText(QuickSplitGeneralEnterShare.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
