package com.example.billsplittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupsAddNewGroup extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    GroupsAddNewPeopleAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_addnewgroup);

        final EditText groupName = findViewById(R.id.editGroupName);
        Button saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupNameString = groupName.getText().toString();
                Map<String, Object> data = new HashMap<>();
                Map<String, Double> itemArray = new HashMap<>();
                double amount = 0;
                List array = new ArrayList();
                itemArray.put(auth.getCurrentUser().getUid(), amount);
                final DocumentReference doc = db.collection("Groups").document();
                array.add(auth.getCurrentUser().getUid());
                data.put("groupName", groupNameString);
                data.put("userArray", array);
                data.put("user", itemArray);
                doc.set(data);
            }
        });

        //navigate to groups add new friend in new group page
        LinearLayout addNewFriend = findViewById(R.id.group_add_people);
        addNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), GroupsAddNewFriendInNewGroup.class);
                startActivity(intent);
            }
        });


        //recyclerview
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

    }
}
