package com.example.billsplittingapp;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
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

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QuickSplitCreditorDebtorStatusPending extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    QuickSplitCreditorDebtorStatusAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ziying_quick_creditor_viewstatuslist);
        final String billId = getIntent().getStringExtra("billId");
        final Button calculateButton = findViewById(R.id.buttonCalculate);

        //button visibility
        db.collection("QuickSplit").document(billId).collection("splitWith").whereEqualTo("status", QuickSplitMemberStatus.NOTHING_SELECTED).addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("mytag", "Listen failed." + e);
                    return;
                }
                if (queryDocumentSnapshots.getDocuments().size() < 1) {
                    calculateButton.setVisibility(View.VISIBLE);

                } else {
                    calculateButton.setVisibility(View.INVISIBLE);
                }
            }
        });


        //toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String billName = getIntent().getStringExtra("billName");
        setTitle(billName);

        //recycler view
        RecyclerView recyclerView = findViewById(R.id.debtorList);
        Query query = db.collection("QuickSplit").document(billId).collection("splitWith").orderBy("status");
        FirestoreRecyclerOptions<QuickSplitDebtor> options = new FirestoreRecyclerOptions.Builder<QuickSplitDebtor>()
                .setQuery(query, QuickSplitDebtor.class)
                .build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new QuickSplitCreditorDebtorStatusAdapter(options, billId, billName, this);
        adapter.startListening();
        recyclerView.setAdapter(adapter);


        //qr code
        ImageView imageView = findViewById(R.id.qrCodeButton);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //generate qr CODE
                try {
                    QRGEncoder qrgEncoder = new QRGEncoder(billId, null, QRGContents.Type.TEXT, 600);
                    Bitmap bm = qrgEncoder.encodeAsBitmap();
                    View v = getLayoutInflater().inflate(R.layout.ziying_quick_creditor_showqrcode, null);
                    AlertDialog dialog = new AlertDialog.Builder(QuickSplitCreditorDebtorStatusPending.this)
                            .setNeutralButton("Dismiss", null)
                            .setView(v)
                            .create();
                    ImageView qrCode = v.findViewById(R.id.qrCodeView);
                    qrCode.setImageBitmap(bm);
                    dialog.show();
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("QuickSplit").document(billId).collection("splitWith").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {

                                //get total portion for each item
                                final Map<String, Long> totalPortion = new HashMap<>();
                                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                    Map<String, Long> itemPortion = (HashMap) doc.get("itemPortion");
                                    for (Map.Entry<String, Long> item : itemPortion.entrySet()) {
                                        String key = item.getKey();
                                        Long portion = item.getValue();
                                        if (totalPortion.get(key) != null) {
                                            totalPortion.put(key, totalPortion.get(key) + portion);
                                        } else {
                                            totalPortion.put(key, portion);
                                        }
                                    }
                                }

                                //check if there is any item with zero portion
                                ArrayList<String> zeroTotalPortionItem = new ArrayList<>();
                                for (Map.Entry<String, Long> item : totalPortion.entrySet()) {
                                    Long portion = item.getValue();
                                    if (portion == 0) {
                                        zeroTotalPortionItem.add(item.getKey());
                                    }
                                }
                                if (zeroTotalPortionItem.size() != 0) {
                                    String dialogMessage = "<p>The follow item(s) is not selected by anyone<br><b>";
                                    for (String errorItems : zeroTotalPortionItem) {
                                        dialogMessage += errorItems + "<br>";
                                    }
                                    dialogMessage += "</b>Please check again</p>";
                                    final AlertDialog dialog = new AlertDialog.Builder(QuickSplitCreditorDebtorStatusPending.this)
                                            .setTitle("Total")
                                            .setMessage(Html.fromHtml(dialogMessage))
                                            .setNeutralButton("Dismiss", null)
                                            .create();
                                    dialog.show();
                                } else {
                                    db.collection("QuickSplit").document(billId).get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                    //get tax and price for each item
                                                    Map<String, Number> itemsPrice = (HashMap) documentSnapshot.get("items");
                                                    Double tax = documentSnapshot.getDouble("tax");
                                                    Map debtorsAmountList = new HashMap();


                                                    //loop through every user and get list of items with portion
                                                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                                        Map<String, Long> itemPortion = (HashMap) doc.get("itemPortion");
                                                        String name = (String) doc.get("displayName");
                                                        String uid = (String) doc.get("uid");
                                                        Double totalToBePaid = 0.0;

                                                        //for each item in each user, get the amount and sum
                                                        for (Map.Entry<String, Long> item : itemPortion.entrySet()) {
                                                            Long total = totalPortion.get(item.getKey());
                                                            Long portion = item.getValue();

                                                            Double price = Double.parseDouble(itemsPrice.get(item.getKey()).toString());
                                                            totalToBePaid += price * portion / total;
                                                            Log.e("mytag", name + " : " + item.getKey() + price * portion / total);
                                                        }
                                                        totalToBePaid = totalToBePaid * (1 + tax / 100);
                                                        totalToBePaid = Double.parseDouble(String.format("%.2f", totalToBePaid));
                                                        debtorsAmountList.put(uid, totalToBePaid);
                                                        Log.e("mytag", name + ": " + totalToBePaid);

                                                    }
                                                    Map eachDebtorAmountData = new HashMap();
                                                    eachDebtorAmountData.put("totalPortion", totalPortion);
                                                    eachDebtorAmountData.put("status", "calculated");
                                                    eachDebtorAmountData.put("debtorAmount", debtorsAmountList);
                                                    db.collection("QuickSplit").document(billId).set(eachDebtorAmountData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(QuickSplitCreditorDebtorStatusPending.this, "Calculated", Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.e("mytag", e.toString());
                                                        }
                                                    });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("mytag", e.toString());
                                                }
                                            });


                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("mytag", e.toString());
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
