package com.example.billsplittingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QuickSplitMainRecyclerAdapter extends FirestoreRecyclerAdapter<QuickSplitBillObjects, QuickSplitMainRecyclerAdapter.ViewHolder> {
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public QuickSplitMainRecyclerAdapter(FirestoreRecyclerOptions<QuickSplitBillObjects> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(final ViewHolder viewHolder, final int i, final QuickSplitBillObjects quickSplitBillObjects) {
        final boolean isOwner = quickSplitBillObjects.owner.equals(auth.getCurrentUser().getUid());
        boolean isPending = quickSplitBillObjects.status.equals("pending");
        boolean isReady = quickSplitBillObjects.status.equals("Ready to split");
        final boolean isCalculated = quickSplitBillObjects.status.equals("calculated");
        viewHolder.billNameTV.setText(quickSplitBillObjects.getBillName());
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        viewHolder.dateTV.setText(sfd.format(quickSplitBillObjects.getCreateTime().toDate()));

        if (!isOwner && (isPending || isReady)) {
            viewHolder.statusTV.setText("Pending");
        } else if (!isOwner && isCalculated) {
            viewHolder.statusTV.setText("Invoice received");
        } else if (isOwner && isPending) {
            viewHolder.statusTV.setText("Pending");
        } else if (isOwner && isReady) {
            viewHolder.statusTV.setText("Ready to split");
        } else if (isOwner && isCalculated) {
            viewHolder.statusTV.setText("Unsettled bill");
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOwner && !isCalculated) {
                    Intent intent = new Intent(view.getContext(), QuickSplitCreditorDebtorStatusPending.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("billId", quickSplitBillObjects.billId);
                    intent.putExtra("billName", quickSplitBillObjects.billName);
                    view.getContext().startActivity(intent);
                } else if (isOwner && isCalculated) {
                    //intent to lend list page
                    Intent intent = new Intent(view.getContext(), QuickSplitCreditorDebtorStatusUnpaid.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("billId", quickSplitBillObjects.billId);
                    intent.putExtra("billName", quickSplitBillObjects.billName);
                    view.getContext().startActivity(intent);

                } else if (!isOwner && !isCalculated) {
                    //intent to item portion list
                    Intent intent = new Intent(view.getContext(), QuickSplitGeneralViewEnteredShare.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("billId", quickSplitBillObjects.billId);
                    intent.putExtra("billName", quickSplitBillObjects.billName);
                    view.getContext().startActivity(intent);
                } else if (!isOwner && isCalculated) {
                    //intent to invoice
                    Intent intent = new Intent(view.getContext(), QuickSplitDebtorViewInvoice.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("billId", quickSplitBillObjects.billId);
                    intent.putExtra("billName", quickSplitBillObjects.billName);
                    view.getContext().startActivity(intent);
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ziying_quick_recycler_topbottomright, parent, false);
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
    List<QuickSplitItemsPrice> items;

    public QuickSplitCreditorAddAdapter(List<QuickSplitItemsPrice> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ziying_quick_recycler_leftright, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuickSplitItemsPrice item = items.get(position);
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

class QuickSplitGeneralViewPortionAdapter extends RecyclerView.Adapter<QuickSplitGeneralViewPortionAdapter.ViewHolder> {
    List<QuickSplitItemsPortion> items;

    public QuickSplitGeneralViewPortionAdapter(List<QuickSplitItemsPortion> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ziying_quick_recycler_shares_alltext, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuickSplitItemsPortion item = items.get(position);
        holder.itemName.setText(item.name);
        holder.portion.setText(Long.toString(item.portion));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView portion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            portion = itemView.findViewById(R.id.portionEt);
        }
    }

}

class QuickSplitGeneralEnterPortionAdapter extends RecyclerView.Adapter<QuickSplitGeneralEnterPortionAdapter.ViewHolder> {
    List<QuickSplitItemsPrice> items;

    public QuickSplitGeneralEnterPortionAdapter(List<QuickSplitItemsPrice> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ziying_quick_recycler_shares_edittext, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuickSplitItemsPrice item = items.get(position);
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

class QuickSplitCreditorDebtorStatusAdapter extends FirestoreRecyclerAdapter<QuickSplitDebtor, QuickSplitCreditorDebtorStatusAdapter.ViewHolder> {
    String billId;
    String billName;
    Context context;

    public QuickSplitCreditorDebtorStatusAdapter(FirestoreRecyclerOptions<QuickSplitDebtor> options, String billId, String billName, Context context) {
        super(options);
        this.billId = billId;
        this.billName = billName;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull final QuickSplitDebtor quickSplitSplitWith) {
        viewHolder.debtorStatus.setText(QuickSplitMemberStatus.getStatusInString(quickSplitSplitWith.status));
        viewHolder.debtorName.setText(quickSplitSplitWith.displayName);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), QuickSplitGeneralViewEnteredShare.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("billId", billId);
                intent.putExtra("billName", billName);
                intent.putExtra("creditor", true);
                intent.putExtra("debtorUid", quickSplitSplitWith.uid);
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ziying_quick_recycler_leftright, parent, false);
        return new ViewHolder(v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView debtorName;
        TextView debtorStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            debtorName = itemView.findViewById(R.id.itemName);
            debtorStatus = itemView.findViewById(R.id.itemPrice);
        }
    }
}

class QuickSplitPaidStatusListAdapter extends RecyclerView.Adapter<QuickSplitPaidStatusListAdapter.ViewHolder> {
    List<QuickSplitDebtor> arrayList;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String billId;

    public QuickSplitPaidStatusListAdapter(List<QuickSplitDebtor> arrayList, Context context, String billId) {
        this.arrayList = arrayList;
        this.context = context;
        this.billId = billId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ziying_quick_recycler_leftrightcenter, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final QuickSplitDebtor debtor = arrayList.get(position);
        holder.debtorName.setText(debtor.displayName);
        holder.debtorAmount.setText(debtor.amount.toString());

        if (debtor.displayName.equals("Me")) {
            Map status = new HashMap();
            status.put("status", QuickSplitMemberStatus.PAID);
            db.collection("QuickSplit").document(billId)
                    .collection("splitWith").document(debtor.uid)
                    .set(status, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Fail to update", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        if (debtor.status != QuickSplitMemberStatus.PAID) {
            holder.debtorStatus.setText("Unpaid");
        } else {
            holder.debtorStatus.setText("Paid");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (debtor.status != QuickSplitMemberStatus.PAID) {

                    final androidx.appcompat.app.AlertDialog dialog = new AlertDialog.Builder(context)
                            .setMessage(debtor.displayName + " paid you RM" + debtor.amount + " ?")
                            .setPositiveButton("Yes", null)
                            .setNegativeButton("Cancel", null)
                            .setTitle("Settle up")
                            .setCancelable(false)
                            .create();
                    dialog.show();
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Map status = new HashMap();
                            status.put("status", QuickSplitMemberStatus.PAID);
                            db.collection("QuickSplit").document(billId)
                                    .collection("splitWith").document(debtor.uid)
                                    .set(status, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Fail to update", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                        }

                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView debtorName;
        TextView debtorStatus;
        TextView debtorAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            debtorName = itemView.findViewById(R.id.debtorName);
            debtorStatus = itemView.findViewById(R.id.debtorStatus);
            debtorAmount = itemView.findViewById(R.id.debtorAmount);
        }
    }
}

class QuickSplitInvoiceAdapter extends RecyclerView.Adapter<QuickSplitInvoiceAdapter.ViewHolder> {
    List<QuickSplitInvoiceItem> arrayList;


    public QuickSplitInvoiceAdapter(List<QuickSplitInvoiceItem> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ziying_quick_recycler_5columns, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final QuickSplitInvoiceItem item = arrayList.get(position);
        holder.itemName.setText(item.itemName);
        holder.itemPrice.setText(item.price.toString());
        holder.totalShare.setText(item.totalPortion.toString());
        holder.share.setText(item.dividePortion.toString());
        holder.totalAmount.setText(item.divideAmount.toString());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView itemPrice;
        TextView totalShare;
        TextView share;
        TextView totalAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemNameV);
            itemPrice = itemView.findViewById(R.id.itemPriceV);
            totalShare = itemView.findViewById(R.id.totalShareV);
            share = itemView.findViewById(R.id.shareV);
            totalAmount = itemView.findViewById(R.id.totalAmountV);

        }
    }
}