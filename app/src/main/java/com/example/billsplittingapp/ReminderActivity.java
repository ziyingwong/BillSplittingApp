package com.example.billsplittingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReminderActivity extends AppCompatActivity {
    private TextView nameTextView,emailTextView;
    private String name, status, email, uid;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button deleteButton;
    private TextView completedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        nameTextView = (TextView)findViewById(R.id.name_text);
        emailTextView = (TextView)findViewById(R.id.email_text);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        deleteButton = (Button)findViewById(R.id.button_delete);
        completedText = (TextView)findViewById(R.id.completed_text);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                Log.i("FCM Token", token);
//                saveToken(token);
            }
        });

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                name = extras.getString("name");
                status = extras.getString("status");
                email = extras.getString("email");
                uid = extras.getString("uid");
                Log.e("TAG", "onCreate: email from extra"+email );
                nameTextView.setText(name);
                emailTextView.setText(email);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onDeleteFriend(uid);
                    }
                });
            }
        }

    }

    public void onDeleteFriend(String uid){

        firestore.collection("contactList")
                .document(auth.getUid())
                .collection("friend")
                .document(uid)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.e("TAG", "onComplete: deleted" );
                            completedText.setVisibility(View.VISIBLE);
                            deleteButton.setVisibility(View.GONE);
                        } else{
                            Toast.makeText(getApplicationContext()
                                    ,"Error, Please Try Again Later"
                                    ,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(ReminderActivity.this,
                MainActivity.class);
        startActivity(i);
        super.onBackPressed();
    }
}
