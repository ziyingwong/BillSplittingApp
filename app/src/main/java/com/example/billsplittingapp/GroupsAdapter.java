package com.example.billsplittingapp;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GroupsAdapter extends FirestoreRecyclerAdapter<GroupsObject, GroupsAdapter.ViewHolder> {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ArrayList<Double> arrayListTotalAmount = new ArrayList<Double>();

    public GroupsAdapter(FirestoreRecyclerOptions<GroupsObject> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(final ViewHolder viewHolder, final int i, final GroupsObject groupsObjects) {
        arrayListTotalAmount.add(groupsObjects.user.get(auth.getCurrentUser().getUid()));
        viewHolder.groupName.setText(groupsObjects.groupName);
        String stringAmountFormat = String.format("%,.2f", groupsObjects.user.get(auth.getCurrentUser().getUid()));
        viewHolder.amount.setText("RM" + stringAmountFormat);

        if (groupsObjects.user.get(auth.getCurrentUser().getUid()) < 0) {
            viewHolder.amount.setTextColor(Color.parseColor("#D81B60"));
        } else if (groupsObjects.user.get(auth.getCurrentUser().getUid()) > 0) {
            viewHolder.amount.setTextColor(Color.parseColor("#45B39D"));
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GroupsDetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("groupId", groupsObjects.groupId);
                intent.putExtra("groupName", groupsObjects.groupName);
                v.getContext().startActivity(intent);
            }
        });

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

    public ArrayList<Double> getArrayListTotalAmount() {
        return arrayListTotalAmount;
    }
}

class GroupsNewFriendAddedAdapter extends RecyclerView.Adapter<GroupsNewFriendAddedAdapter.ViewHolder> {
    ArrayList<GroupsNewUserObject> items;

    public GroupsNewFriendAddedAdapter(ArrayList<GroupsNewUserObject> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_card_addgroup, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupsNewUserObject user = items.get(position);

        holder.friendName.setText(user.getFriendName());
        holder.friendEmail.setText(user.getFriendEmail());

    }

