package com.example.billsplittingapp;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class GroupsAdapter extends FirestoreRecyclerAdapter<GroupsObject, GroupsAdapter.ViewHolder> {
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public GroupsAdapter(FirestoreRecyclerOptions<GroupsObject> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(final ViewHolder viewHolder, final int i, final GroupsObject groupsObjects) {
        viewHolder.groupName.setText(groupsObjects.groupName);
        viewHolder.amount.setText(groupsObjects.user.get(auth.getCurrentUser().getUid()).toString());

        if (groupsObjects.user.get(auth.getCurrentUser().getUid()) < 0) {
            viewHolder.amount.setTextColor(Color.RED);
        } else {
            viewHolder.amount.setTextColor(Color.GREEN);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_card, parent, false);
        return new ViewHolder(v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName);
            amount = itemView.findViewById(R.id.amount);
        }
    }
}

class GroupsAddNewPeopleAdapter extends FirestoreRecyclerAdapter<GroupsNewUserObject, GroupsAddNewPeopleAdapter.ViewHolder> {
    public GroupsAddNewPeopleAdapter(FirestoreRecyclerOptions<GroupsNewUserObject> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(final ViewHolder viewHolder, final int i, final GroupsNewUserObject groupsNewUserObject) {
        viewHolder.friendName.setText(groupsNewUserObject.friendName);
        viewHolder.friendEmail.setText(groupsNewUserObject.friendEmail);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_card_addgroup, parent, false);
        return new ViewHolder(v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        TextView friendEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.groupUserName);
            friendEmail = itemView.findViewById(R.id.groupUserEmail);
        }
    }
}
