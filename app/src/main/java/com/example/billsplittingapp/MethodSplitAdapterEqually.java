package com.example.billsplittingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MethodSplitAdapterEqually extends RecyclerView.Adapter<MethodSplitAdapterEqually.ViewHolder> {

    ArrayList<MethodSplitObject> items;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Double total;

    public MethodSplitAdapterEqually(ArrayList<MethodSplitObject> items, Double total) {
        this.items = items;
        this.total = total;
    }

    @NonNull
    @Override
    public MethodSplitAdapterEqually.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.method_card_equalvalue, parent, false);
        return new MethodSplitAdapterEqually.ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull MethodSplitAdapterEqually.ViewHolder holder, int position) {
        MethodSplitObject user = items.get(position);
        DecimalFormat df2 = new DecimalFormat("#.##");
        db.collection("contactList").document(user.uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    holder.userName.setText(documentSnapshot.get("userName").toString());
                    holder.userEmail.setText(documentSnapshot.get("userEmail").toString());
                    double d = total/getItemCount();
                    String str = String.format("%1.2f", d);
                    d = Double.valueOf(str);
                    holder.userPay.setText(str);

                }
            }
        });

    }

    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView userEmail;
        TextView userPay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.musername);
            userEmail = itemView.findViewById(R.id.mamount);
            userPay = itemView.findViewById(R.id.tvtopay);
        }

    }


}
