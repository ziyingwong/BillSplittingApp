package com.example.billsplittingapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuickSplitGeneralEnterShare extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ArrayList<QuickSplitItems> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ziying_quick_general_entershare);
        final String billId = getIntent().getStringExtra("billId");
        Button doneButton = findViewById(R.id.buttonDone);

        //toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //items
        final RecyclerView recyclerView = findViewById(R.id.itemListRecycler);
        final QuickSplitGeneralEnterPortionAdapter adapter = new QuickSplitGeneralEnterPortionAdapter(arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        db.collection("InstantSplit").document(billId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    setTitle(documentSnapshot.get("billName").toString());
                    Map<String, Double> items = (HashMap) documentSnapshot.get("items");
                    for (String key : items.keySet()) {
                        arrayList.add(new QuickSplitItems(key, items.get(key)));
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

        doneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
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

                db.collection("InstantSplit").document(billId)
                        .collection("splitWith").document(auth.getCurrentUser().getUid())
                        .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(QuickSplitGeneralEnterShare.this, "Submitted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuickSplitGeneralEnterShare.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
