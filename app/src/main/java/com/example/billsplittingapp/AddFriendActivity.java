package com.example.billsplittingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class AddFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
    }
    public void onAddFriend(View view){
        ImageView imageview = (ImageView)findViewById(R.id.add_symbol);
        imageview.setVisibility(View.GONE);
        ImageView imageview2 = (ImageView)findViewById(R.id.added_symbol);
        imageview2.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(),"Friend Request Sent!",Toast.LENGTH_SHORT).show();
    }
}
