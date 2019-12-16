package com.example.billsplittingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ExpenseDeleteAdapter extends FirestoreRecyclerAdapter<GroupsPaymentObject, ExpenseDeleteAdapter.ExpenseDeleteHolder> {

    private OnItemClickListener listener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public ExpenseDeleteAdapter(@NonNull FirestoreRecyclerOptions<GroupsPaymentObject> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ExpenseDeleteAdapter.ExpenseDeleteHolder viewHolder, int i, @NonNull GroupsPaymentObject groupsPaymentObject) {

        viewHolder.tvExpenseName.setText(groupsPaymentObject.billName);
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        viewHolder.tvDate.setText(date.format(groupsPaymentObject.getCreateTime().toDate()));
    }

    @NonNull
    @Override
    public ExpenseDeleteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_delete_card, parent, false);
        return new ExpenseDeleteHolder(v);
    }

    public void deleteItem (int position){

        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class ExpenseDeleteHolder extends RecyclerView.ViewHolder {

        TextView tvExpenseName;
        TextView tvDate;

        public ExpenseDeleteHolder(@NonNull View itemView) {
            super(itemView);
            tvExpenseName = itemView.findViewById(R.id.tvExpenseName);
            tvDate = itemView.findViewById(R.id.tvDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener (OnItemClickListener listener) {
        this.listener = listener;
    }
}
