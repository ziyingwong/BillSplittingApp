package com.example.billsplittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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

public class GroupsDetails extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    GroupsPaymentAdapter adapter;
    Map<String, Double> map;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_details);
        final String groupId = getIntent().getStringExtra("groupId");
        final String groupName = getIntent().getStringExtra("groupName");

        Button groupSettingButton = findViewById(R.id.groupSettingButton);
        groupSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupsDetails.this, GroupsDetailsSetting.class);
                intent.putExtra("groupName", groupName);
                intent.putExtra("groupId", groupId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        Button buttonSettleUp = findViewById(R.id.buttonSettleUp);
        buttonSettleUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupsDetails.this, GroupsSettleUp.class);
                intent.putExtra("groupId", groupId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        //toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(groupName);

        //recyclerview
        RecyclerView recycler = findViewById(R.id.expenserecycler);
        Query query = db.collection("Groups").document(groupId).collection("Payment");
        FirestoreRecyclerOptions<GroupsPaymentObject> options = new FirestoreRecyclerOptions.Builder<GroupsPaymentObject>()
                .setQuery(query, GroupsPaymentObject.class)
                .build();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupsPaymentAdapter(options);
        adapter.startListening();
        recycler.setAdapter(adapter);

        //changing text of groupsdetails
        TextView groupDetails = findViewById(R.id.groupsDetails);

        //intent to page group balance
        Button groupBalanceButton = findViewById(R.id.buttonBalances);
        groupBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(GroupsDetails.this, GroupsBalances.class);
                Intent intent = new Intent(GroupsDetails.this, GroupDebtDetailsList.class);
                intent.putExtra("groupName", groupName);
                intent.putExtra("groupId", groupId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });


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
                    }
                }
            }
        });
        ArrayList<UserStringValue> positive = new ArrayList<>();
        ArrayList<UserStringValue> negative = new ArrayList<>();
        ArrayList<UserDebtProfile> complete = new ArrayList<>();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            if (entry.getValue() > 0) {
                positive.add(new UserStringValue(entry.getKey(), entry.getValue()));
            } else {
                negative.add(new UserStringValue(entry.getKey(), entry.getValue()));
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
    }
}

class UserDebtProfile {
    String key;
    ArrayList<UserStringValue> hutang = new ArrayList<>();
    ArrayList<UserStringValue> pinjam = new ArrayList<>();

    UserDebtProfile(String k) {
        this.key = k;
    }
}

class UserStringValue {
    String key;
    Double value;

    UserStringValue(String k, Double v) {
        this.key = k;
        this.value = v;
    }
}