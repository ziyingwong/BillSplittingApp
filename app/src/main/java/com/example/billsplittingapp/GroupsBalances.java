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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupsBalances extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    GroupsBalancesAdapter adapter;

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
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new GroupsBalancesAdapter(arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}