    @Override
    public int getItemCount() {
        return items.size();
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

class GroupsAddNewPeopleAdapter extends FirestoreRecyclerAdapter<GroupsNewUserObject, GroupsAddNewPeopleAdapter.ViewHolder> {
    public GroupsAddNewPeopleAdapter(FirestoreRecyclerOptions<GroupsNewUserObject> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(final ViewHolder viewHolder, final int i, final GroupsNewUserObject groupsNewUserObject) {
        viewHolder.friendName.setText(groupsNewUserObject.friendName);
        viewHolder.friendEmail.setText(groupsNewUserObject.friendEmail);
        for (int j = 0; j < GroupsNewFriendCheckedArray.getInstance().arrayList.size(); j++) {
            if (GroupsNewFriendCheckedArray.getInstance().arrayList.get(j).friendUID.equals(groupsNewUserObject.getFriendUID())) {
                viewHolder.checkBox.setChecked(true);
            }
        }

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    GroupsNewFriendCheckedArray.getInstance().tempList.add(groupsNewUserObject);
                } else {
                    for (int k = 0; k < GroupsNewFriendCheckedArray.getInstance().arrayList.size(); k++) {
                        if (GroupsNewFriendCheckedArray.getInstance().arrayList.get(k).friendUID.equals(groupsNewUserObject.getFriendUID())) {
                            GroupsNewFriendCheckedArray.getInstance().arrayList.remove(k);
                        }
                        if (GroupsNewFriendCheckedArray.getInstance().tempList.get(k).friendUID.equals(groupsNewUserObject.getFriendUID())) {
                            GroupsNewFriendCheckedArray.getInstance().tempList.remove(k);

                        }
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_card_addfriend_newgroup, parent, false);
        return new ViewHolder(v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        TextView friendEmail;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            friendName = itemView.findViewById(R.id.groupAddFriendName);
            friendEmail = itemView.findViewById(R.id.groupAddFriendEmail);
        }
    }
}

class GroupsAddPeopleSettingAdapter extends FirestoreRecyclerAdapter<GroupsNewUserObject, GroupsAddPeopleSettingAdapter.ViewHolder> {
    public GroupsAddPeopleSettingAdapter(FirestoreRecyclerOptions<GroupsNewUserObject> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(final ViewHolder viewHolder, final int i, final GroupsNewUserObject groupsNewUserObject) {
        viewHolder.friendName.setText(groupsNewUserObject.friendName);
        viewHolder.friendEmail.setText(groupsNewUserObject.friendEmail);
        for (int j = 0; j < GroupsNewFriendCheckedArray.getInstance().arrayList.size(); j++) {
            if (GroupsNewFriendCheckedArray.getInstance().arrayList.get(j).friendUID.equals(groupsNewUserObject.getFriendUID())) {
                viewHolder.checkBox.setChecked(true);
            }
        }

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    GroupsNewFriendCheckedArray.getInstance().tempList.add(groupsNewUserObject);
                } else {
                    GroupsNewFriendCheckedArray.getInstance().deleteList.add(groupsNewUserObject);
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_card_addfriend_newgroup, parent, false);
        return new ViewHolder(v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        TextView friendEmail;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            friendName = itemView.findViewById(R.id.groupAddFriendName);
            friendEmail = itemView.findViewById(R.id.groupAddFriendEmail);
        }
    }
}

class GroupsPaymentAdapter extends FirestoreRecyclerAdapter<GroupsPaymentObject, GroupsPaymentAdapter.ViewHolder> {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public GroupsPaymentAdapter(FirestoreRecyclerOptions<GroupsPaymentObject> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull GroupsPaymentAdapter.ViewHolder viewHolder, int i, @NonNull GroupsPaymentObject groupsPaymentObject) {
        viewHolder.expensename.setText(groupsPaymentObject.billName);
        if (groupsPaymentObject.splitUser.contains(auth.getCurrentUser().getUid())) {
            String splitAmountBox = String.format("%,.2f", groupsPaymentObject.price);
            viewHolder.amountborrowed.setText("RM" + splitAmountBox);
            if (groupsPaymentObject.payer.equals(auth.getCurrentUser().getUid().toString())) {
                viewHolder.lentborrow.setText("You lent");
                viewHolder.lentborrow.setTextColor(Color.parseColor("#45B39D"));
                viewHolder.amountborrowed.setTextColor(Color.parseColor("#45B39D"));
            } else {
                viewHolder.lentborrow.setText("You borrowed");
                viewHolder.lentborrow.setTextColor(Color.parseColor("#D81B60"));
                viewHolder.amountborrowed.setTextColor(Color.parseColor("#D81B60"));
            }
        } else {
            viewHolder.lentborrow.setText("not involved");
            viewHolder.amountborrowed.setText("");
        }

        String priceBox = String.format("%,.2f", groupsPaymentObject.price);

        final DocumentReference documentReference = db.collection("contactList").document(groupsPaymentObject.payer);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (groupsPaymentObject.splitUser.contains(auth.getCurrentUser().getUid().toString())) {
                    viewHolder.expenseactivity.setText(documentSnapshot.get("userName").toString() + " paid RM" + priceBox);
                } else {
                    viewHolder.expenseactivity.setText("You are not involved");
                }
            }
        });

    }

    @NonNull
    @Override
    public GroupsPaymentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_card_groupdetails, parent, false);
        return new ViewHolder(v);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView expensename;
        TextView expenseactivity;
        TextView lentborrow;
        TextView amountborrowed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            expensename = itemView.findViewById(R.id.expensename);
            expenseactivity = itemView.findViewById(R.id.expensesactivity);
            lentborrow = itemView.findViewById(R.id.lentborrow);
            amountborrowed = itemView.findViewById(R.id.amountborrowed);
        }
    }
}

