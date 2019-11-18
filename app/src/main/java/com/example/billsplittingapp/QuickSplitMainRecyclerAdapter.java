package com.example.billsplittingapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.List;


public class QuickSplitMainRecyclerAdapter extends FirestoreRecyclerAdapter<QuickSplitBillObjects, QuickSplitMainRecyclerAdapter.ViewHolder> {

    public QuickSplitMainRecyclerAdapter(FirestoreRecyclerOptions<QuickSplitBillObjects> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, final int i, QuickSplitBillObjects quickSplitBillObjects) {
        viewHolder.billNameTV.setText(quickSplitBillObjects.getBillName());
        viewHolder.statusTV.setText(quickSplitBillObjects.getStatus());
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        viewHolder.dateTV.setText(sfd.format(quickSplitBillObjects.getCreateTime().toDate()));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("mytag", Integer.toString(i));
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ziying_quick_main_recyclercontent, parent, false);
        return new ViewHolder(v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView billNameTV;
        TextView statusTV;
        TextView dateTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            billNameTV = itemView.findViewById(R.id.billName);
            dateTV = itemView.findViewById(R.id.dateText);
            statusTV = itemView.findViewById(R.id.statusText);
        }
    }
}

class QuickSplitCreditorAddAdapter extends RecyclerView.Adapter<QuickSplitCreditorAddAdapter.ViewHolder> {
    List<QuickSplitItems> items;

    public QuickSplitCreditorAddAdapter(List<QuickSplitItems> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ziying_quick_creditor_additemrecycler, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuickSplitItems item = items.get(position);
        holder.itemName.setText(item.name);
        holder.price.setText(Double.toString(item.price));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.priceText);
        }
    }

}

class QuickSplitGeneralEnterPortionAdapter extends RecyclerView.Adapter<QuickSplitGeneralEnterPortionAdapter.ViewHolder> {
    List<QuickSplitItems> items;

    public QuickSplitGeneralEnterPortionAdapter(List<QuickSplitItems> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ziying_quick_general_itemsportionrecycler, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuickSplitItems item = items.get(position);
        holder.itemName.setText(item.name);
        holder.price.setText(Double.toString(item.price));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.itemPrice);
        }
    }

}