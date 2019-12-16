package com.example.billsplittingapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupDebtDetailsList extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    GroupsDebtDetailsAdapter adapter;
    ArrayList<UserDebtProfile> complete;
    Map<String, Object> map;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_balances);

        //toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Balances");
        final String groupName = getIntent().getStringExtra("groupName");
        final String groupId = getIntent().getStringExtra("groupId");
        ArrayList<GroupsAmountUserObject> arrayList = new ArrayList<>();
        db.collection("Groups").document(groupId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> user = (HashMap) documentSnapshot.get("user");
                    for (String key : user.keySet()) {
                        GroupsAmountUserObject userProfile = new GroupsAmountUserObject();
                        userProfile.uid = key;
                        userProfile.amount = user.get(key);
                        arrayList.add(userProfile);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
        //recycler view
        complete = new ArrayList<>();
        calculateTotal();
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new GroupsDebtDetailsAdapter(arrayList, complete);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    public void calculateTotal() {

        String uid = auth.getCurrentUser().getUid();
        Query query = db.collection("Groups").whereArrayContains("userArray", uid);
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots.size() > 0) {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        map = (HashMap) doc.get("user");
                        ArrayList<UserStringValue> positive = new ArrayList<>();
                        ArrayList<UserStringValue> negative = new ArrayList<>();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (Double.parseDouble(entry.getValue().toString()) > 0) {
                                positive.add(new UserStringValue(entry.getKey(), Double.parseDouble(entry.getValue().toString())));
                            } else {
                                negative.add(new UserStringValue(entry.getKey(), Double.parseDouble(entry.getValue().toString())));
                            }
                            complete.add(new UserDebtProfile(entry.getKey()));
                        }
                        int i = 0, j = 0;
                        while (i < positive.size()) {
                            if ((negative.get(j).value * -1) > positive.get(i).value) {
                                negative.get(j).value += positive.get(i).value;
                                for (UserDebtProfile usd : complete) {
                                    if (positive.get(i).key.equals(usd.key)) {
                                        usd.pinjam.add(new UserStringValue(negative.get(j).key, positive.get(i).value));
                                    } else if (negative.get(i).key.equals(usd.key)) {
                                        usd.hutang.add(new UserStringValue(positive.get(i).key, positive.get(i).value));
                                    }
                                }
                                i++;
                            } else if ((negative.get(j).value * -1) == positive.get(i).value) {
                                for (UserDebtProfile usd : complete) {
                                    if (positive.get(i).key.equals(usd.key)) {
                                        usd.pinjam.add(new UserStringValue(negative.get(j).key, positive.get(i).value));
                                    } else if (negative.get(i).key.equals(usd.key)) {
                                        usd.hutang.add(new UserStringValue(positive.get(i).key, positive.get(i).value));
                                    }
                                }
                                i++;
                                j++;
                            } else {
                                positive.get(i).value += negative.get(j).value;
                                for (UserDebtProfile usd : complete) {
                                    if (positive.get(i).key.equals(usd.key)) {
                                        usd.pinjam.add(new UserStringValue(negative.get(j).key, negative.get(i).value * -1));
                                    } else if (negative.get(i).key.equals(usd.key)) {
                                        usd.hutang.add(new UserStringValue(positive.get(i).key, negative.get(i).value * -1));
                                    }
                                }
                                j++;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }
}