class GroupsSettingAdapter extends RecyclerView.Adapter<GroupsSettingAdapter.ViewHolder> {
    ArrayList<GroupsAmountUserObject> items;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public GroupsSettingAdapter(ArrayList<GroupsAmountUserObject> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_card_settingpeople, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupsAmountUserObject user = items.get(position);
        db.collection("contactList").document(user.uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    holder.userName.setText(documentSnapshot.get("userName").toString());
                    holder.userEmail.setText(documentSnapshot.get("userEmail").toString());
                    Object boxAmount = user.amount;
                    String stringAmount = boxAmount.toString();
                    Double doubleAmount = Double.parseDouble(stringAmount);
                    String stringAmountFormat = String.format("%,.2f", doubleAmount);
                    holder.amountowed.setText("RM" + stringAmountFormat);
//                    GroupsSettingFriendArray.getInstance().settingList.add(items.get(position));

                    if (doubleAmount > 0) {
                        holder.amountowed.setTextColor(Color.parseColor("#45B39D"));
                        holder.owes.setText("gets back");
                        holder.owes.setTextColor(Color.parseColor("#45B39D"));
                    } else if (doubleAmount < 0) {
                        holder.amountowed.setTextColor(Color.parseColor("#D81B60"));
                        holder.owes.setText("owes");
                        holder.owes.setTextColor(Color.parseColor("#D81B60"));
                    } else {
                        holder.owes.setText("settled up");
                    }

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView userEmail;
        TextView owes;
        TextView amountowed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userEmail = itemView.findViewById(R.id.userEmail);
            owes = itemView.findViewById(R.id.owes);
            amountowed = itemView.findViewById(R.id.amountowed);
        }

    }
}

//class GroupsSettingFriendAdapter extends RecyclerView.Adapter<GroupsSettingFriendAdapter.ViewHolder> {
//    ArrayList<GroupsAmountUserObject> items;
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//    public GroupsSettingFriendAdapter(ArrayList<GroupsAmountUserObject> items) {
//        this.items = items;
//    }
//
//    @NonNull
//    @Override
//    public GroupsSettingFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_card_addfriend_newgroup, parent, false);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull GroupsSettingFriendAdapter.ViewHolder holder, int position) {
//        GroupsAmountUserObject user = items.get(position);
//        db.collection("contactList").document(user.uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                holder.friendName.setText(documentSnapshot.get("userName").toString());
//                holder.friendEmail.setText(documentSnapshot.get("userEmail").toString());
//                for (int i = 0; i < GroupsSettingFriendArray.getInstance().settingList.size(); i++) {
//                    if(user.uid.equals(GroupsSettingFriendArray.getInstance().settingList.get(i).uid)) {
//                        holder.checkBox.setChecked(true);
//                    }
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    class ViewHolder extends RecyclerView.ViewHolder {
//        TextView friendName;
//        TextView friendEmail;
//        CheckBox checkBox;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            friendName = itemView.findViewById(R.id.groupAddFriendName);
//            friendEmail = itemView.findViewById(R.id.groupAddFriendEmail);
//            checkBox = itemView.findViewById(R.id.checkBox);
//        }
//    }
//}


class GroupsBalancesAdapter extends RecyclerView.Adapter<GroupsBalancesAdapter.ViewHolder> {
    ArrayList<GroupsAmountUserObject> items;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public GroupsBalancesAdapter(ArrayList<GroupsAmountUserObject> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_card_balances, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupsAmountUserObject user = items.get(position);
        db.collection("contactList").document(user.uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Object boxAmount = user.amount;
                    String stringAmount = boxAmount.toString();
                    Double doubleAmount = Double.parseDouble(stringAmount);
                    String stringAmountFormat = String.format("%,.2f", doubleAmount);

                    String username = documentSnapshot.get("userName").toString();

                    if (doubleAmount > 0) {
                        holder.balanceText.setTextColor(Color.parseColor("#45B39D"));
                        holder.balanceText.setText(username + " gets back RM" + stringAmountFormat + " in total");
                    } else if (doubleAmount < 0) {
                        holder.balanceText.setTextColor(Color.parseColor("#D81B60"));
                        holder.balanceText.setText(username + " owes RM" + stringAmountFormat + " in total");
                    } else {
                        holder.balanceText.setText(username + " is being settled up");
                    }

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView balanceText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            balanceText = itemView.findViewById(R.id.groupsBalance);
        }

    }
}