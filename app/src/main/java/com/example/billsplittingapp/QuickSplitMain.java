package com.example.billsplittingapp;

import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class QuickSplitMain extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ziying_main, container, false);
        RecyclerView recycler = v.findViewById(R.id.quickMainRecycler);
        String uid = auth.getCurrentUser().getUid();
        setHasOptionsMenu(true);

        Query query = db.collection("InstantSplit").whereArrayContains("splitWith", uid).orderBy("createTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<QuickSplitBillObjects> options = new FirestoreRecyclerOptions.Builder<QuickSplitBillObjects>()
                .setQuery(query, QuickSplitBillObjects.class)
                .build();
        if(options.getSnapshots().isEmpty()){
            Log.e("mytag","empty");
        }
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        QuickSplitMainRecyclerAdapter adapter = new QuickSplitMainRecyclerAdapter(options);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
