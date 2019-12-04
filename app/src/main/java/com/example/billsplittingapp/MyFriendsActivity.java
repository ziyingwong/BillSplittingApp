package com.example.billsplittingapp;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class MyFriendsActivity extends Fragment {
private FirebaseFirestore firestore;
private FirebaseAuth auth;
private TextView t1, t2;
private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_my_friends, container, false);
        setHasOptionsMenu(true);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
//        addFriend("Amirul","Owed RM50");
        getFriendList(v);
        return v;
    }


    public void getFriendList(View v){
        progressBar = (ProgressBar)v.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        final LinearLayout linearLayout = (LinearLayout)v.findViewById(R.id.layout_contact_list);
        CollectionReference collectionReference =
                firestore.collection("contactList");
                collectionReference
                .document(auth.getUid())
                .collection("friend")
                .orderBy("friendName", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    MyFriendsObject object = documentSnapshot.toObject(MyFriendsObject.class);
                    final String name = object.getFriendName();
                    final String status = object.getFriendStatus();
                    final String email = object.getFriendEmail();
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.activity_my_friends_field, linearLayout, false);
                    linearLayout.addView(rowView, linearLayout.getChildCount() - 1);
                    t1 = (TextView)rowView.findViewById(R.id.name_text);
                    t2 = (TextView)rowView.findViewById(R.id.status_text);
                    t1.setText(name);
                    t2.setText(status);
                    if(!status.equalsIgnoreCase("Settled Up")){
                        t2.setTextColor(Color.parseColor("#B22222"));
                    }
                    Log.e("TAG", "name: "+name+" status: "+status );
                    rowView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getContext(),ReminderActivity.class);
                            i.putExtra("name",name);
                            i.putExtra("status",status);
                            i.putExtra("email",email);
                            Log.e("TAG", "MyFriendsActivityClass: email is: "+email );
                            startActivity(i);
                        }
                    });
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }




    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_contact_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.addFriend:
                Intent i = new Intent (getActivity(),AddFriendActivity.class);
                startActivity(i);
        }
        return true;
    }

}
