package com.example.billsplittingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MethodSplitAdapterPercentage extends RecyclerView.Adapter<MethodSplitAdapterPercentage.ViewHolder> {

    ArrayList<MethodSplitObject> items;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Double total;




    public MethodSplitAdapterPercentage(ArrayList<MethodSplitObject> items, Double total) {
        this.items = items;
        this.total = total;
    }

    @NonNull
    @Override
    public MethodSplitAdapterPercentage.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.method_card_percentage, parent, false);
        return new MethodSplitAdapterPercentage.ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull MethodSplitAdapterPercentage.ViewHolder holder, int position) {
        MethodSplitObject user = items.get(position);
        db.collection("contactList").document(user.uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    holder.userName.setText(documentSnapshot.get("userName").toString());
                    holder.userEmail.setText(documentSnapshot.get("userEmail").toString());
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
        EditText userPay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.musername);
            userEmail = itemView.findViewById(R.id.mamount);
            userPay = itemView.findViewById(R.id.etpercentage);
        }

    }


}
