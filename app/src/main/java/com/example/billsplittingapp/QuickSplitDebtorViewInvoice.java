package com.example.billsplittingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.IntDef;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuickSplitDebtorViewInvoice extends AppCompatActivity {
    List<QuickSplitInvoiceItem> arrayList;
    RecyclerView recyclerView;
    QuickSplitInvoiceAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    TextView totalToBePaid;
    TextView taxTV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ziying_quick_debtor_viewinvoice);
        final String billId = getIntent().getStringExtra("billId");

        totalToBePaid = findViewById(R.id.totalTv);
        taxTV = findViewById(R.id.taxTextView);

        //toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String billName = getIntent().getStringExtra("billName");
        setTitle(billName);

        //recycler
        recyclerView = findViewById(R.id.itemListRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        arrayList = new ArrayList<>();
        adapter = new QuickSplitInvoiceAdapter(arrayList);
        recyclerView.setAdapter(adapter);
        db.collection("QuickSplit").document(billId).collection("splitWith").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                final Map<String, Long> dividePortion = (HashMap) documentSnapshot.get("itemPortion");

                db.collection("QuickSplit").document(billId)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                arrayList.clear();

                                Map totalAmount = (HashMap) documentSnapshot.get("debtorAmount");
                                totalToBePaid.setText(totalAmount.get(auth.getCurrentUser().getUid()).toString());
                                Double tax = (Double) documentSnapshot.get("tax");
                                taxTV.setText("Tax : " + tax + "%");

                                Map<String, Long> totalPortion = (HashMap) documentSnapshot.get("totalPortion");
                                Map<String, Number> price = (HashMap) documentSnapshot.get("items");

                                for (Map.Entry<String, Long> portionForEach : totalPortion.entrySet()) {
                                    QuickSplitInvoiceItem item = new QuickSplitInvoiceItem();
                                    long totalPortionLong = portionForEach.getValue();
                                    item.totalPortion = totalPortionLong;
                                    String name = portionForEach.getKey();
                                    item.itemName = name;
                                    Double priceDouble = Double.parseDouble(price.get(item.itemName).toString());
                                    item.price = priceDouble;
                                    long dividePortionLong = dividePortion.get(item.itemName);
                                    item.dividePortion = dividePortionLong;

                                    double amount = priceDouble * dividePortionLong / totalPortionLong;
                                    item.divideAmount = Double.parseDouble(String.format("%.2f", amount));
                                    arrayList.add(item);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public void closeButtonClicked(View v) {
        finish();
    }
}
