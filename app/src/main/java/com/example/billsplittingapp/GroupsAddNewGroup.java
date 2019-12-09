package com.example.billsplittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.Source;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class GroupsAddNewGroup extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DocumentReference doc;
    String groupId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_addnewgroup);

        final EditText groupNameField = findViewById(R.id.editGroupName);
        groupNameField.setText(GroupsGetNewGroupName.getInstance().groupName);

        groupNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String groupNameString = groupNameField.getText().toString();
                GroupsGetNewGroupName.getInstance().groupName = groupNameString;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //add group to database
        Button addGroupButton = findViewById(R.id.buttonAddGroup);
        addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Group added", Toast.LENGTH_SHORT).show();
                Map<String, Object> data = new HashMap<>();
                Map<String, Double> itemArray = new HashMap<>();
                double amount = 0;
                List array = new ArrayList();
                itemArray.put(auth.getCurrentUser().getUid(), amount);
                doc = db.collection("Groups").document();
                array.add(auth.getCurrentUser().getUid());
                groupId = doc.getId();
                for (int i = 0; i<GroupsNewFriendCheckedArray.getInstance().arrayList.size(); i++) {
                    array.add(GroupsNewFriendCheckedArray.getInstance().arrayList.get(i).getFriendUID());
                    itemArray.put(GroupsNewFriendCheckedArray.getInstance().arrayList.get(i).getFriendUID(), amount);
                }
                data.put("groupName", GroupsGetNewGroupName.getInstance().groupName);
                data.put("groupId", doc.getId());
                data.put("userArray", array);
                data.put("user", itemArray);
                doc.set(data);
                Intent intent = new Intent(GroupsAddNewGroup.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        //cancel button intent to group page
        Button cancelButton = findViewById(R.id.buttonCancelGroup);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupsAddNewGroup.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        //add self name and self email to text view
        TextView selfName = findViewById(R.id.groupSelfName);
        TextView selfEmail = findViewById(R.id.groupSelfEmail);
        db.collection("contactList").document(auth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    selfName.setText(documentSnapshot.get("userName").toString());
                    selfEmail.setText(documentSnapshot.get("userEmail").toString());
                }
            }
        });

        //navigate to groups add new friend in new group page
        LinearLayout addNewFriend = findViewById(R.id.group_add_people);
        addNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), GroupsAddNewFriendInNewGroup.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
            }
        });

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add New Group");

        //recyclerview, calling GroupsNewFriendArray
        RecyclerView recyclerView = findViewById(R.id.grouppeoplerecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GroupsNewFriendAddedAdapter adapter = new GroupsNewFriendAddedAdapter(GroupsNewFriendCheckedArray.getInstance().arrayList);
        recyclerView.setAdapter(adapter);
    }
}

