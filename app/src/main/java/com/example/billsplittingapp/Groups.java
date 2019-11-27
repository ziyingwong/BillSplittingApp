package com.example.billsplittingapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Groups extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    GroupsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.groups, container, false);
        RecyclerView recycler = v.findViewById(R.id.grouprecycler);
        String uid = auth.getCurrentUser().getUid();
        setHasOptionsMenu(true);

        Query query = db.collection("Groups").whereArrayContains("userArray", uid);
        FirestoreRecyclerOptions<GroupsObject> options = new FirestoreRecyclerOptions.Builder<GroupsObject>()
                .setQuery(query, GroupsObject.class)
                .build();
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new GroupsAdapter(options);
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

//    db.collection("Groups").whereEqualTo("user",auth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
//        @Override
//        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//            if(e!=null){
//                Log.e("mytag",e.getMessage());
//            }
//            if(queryDocumentSnapshots.size()>0){
//                Log.e("mytag",queryDocumentSnapshots.getDocuments().toString());
//            }else{
//                Log.e("mytag","no group");
//
//            }
//        }
//    });

}

