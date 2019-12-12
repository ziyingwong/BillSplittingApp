package com.example.billsplittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupsAddNewFriendInNewGroup extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    GroupsAddNewPeopleAdapter adapter;
    String groupName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_addnewfriend);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Friend");

        //recyclerview
        GroupsNewFriendCheckedArray.getInstance().tempList = (ArrayList<GroupsNewUserObject>) GroupsNewFriendCheckedArray.getInstance().arrayList.clone();
        String uid = auth.getCurrentUser().getUid();
        RecyclerView recycler = findViewById(R.id.grouppeoplerecycler);
        Query query = db.collection("contactList").document(uid).collection("friend");
        FirestoreRecyclerOptions<GroupsNewUserObject> options = new FirestoreRecyclerOptions.Builder<GroupsNewUserObject>()
                .setQuery(query, GroupsNewUserObject.class)
                .build();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupsAddNewPeopleAdapter(options);
        adapter.startListening();
        recycler.setAdapter(adapter);

        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupsNewFriendCheckedArray.getInstance().tempList.clear();
                Intent intent = new Intent(GroupsAddNewFriendInNewGroup.this, GroupsAddNewGroup.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });


        Button addNewFriend = findViewById(R.id.buttonNewFriend);
        addNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupsNewFriendCheckedArray.getInstance().arrayList = (ArrayList<GroupsNewUserObject>) GroupsNewFriendCheckedArray.getInstance().tempList.clone();
                GroupsNewFriendCheckedArray.getInstance().tempList.clear();

                Intent intent = new Intent(GroupsAddNewFriendInNewGroup.this, GroupsAddNewGroup.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);

            }
        });
    }


}
