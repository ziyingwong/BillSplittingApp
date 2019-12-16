package com.example.billsplittingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupsDetailsSetting extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    GroupsSettingAdapter adapter;
    String groupId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_details_settings);
        final String groupName = getIntent().getStringExtra("groupName");
        groupId = getIntent().getStringExtra("groupId");

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

        //intent to add new friend setting
        LinearLayout addFriendSetting = findViewById(R.id.add_people_groupsetting);
        addFriendSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Groups").document(groupId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            ArrayList<String> added = (ArrayList) documentSnapshot.get("userArray");
                            db.collection("contactList").document(auth.getCurrentUser().getUid()).collection("friend").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    ArrayList<String> notAdded = new ArrayList<>();
                                    for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                        if (!added.contains(queryDocumentSnapshots.getDocuments().get(i).getId())) {
                                            notAdded.add(queryDocumentSnapshots.getDocuments().get(i).getId());
                                        }
                                    }

                                }
                            });
                        }
                    }
                });
            }
        });
//        addFriendSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GroupsNewFriendCheckedArray.getInstance().arrayList.clear();
//
//                for (GroupsAmountUserObject user : arrayList) {
//                    GroupsNewUserObject olduser = new GroupsNewUserObject();
//                    olduser.friendUID = user.uid;
//                    GroupsNewFriendCheckedArray.getInstance().arrayList.add(olduser);
//
//                }
//                Intent intent = new Intent(GroupsDetailsSetting.this, GroupsAddNewPeopleInSetting.class);
//                intent.putExtra("groupId", groupId);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                startActivity(intent);
//            }
//        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        adapter = new GroupsSettingAdapter(arrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //putting group name into groupsgetnewgroupname singleton
        GroupsGetNewGroupName.getInstance().groupName = groupName;

        EditText editGroupName = findViewById(R.id.editGroupName);
        editGroupName.setText(GroupsGetNewGroupName.getInstance().groupName);

        editGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String groupNameString = editGroupName.getText().toString();
                GroupsGetNewGroupName.getInstance().groupName = groupNameString;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Group Settings");

        //cancel button press
        Button cancelButton = findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                Intent intent = new Intent(GroupsDetailsSetting.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                startActivity(intent);
            }
        });

        //save button press
        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editGroupNameString = GroupsGetNewGroupName.getInstance().groupName;
                Map<String, Object> data = new HashMap<>();
                final DocumentReference doc = db.collection("Groups").document(groupId);
                data.put("groupName", editGroupNameString);
                doc.update(data);
                Toast.makeText(getApplicationContext(), "Group updated", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_groups_deletegroup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.deleteGroup) {

            AlertDialog dialog = new AlertDialog.Builder(GroupsDetailsSetting.this)
                    .setMessage("Are you sure you want to delete group?")
                    .setPositiveButton("Yes", null)
                    .setNegativeButton("Cancel", null)
                    .setTitle("Delete group")
                    .setCancelable(false)
                    .create();
            dialog.show();
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("Groups").document(groupId).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Group deleted", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Fail to delete : " + e, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    Log.e("mytag", e.toString());
                                }
                            });
                }
            });
        }
        return true;
    }


}
