package com.example.billsplittingapp;

import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.WriteBatch;

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
    ArrayList<String> added;
    ArrayList<GroupsNewUserObject> addedObject;
    GroupsNewFriendAddedAdapter adapter;
    boolean buttonPressed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_addnewgroup);

        final EditText groupNameField = findViewById(R.id.editGroupName);
        groupNameField.setText(GroupsGetNewGroupName.getInstance().groupName);

        GroupsNewUserObject myProfile = new GroupsNewUserObject();
        myProfile.friendUID = auth.getCurrentUser().getUid();
        db.collection("contactList").document(myProfile.friendUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                adapter.notifyDataSetChanged();
                myProfile.friendName = documentSnapshot.get("userName").toString();
                myProfile.friendEmail = documentSnapshot.get("userEmail").toString();
            }
        });

        added = new ArrayList<>();
        addedObject = new ArrayList<>();

        added.add(auth.getCurrentUser().getUid());
        addedObject.add(myProfile);

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

        Button confirmgroupnameButton = findViewById(R.id.groupnameButton);
        confirmgroupnameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GroupsGetNewGroupName.getInstance().groupName.equals("")) {
                    Toast.makeText(getApplicationContext(), "Group name cannot be blanked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Confirm Group Name", Toast.LENGTH_SHORT).show();
                    Map<String, Object> data = new HashMap<>();
                    Map<String, Double> itemArray = new HashMap<>();
                    Double amount = 0.0;
                    List array = new ArrayList();
                    itemArray.put(auth.getCurrentUser().getUid(), amount);
                    doc = db.collection("Groups").document();
                    array.add(auth.getCurrentUser().getUid());
                    data.put("groupName", GroupsGetNewGroupName.getInstance().groupName);
                    data.put("groupId", doc.getId());
                    data.put("userArray", array);
                    data.put("user", itemArray);
                    doc.set(data);
                    confirmgroupnameButton.setClickable(false);
                    buttonPressed = true;
                }
            }
        });


        //add group to database
        Button addGroupButton = findViewById(R.id.buttonAddGroup);
        addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonPressed == false) {
                    Toast.makeText(GroupsAddNewGroup.this, "You have not yet confirm group name", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
            }
        });

        //cancel button intent to group page
        Button cancelButton = findViewById(R.id.buttonCancelGroup);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonPressed == false) {
                    finish();
                } else {
                    db.collection("Groups").document(doc.getId()).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(GroupsAddNewGroup.this, "Cancel", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(GroupsAddNewGroup.this, "Fail to delete : " + e, Toast.LENGTH_SHORT).show();
                                    Log.e("mytag", e.toString());
                                }
                            });
                }
            }
        });

        //navigate to groups add new friend in new group page
        LinearLayout addNewFriend = findViewById(R.id.group_add_people);
        addNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonPressed == false) {
                    Toast.makeText(GroupsAddNewGroup.this, "You have not yet confirm group name", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("Groups").document(doc.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                added = (ArrayList) documentSnapshot.get("userArray");
                                Map<String, Object> user = (HashMap) documentSnapshot.get("user");
                                db.collection("contactList").document(auth.getCurrentUser().getUid()).collection("friend").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        Map<String, String> notAdded = new HashMap<>();
                                        for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                                            if (!added.contains(queryDocumentSnapshots.getDocuments().get(i).getId())) {
                                                notAdded.put(queryDocumentSnapshots.getDocuments().get(i).getId(), queryDocumentSnapshots.getDocuments().get(i).get("friendName").toString());
                                            }
                                        }

                                        AlertDialog.Builder dialog = new AlertDialog.Builder(GroupsAddNewGroup.this);
                                        dialog.setTitle("Add Friends to Group");
                                        LinearLayout layout = new LinearLayout(GroupsAddNewGroup.this);
                                        layout.setOrientation(LinearLayout.VERTICAL);
                                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 600);
                                        lp.setMargins(50, 50, 70, 0);
                                        View radioLayout = LayoutInflater.from(GroupsAddNewGroup.this).inflate(R.layout.other_radiobuttons, layout, false);
                                        RadioGroup radioGroup = radioLayout.findViewById(R.id.radioGroup);
                                        ArrayList<String> selectedArray = new ArrayList<>();
                                        ArrayList<GroupsNewUserObject> selectedObject = new ArrayList<>();
                                        for (String key : notAdded.keySet()) {
                                            CheckBox checkBox = new CheckBox(GroupsAddNewGroup.this);
                                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (isChecked) {
                                                        GroupsNewUserObject userProfile = new GroupsNewUserObject();
                                                        selectedArray.add(key);
                                                        userProfile.friendUID = key;
                                                        db.collection("contactList").document(userProfile.friendUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                userProfile.friendName = documentSnapshot.get("userName").toString();
                                                                userProfile.friendEmail = documentSnapshot.get("userEmail").toString();
                                                            }
                                                        });
                                                        selectedObject.add(userProfile);
                                                    } else {
                                                        GroupsNewUserObject userProfile = new GroupsNewUserObject();
                                                        userProfile.friendUID = key;
                                                        db.collection("contactList").document(userProfile.friendUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                userProfile.friendName = documentSnapshot.get("userName").toString();
                                                                userProfile.friendEmail = documentSnapshot.get("userEmail").toString();
                                                            }
                                                        });
                                                        selectedArray.remove(key);
                                                    }
                                                }

                                            });
//                                        checkBox.setId(key);
                                            checkBox.setText(notAdded.get(key));
                                            radioGroup.addView(checkBox);
                                        }

                                        layout.addView(radioLayout, lp);
                                        dialog.setView(layout);
                                        dialog.setNegativeButton("Cancel", null);
                                        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                added.addAll(selectedArray);
                                                addedObject.addAll(selectedObject);
                                                for (String key : selectedArray) {
                                                    user.put(key, 0.0);
                                                }
                                                db.runBatch(new WriteBatch.Function() {
                                                    @Override
                                                    public void apply(@NonNull WriteBatch writeBatch) {
                                                        writeBatch.update(db.collection("Groups").document(doc.getId()), "user", user);
                                                        writeBatch.update(db.collection("Groups").document(doc.getId()), "userArray", added);
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        adapter.notifyDataSetChanged();
                                                        Toast.makeText(GroupsAddNewGroup.this, "Updated", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });

                                        dialog.show();

                                    }
                                });
                            }
                        }
                    });
                }
            }

        });

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add New Group");

        //recyclerview, calling GroupsNewFriendArray
        RecyclerView recyclerView = findViewById(R.id.grouppeoplerecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupsNewFriendAddedAdapter(addedObject);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.delete(
                        viewHolder.itemView.getContext(),
                        viewHolder.getAdapterPosition(),
                        doc.getId()
                );
            }


        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallBack);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

}