package com.example.billsplittingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MyFriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        Toolbar myToolBar = (Toolbar) findViewById(R.id.TB);
        setSupportActionBar(myToolBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                return true;

            case R.id.action_logout:
                return true;

            case R.id.action_faq:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
