package com.example.billsplittingapp;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import java.util.Set;

public class Groups extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    GroupsAdapter adapter;
    Double totalAmount = 0.00;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.groups, container, false);
        //addition of total amount
        String uid = auth.getCurrentUser().getUid();
        final TextView totalAmountString = v.findViewById(R.id.groups_totalamount);
        Query query2 = db.collection("Groups").whereArrayContains("userArray", uid);
        query2.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots.size() > 0) {
                    totalAmount = 0.00;

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Map<String, Double> map = (HashMap) doc.get("user");

                        Object singleAmount = map.get(auth.getCurrentUser().getUid());
                        String singleAmountString = singleAmount.toString();
                        Double singleAmountDouble = Double.parseDouble(singleAmountString);
                        totalAmount += singleAmountDouble;
                        if (totalAmount > 0) {
                            totalAmountString.setTextColor(Color.parseColor("#45B39D"));
                            totalAmountString.setText("You are owed RM");
                        } else {
                            totalAmountString.setTextColor(Color.parseColor("#D81B60"));
                            totalAmountString.setText("You owe RM");
                        }
                        String totalAmountBox = String.format("%,.2f", (totalAmount*-1));
                        totalAmountString.setText(totalAmountString.getText() + totalAmountBox);
                    }
                }
            }

        });

        //Recycler, showing out groups for each user by searching if the groups contain the user uid
        RecyclerView recycler = v.findViewById(R.id.grouprecycler);

        setHasOptionsMenu(true);

        Query query = db.collection("Groups").whereArrayContains("userArray", uid);
        FirestoreRecyclerOptions<GroupsObject> options = new FirestoreRecyclerOptions.Builder<GroupsObject>()
                .setQuery(query, GroupsObject.class)
                .build();
        recycler.setLayoutManager(new

                LinearLayoutManager(getActivity()));
        adapter = new

                GroupsAdapter(options);
        adapter.startListening();
        recycler.setAdapter(adapter);
        return v;


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_groups_addnew, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addGroup) {
            Intent intent = new Intent(getActivity(), GroupsAddNewGroup.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            GroupsNewFriendCheckedArray.getInstance().arrayList.clear();
            GroupsGetNewGroupName.getInstance().groupName = "";
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}

