package com.example.billsplittingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExpenseDelete extends AppCompatActivity {


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ExpenseDeleteAdapter adapter;

    TextView tvGroupNameDelete;
    String groupId;
    String groupName;
    String createTime;
    String billId;
    String billName;
    Button btnBackDelete;
    RecyclerView deleteRecycler;
    Map<String, Double> user;
    ArrayList<String> userArray;
    Map<String, Double> splitAmount;
    ArrayList<String> splitUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_delete);

        tvGroupNameDelete = findViewById(R.id.tvGroupNameDelete);
        deleteRecycler = findViewById(R.id.recyclerview);
        btnBackDelete = findViewById(R.id.btnBackDelete);

        billId = getIntent().getStringExtra("billId");
        billName = getIntent().getStringExtra("billName");
        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");
        createTime = getIntent().getStringExtra("createTime");
        //tvGroupNameDelete.setText(billName);

        CollectionReference doc = db.collection("Groups").document(groupId).collection("Payment");

        setUpRecyclerView();

        btnBackDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpenseDelete.this, ExpenseAddNew.class);
                startActivity(intent);
            }
        });

    }

    private void setUpRecyclerView() {
        Query query = db.collection("Groups").document(groupId).collection("Payment").orderBy("createTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<GroupsPaymentObject> options = new FirestoreRecyclerOptions.Builder<GroupsPaymentObject>()
                .setQuery(query, GroupsPaymentObject.class)
                .build();
        adapter = new ExpenseDeleteAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.deleteRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//                adapter.deleteItem(viewHolder.getAdapterPosition());
//            }
//        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new ExpenseDeleteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                db.collection("Groups").document(groupId).collection("Payment").document(billId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            splitAmount = (HashMap) documentSnapshot.get("splitAmount");
                            splitUser = (ArrayList<String>) documentSnapshot.get("splitUser");

                            db.collection("Groups").document(groupId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {

                                        user = (HashMap) documentSnapshot.get("user");
                                        userArray = (ArrayList<String>) documentSnapshot.get("userArray");

                                        for (String key : splitAmount.keySet()){

                                            Object objectamount = user.get(key);
                                            Double oldValue = Double.parseDouble(objectamount.toString());
                                            Double valueToAdd = splitAmount.get(key);
                                            user.put(key, oldValue - valueToAdd);
                                        }

                                        db.collection("Groups").document(groupId).update("user", user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        });

                                    }
                                }
                            });

                          Toast.makeText(getApplicationContext(), "Deleted Expense", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                adapter.deleteItem(position);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
