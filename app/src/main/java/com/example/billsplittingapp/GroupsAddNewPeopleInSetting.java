package com.example.billsplittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupsAddNewPeopleInSetting extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    GroupsAddPeopleSettingAdapter adapter;
    String groupName;
    Map<String, Double> oriUserMap;
    double tempValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_addnewfriend);

        //toolbar
        String groupId = getIntent().getStringExtra("groupId");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Friend");

        db.collection("Groups").document(groupId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                groupName = documentSnapshot.get("groupName").toString();
            }
        });

        //recyclerview
        GroupsNewFriendCheckedArray.getInstance().tempList = (ArrayList<GroupsNewUserObject>) GroupsNewFriendCheckedArray.getInstance().arrayList.clone();
        String uid = auth.getCurrentUser().getUid();
        RecyclerView recycler = findViewById(R.id.grouppeoplerecycler);
        Query query = db.collection("contactList").document(uid).collection("friend");
        FirestoreRecyclerOptions<GroupsNewUserObject> options = new FirestoreRecyclerOptions.Builder<GroupsNewUserObject>()
                .setQuery(query, GroupsNewUserObject.class)
                .build();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupsAddPeopleSettingAdapter(options);
        adapter.startListening();
        recycler.setAdapter(adapter);

        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupsNewFriendCheckedArray.getInstance().tempList.clear();
                Intent intent = new Intent(GroupsAddNewPeopleInSetting.this, GroupsDetailsSetting.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });


        Button addNewFriend = findViewById(R.id.buttonNewFriend);
        addNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupsNewFriendCheckedArray.getInstance().arrayList = (ArrayList<GroupsNewUserObject>) GroupsNewFriendCheckedArray.getInstance().tempList.clone();
                ArrayList<String> userArray = new ArrayList<>();
                Map<String, Double> user = new HashMap<>();
                for (GroupsNewUserObject userDetails : GroupsNewFriendCheckedArray.getInstance().arrayList) {
                    userArray.add(userDetails.getFriendUID());
                    user.put(userDetails.getFriendUID(), 0.00);
                }

                Map<String, Object> data = new HashMap<>();
                data.put("userArray", userArray);
                data.put("user", user);
                db.collection("Groups").document(groupId).set(data, SetOptions.merge());

                step1(groupId);


//                    Map<String, Object> deleteUser = new HashMap<>();
//                    Map<String, Double> deleteUserMap = new HashMap<>();
//                    final String friendUID = GroupsNewFriendCheckedArray.getInstance().deleteList.get(i).friendUID;
//
//                    db.collection("Groups").document(groupId)
//                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                                @Override
//                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//
//                                    Map<String, Long> map = (HashMap) documentSnapshot.get("user");
//                                    tempValue = map.get(friendUID);
//                                }
//                            });
//
//                    deleteUserMap.put(GroupsNewFriendCheckedArray.getInstance().deleteList.get(i).getFriendUID(),
//                            tempValue);
//                    deleteUser.put("user", deleteUserMap);
//                    db.collection("Groups").document(groupId).update("user", FieldValue.arrayRemove(deleteUser));

            }

        });
    }

    public void step1(String groupId) {
        db.collection("Groups").document(groupId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String,Double> ori = (HashMap) documentSnapshot.get("user");
                step2(ori, groupId);
            }
        });

    }

    public void step2(Map<String,Double> oriUserMap , String groupId) {
        for (int i = 0; i < GroupsNewFriendCheckedArray.getInstance().deleteList.size(); i++) {
            db.collection("Groups").document(groupId).update("userArray",
                    FieldValue.arrayRemove(GroupsNewFriendCheckedArray.getInstance().deleteList.get(i).friendUID));
            oriUserMap.remove(GroupsNewFriendCheckedArray.getInstance().deleteList.get(i).getFriendUID());
        }
        step3(oriUserMap,groupId);
    }
    public void step3(Map<String,Double> oriUserMap , String groupId){
        db.collection("Groups").document(groupId).update("user",oriUserMap);
        GroupsNewFriendCheckedArray.getInstance().deleteList.clear();
        Intent intent = new Intent(GroupsAddNewPeopleInSetting.this, GroupsDetailsSetting.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("groupId", groupId);
        intent.putExtra("groupName", groupName);
        startActivity(intent);
    }
}


