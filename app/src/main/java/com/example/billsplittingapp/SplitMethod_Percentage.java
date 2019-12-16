package com.example.billsplittingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SplitMethod_Percentage extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    MethodSplitAdapterPercentage adapter;
    RecyclerView recyclerView2;
    ArrayList<MethodSplitObject> arrayList = new ArrayList<>();
    Map<String, Double> user;
    ArrayList<String> userArray;

    Button buttonPercentageconfirmed;
    String groupId;
    String groupName;
    String billName2;
    String totaltopaywrite;
    TextView totaltopay;
    double total;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.method_percentage);
        final Toolbar toolbar = findViewById(R.id.toolbarpercentage);
        setSupportActionBar(toolbar);
        setTitle("percentage");


        groupName = getIntent().getStringExtra("groupName");
        billName2 = getIntent().getStringExtra("billName");
        groupId = getIntent().getStringExtra("groupId");
        total = Double.parseDouble(getIntent().getStringExtra("total"));

        totaltopay = findViewById(R.id.ettotaltopay);
        totaltopaywrite = String.format("%.2f", total);
        totaltopay.setText("RM" + totaltopaywrite);



        db.collection("Groups").document(groupId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    user = (HashMap) documentSnapshot.get("user");
                    userArray = (ArrayList<String>) documentSnapshot.get("userArray");
                    for (int i = 0; i < user.size(); i++) {
                        MethodSplitObject userProfile = new MethodSplitObject();
                        userProfile.uid = userArray.get(i);
                        arrayList.add(userProfile);
                    }
                    adapter.notifyDataSetChanged();

                }
            }
        });

        recyclerView2 = findViewById(R.id.percentagevaluelist);
        adapter = new MethodSplitAdapterPercentage(arrayList,total);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.setAdapter(adapter);

        buttonPercentageconfirmed = findViewById(R.id.buttonPercentageconfirmed);
        buttonPercentageconfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Double> newAmount = new HashMap<>();

                Double counttotal = 0.0;
                boolean emptycheck = true;

                for (int i = 0; i < userArray.size(); i++) {
                    View view = recyclerView2.getChildAt(i);
                    EditText amount = view.findViewById(R.id.etpercentage);

                    if(amount.getText().toString().isEmpty()){
                        emptycheck = false;
                        break;
                    }

                    counttotal += Double.parseDouble(amount.getText().toString());
                }


                if(emptycheck == false){
                    Toast. makeText(getApplicationContext(),"Don't leave empty", Toast. LENGTH_LONG).show();

                }

                else if(counttotal != 100.00 && emptycheck == true){
                    Toast. makeText(getApplicationContext(),"Sum of percentages must be 100%", Toast. LENGTH_LONG).show();
                }

                else{
                    for (int i = 0; i < userArray.size(); i++) {
                        View view = recyclerView2.getChildAt(i);
                        EditText amount = view.findViewById(R.id.etpercentage);
                        Double portion = Double.parseDouble(amount.getText().toString());
                        Double newportion = (portion/100.00)*total;
                        String str = String.format("%1.2f", newportion);
                        Double str2 = Double.parseDouble(str);
                        newAmount.put(userArray.get(i), str2);
                    }


                    Intent intent = new Intent(SplitMethod_Percentage.this, ExpenseAddNew.class);
                    intent.putExtra("groupId", groupId);
                    intent.putExtra("groupName", groupName);
                    intent.putExtra("splitAmount", newAmount);
                    intent.putExtra("splitUser", userArray);
                    intent.putExtra("price", total);
                    intent.putExtra("billName2", billName2);
                    startActivity(intent);
                }
            }
        });


    }
}
