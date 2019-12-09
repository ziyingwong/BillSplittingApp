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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupsDetails extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    GroupsPaymentAdapter adapter;

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
                Intent intent = new Intent(GroupsDetails.this, GroupsBalances.class);
                intent.putExtra("groupName", groupName);
                intent.putExtra("groupId", groupId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });


    }
}
