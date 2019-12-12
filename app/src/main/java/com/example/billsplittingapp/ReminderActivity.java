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
    private TextView nameTextView,statusTextView;
    private String name, status, email, uid;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private static String URL;
    private ProgressBar progressBar;
    private Button reminderButton;
    private TextView completedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        nameTextView = (TextView)findViewById(R.id.name_text);
        statusTextView = (TextView)findViewById(R.id.status_text);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        reminderButton = (Button)findViewById(R.id.button_reminder);
        completedText = (TextView)findViewById(R.id.completed_text);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        URL = "http://192.168.0.187:3030/sendEmailReminder";

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
                statusTextView.setText(status);
            }
        }

    }

    public void onSendReminder(View v){
        reminderButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Log.e("TAG", "onCreate: email in reminder method"+email );
        Log.e("TAG", "onCreate: current user name: "+auth.getCurrentUser().getDisplayName() );
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String success = jsonObject.getString("success");
                            if (success.equals("1")) {
                                Toast.makeText(getApplicationContext(),
                                        "A Reminder has been sent to the user's email",
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                completedText.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Error, Please Try Again Later",
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                reminderButton.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),
                                "Error, Please Try Again Later!",
                                Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("senderName",auth.getCurrentUser().getDisplayName());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public void onSettledUp(View v){
        MyFriendsObject object = new MyFriendsObject(uid, name
                ,email,
                "Settled Up");
        CollectionReference collectionReference =
                firestore.collection("contactList")
                        .document(auth.getCurrentUser().getUid())
                        .collection("friend");
        collectionReference
                .whereEqualTo("friendEmail",email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document:
                            task.getResult()){
                                collectionReference
                                .document(document.getId())
                                .set(object);
                                statusTextView.setText("Settled Up");
                                statusTextView.setTextColor(Color.parseColor("#607D8B"));
                                Toast.makeText(getApplicationContext(),
                                        "Your Bills are now Settled Up!",
                                        Toast.LENGTH_SHORT).show();

                            }
                        } else{
                            Log.e("TAG", "onComplete: Failure" );
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
