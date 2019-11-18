package com.example.billsplittingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Fragment selectFragment;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Fragment quickSplit = new QuickSplitMain();
        final BottomNavigationView menu = findViewById(R.id.navigation);

        /*Drawer Menu */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        frameLayout = (FrameLayout)findViewById(R.id.fragment_container);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
//                .WRAP_CONTENT);
//        params.addRule(RelativeLayout.BELOW,R.id.toolbar);



        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar,
                R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id){
                            case R.id.nav_logout:
                                auth.signOut();
                                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                                startActivity(i);
                                break;
                            case R.id.nav_change_password:
                                //change pw
                                Intent i2 = new Intent(MainActivity.this,ChangePasswordActivity.class);
                                startActivity(i2);
                                break;
                            default:
                                return true;
                        }
                        return true;
                    }
                }
        );

        menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_contact_list:
                        setTitle("Contact");
                        break;
                    case R.id.navigation_group:
                        setTitle("Group");
                        break;
                    case R.id.navigation_quick_split:
                        selectFragment = quickSplit;
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectFragment).commit();
                        setTitle("Quick Split");
                        break;
                    case R.id.navigation_notification:
                        setTitle("Notification");
                        break;
                    default:
                        break;
                }


                return true;
            }
        });

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finishAffinity();
    }
}
