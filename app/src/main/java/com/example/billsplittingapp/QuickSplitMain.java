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

public class QuickSplitMain extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    QuickSplitMainRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ziying_quick_main, container, false);
        RecyclerView recycler = v.findViewById(R.id.quickMainRecycler);
        String uid = auth.getCurrentUser().getUid();
        setHasOptionsMenu(true);

        Query query = db.collection("QuickSplit").whereArrayContains("splitWith", uid).orderBy("createTime", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<QuickSplitBillObjects> options = new FirestoreRecyclerOptions.Builder<QuickSplitBillObjects>()
                .setQuery(query, QuickSplitBillObjects.class)
                .build();
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new QuickSplitMainRecyclerAdapter(options);
        adapter.startListening();
        recycler.setAdapter(adapter);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_instantsplit, menu);
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
