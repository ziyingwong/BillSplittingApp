package com.example.billsplittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.resources.TextAppearance;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuickSplitCreditorAdd extends AppCompatActivity {
    List<QuickSplitItems> itemsArray = new ArrayList<>();
    Map<String, Double> itemArray = new HashMap<>();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ziying_quick_creditor_add);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add Bill");

        final Button addButton = findViewById(R.id.buttonAdd);
        ImageView addImage = findViewById(R.id.addItemButton);
        final EditText title = findViewById(R.id.description);
        final EditText tax = findViewById(R.id.tax);
        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        //recycler view
        RecyclerView recyclerView = findViewById(R.id.itemsBox);
        final QuickSplitCreditorAddAdapter adapter = new QuickSplitCreditorAddAdapter(itemsArray);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //add item
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final View v = getLayoutInflater().inflate(R.layout.ziying_quick_creditor_additemdialog, null);
                final AlertDialog dialog = new AlertDialog.Builder(QuickSplitCreditorAdd.this)
                        .setPositiveButton("Add", null)
                        .setNegativeButton("Cancel", null)
                        .setView(v)
                        .setTitle("Add item")
                        .setCancelable(false)
                        .create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText itemET = v.findViewById(R.id.itemName);
                        String itemName = itemET.getText().toString();
                        EditText priceET = v.findViewById(R.id.priceText);

                        if (itemName.isEmpty()) {
                            Toast.makeText(dialog.getContext(), "Please enter description", Toast.LENGTH_SHORT).show();
                        } else if (priceET.getText().toString().isEmpty()) {
                            Toast.makeText(view.getContext(), "Please enter price", Toast.LENGTH_SHORT).show();
                        } else {
                            double price = Double.parseDouble(priceET.getText().toString());
                            itemsArray.add(new QuickSplitItems(itemName, price));
                            itemArray.put(itemName, price);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        //remove item
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Toast.makeText(QuickSplitCreditorAdd.this, "Deleted" + itemsArray.get(position).name, Toast.LENGTH_SHORT).show();
                itemArray.remove(itemsArray.get(position).name);
                itemsArray.remove(position);
                adapter.notifyDataSetChanged();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        //onclick
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.getText().length() < 1) {
                    Toast.makeText(QuickSplitCreditorAdd.this, "Please enter description", Toast.LENGTH_SHORT).show();
                } else if (itemsArray.size() == 0) {
                    Toast.makeText(QuickSplitCreditorAdd.this, "You must add at least 1 item", Toast.LENGTH_SHORT).show();
                } else if (tax.getText().length() < 1) {
                    final AlertDialog dialog = new AlertDialog.Builder(QuickSplitCreditorAdd.this)
                            .setTitle("No tax entered")
                            .setMessage("Are you sure you want to create bill without tax?")
                            .setNegativeButton("Create Bill", null)
                            .setPositiveButton("Add tax", null)
                            .create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tax.setText("0");
                            addButton.performClick();
                            dialog.dismiss();
                        }
                    });
                } else {

                    //calculate total
                    double total = 0;
                    for (QuickSplitItems item : itemsArray) {
                        total += item.price;
                    }
                    total *= (1 + Double.parseDouble(tax.getText().toString()) / 100);
                    DecimalFormat df2 = new DecimalFormat("#.##");
                    Log.e("mytag", Double.toString(total));

                    //ask user check if total correct
                    String dialogMessage = "<p>Is the total amount correct?</p><br><h2><b>RM" + df2.format(total) + "</b></h2>";
                    final AlertDialog dialog = new AlertDialog.Builder(QuickSplitCreditorAdd.this)
                            .setTitle("Total")
                            .setMessage(Html.fromHtml(dialogMessage))
                            .setNegativeButton("No, Check again", null)
                            .setPositiveButton("Yes, Create bill", null)
                            .create();
                    dialog.show();

                    //correct total
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //loading
                            progressBar.setVisibility(View.VISIBLE);
                            addButton.setEnabled(false);
                            dialog.dismiss();

                            //add new bill
                            Map<String, Object> data = new HashMap<>();
                            List array = new ArrayList();
                            final DocumentReference doc = db.collection("InstantSplit").document();
                            array.add(auth.getCurrentUser().getUid());
                            data.put("billName", title.getText().toString());
                            data.put("createTime", Timestamp.now());
                            data.put("items", itemArray);
                            data.put("owner", auth.getCurrentUser().getUid());
                            data.put("status", "pending");
                            data.put("splitWith", array);
                            data.put("tax", Double.parseDouble(tax.getText().toString()));
                            doc.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Map<String, Object> splitWithUser = new HashMap<>();
                                    splitWithUser.put("status", QuickSplitMemberStatus.NOTHING_SELECTED);
                                    doc.collection("splitWith").document(auth.getCurrentUser().getUid()).set(splitWithUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(QuickSplitCreditorAdd.this, "Added bill", Toast.LENGTH_SHORT).show();
                                            Log.e("mytag", "added");
                                            Intent intent = new Intent(QuickSplitCreditorAdd.this, QuickSplitGeneralEnterShare.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                            intent.putExtra("billId", doc.getId());
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("mytag", e.toString());
                                            Toast.makeText(QuickSplitCreditorAdd.this, "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("mytag", e.toString());
                                    Toast.makeText(QuickSplitCreditorAdd.this, "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}

