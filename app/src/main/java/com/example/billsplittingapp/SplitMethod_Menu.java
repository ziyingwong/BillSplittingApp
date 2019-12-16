package com.example.billsplittingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SplitMethod_Menu extends AppCompatActivity {

    Button btnEqual;
    Button btnExact;
    Button btnPercentage;
    Button btnShare;

    String groupId;
    String groupName;
    String billName;
    double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_method__menu);

        btnEqual = findViewById(R.id.btnEqual);
        btnExact = findViewById(R.id.btnExact);
        btnPercentage = findViewById(R.id.btnPercentage);
        btnShare = findViewById(R.id.btnShare);

        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");
        billName = getIntent().getStringExtra("billName");
        double total = Double.parseDouble(getIntent().getStringExtra("total"));

        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplitMethod_Menu.this, SplitMethod_EqualValue.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupName", groupName);
                intent.putExtra("billName", billName);
                intent.putExtra("total", total);
                startActivity(intent);
            }
        });

        btnExact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplitMethod_Menu.this, SplitMethod_ExactValue.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupName", groupName);
                intent.putExtra("billName", billName);
                intent.putExtra("total", total);
                startActivity(intent);
            }
        });

        btnPercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplitMethod_Menu.this, SplitMethod_Percentage.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupName", groupName);
                intent.putExtra("billName", billName);
                intent.putExtra("total", total);
                startActivity(intent);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplitMethod_Menu.this, SplitMethod_Share.class);
                intent.putExtra("groupId", groupId);
                intent.putExtra("groupName", groupName);
                intent.putExtra("billName", billName);
                intent.putExtra("total", total);
                startActivity(intent);
            }
        });
    }
}
