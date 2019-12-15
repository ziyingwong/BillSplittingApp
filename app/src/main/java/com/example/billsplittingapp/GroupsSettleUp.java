package com.example.billsplittingapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupsSettleUp extends AppCompatActivity {
    //    GroupsSettleUpAdapter adapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Map<String, Object> map;
    ArrayList<UserDebtProfile> complete;
    String username = "";
    String groupId;
    String URL = "http://192.168.0.187:3030/sendEmailReminder";
    String receiverEmail;
    LinearLayout bigLinearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_settleup);
        complete = new ArrayList<>();
        groupId = getIntent().getStringExtra("groupId");

        calculateTotal();

        //toolbar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Settle Up");

    }

//    public void calculateTotal() {
//        String uid = auth.getCurrentUser().getUid();
//        Query query = db.collection("Groups").whereArrayContains("userArray", uid);
//        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                if (queryDocumentSnapshots.size() > 0) {
//                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
//                        map = (HashMap) doc.get("user");
//                        ArrayList<UserStringValue> positive = new ArrayList<>();
//                        ArrayList<UserStringValue> negative = new ArrayList<>();
//                        for (Map.Entry<String, Object> entry : map.entrySet()) {
//                            if (Double.parseDouble(entry.getValue().toString()) > 0) {
//                                positive.add(new UserStringValue(entry.getKey(), Double.parseDouble(entry.getValue().toString())));
//                            } else {
//                                negative.add(new UserStringValue(entry.getKey(), Double.parseDouble(entry.getValue().toString())));
//                            }
//                            complete.add(new UserDebtProfile(entry.getKey()));
//                        }
//                        int i = 0, j = 0;
//                        while (i < positive.size()) {
//                            if ((negative.get(j).value * -1) > positive.get(i).value) {
//                                negative.get(j).value += positive.get(i).value;
//                                for (UserDebtProfile usd : complete) {
//                                    if (positive.get(i).key.equals(usd.key)) {
//                                        usd.pinjam.add(new UserStringValue(negative.get(j).key, positive.get(i).value));
//                                    } else if (negative.get(i).key.equals(usd.key)) {
//                                        usd.hutang.add(new UserStringValue(positive.get(i).key, positive.get(i).value));
//                                    }
//                                }
//                                i++;
//                            } else if ((negative.get(j).value * -1) == positive.get(i).value) {
//                                for (UserDebtProfile usd : complete) {
//                                    if (positive.get(i).key.equals(usd.key)) {
//                                        usd.pinjam.add(new UserStringValue(negative.get(j).key, positive.get(i).value));
//                                    } else if (negative.get(i).key.equals(usd.key)) {
//                                        usd.hutang.add(new UserStringValue(positive.get(i).key, positive.get(i).value));
//                                    }
//                                }
//                                i++;
//                                j++;
//                            } else {
//                                positive.get(i).value += negative.get(j).value;
//                                for (UserDebtProfile usd : complete) {
//                                    if (positive.get(i).key.equals(usd.key)) {
//                                        usd.pinjam.add(new UserStringValue(negative.get(j).key, negative.get(i).value * -1));
//                                    } else if (negative.get(i).key.equals(usd.key)) {
//                                        usd.hutang.add(new UserStringValue(positive.get(i).key, negative.get(i).value * -1));
//                                    }
//                                }
//                                j++;
//                            }
//                        }
//                        addLinearCard();
//                    }
//                }
//            }
//        });
//
//    }

    public void calculateTotal() {
        String uid = auth.getCurrentUser().getUid();

        db.collection("Groups").document(groupId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                map = (HashMap) documentSnapshot.get("user");
                ArrayList<UserStringValue> positive = new ArrayList<>();
                ArrayList<UserStringValue> negative = new ArrayList<>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (Double.parseDouble(entry.getValue().toString()) > 0) {
                        positive.add(new UserStringValue(entry.getKey(), Double.parseDouble(entry.getValue().toString())));
                    } else if(Double.parseDouble(entry.getValue().toString()) < 0) {
                        negative.add(new UserStringValue(entry.getKey(), Double.parseDouble(entry.getValue().toString())));
                    }
                    complete.add(new UserDebtProfile(entry.getKey()));
                }
                int i = 0, j = 0;

                while (i < positive.size()) {
                    Log.d("index", "" + positive.size() + " " + negative.size());
                    if ((negative.get(j).value * -1) > positive.get(i).value) {
                        negative.get(j).value += positive.get(i).value;
                        for (UserDebtProfile usd : complete) {
                            if (positive.get(i).key.equals(usd.key)) {
                                usd.pinjam.add(new UserStringValue(negative.get(j).key, positive.get(i).value));
                            } else if (negative.get(j).key.equals(usd.key)) {
                                usd.hutang.add(new UserStringValue(positive.get(i).key, positive.get(i).value));
                            }
                        }
                        i++;
                    } else if ((negative.get(j).value * -1) == positive.get(i).value) {
                        for (UserDebtProfile usd : complete) {
                            if (positive.get(i).key.equals(usd.key)) {
                                usd.pinjam.add(new UserStringValue(negative.get(j).key, positive.get(i).value));
                            } else if (negative.get(j).key.equals(usd.key)) {
                                usd.hutang.add(new UserStringValue(positive.get(i).key, positive.get(i).value));
                            }
                        }
                        i++;
                        j++;
                    } else {
                        positive.get(i).value += negative.get(j).value;
                        for (UserDebtProfile usd : complete) {
                            if (positive.get(i).key.equals(usd.key)) {
                                usd.pinjam.add(new UserStringValue(negative.get(j).key, negative.get(j).value * -1));
                            } else if (negative.get(j).key.equals(usd.key)) {
                                usd.hutang.add(new UserStringValue(positive.get(i).key, negative.get(j).value * -1));
                            }
                        }
                        j++;
                    }
                }
                addLinearCard();
            }
        });
    }


    public void addLinearCard() {
        bigLinearLayout = findViewById(R.id.biglinearlayout);

        LayoutInflater linf = LayoutInflater.from(GroupsSettleUp.this);

        bigLinearLayout.setOrientation(LinearLayout.VERTICAL);

        for (UserDebtProfile user : complete) {
            if (user.key.equals(auth.getCurrentUser().getUid())) {
                Log.e("mytag", "hutang :" + user.hutang);
                Log.e("mytag", "pinjam :" + user.pinjam);

                if (bigLinearLayout.getChildCount() < user.pinjam.size() || bigLinearLayout.getChildCount() < user.hutang.size()) {

                    for (UserStringValue detail : user.hutang) {

                        View mView = linf.inflate(R.layout.groups_card_settleup, null);
                        TextView details = mView.findViewById(R.id.detailsSettleUp);
                        Button buttonSettle = mView.findViewById(R.id.settleupbutton);
                        ImageButton notificationButton = mView.findViewById(R.id.notifyButton);

                        db.collection("contactList").document(detail.key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    username = documentSnapshot.get("userName").toString();
                                    String stringAmountFormat = String.format("%,.2f", detail.value);
                                    details.setText("Owing " + username + " RM" + stringAmountFormat);
                                    Log.d("DEBUG-A", details.getText().toString());
                                }

                            }
                        });
                        bigLinearLayout.addView(mView);
                        buttonSettle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(GroupsSettleUp.this, GroupsSettleUpPay.class);
                                intent.putExtra("payerId", auth.getCurrentUser().getUid());
                                intent.putExtra("payeeId", detail.key);
                                intent.putExtra("amount", detail.value);
                                intent.putExtra("groupId", groupId);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);

                            }
                        });

                        notificationButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onSendReminder(detail.key);
                            }
                        });

                    }
                    for (UserStringValue detail : user.pinjam) {
                        View mView = linf.inflate(R.layout.groups_card_settleup, null);
                        TextView details = mView.findViewById(R.id.detailsSettleUp);
                        Button buttonSettle = mView.findViewById(R.id.settleupbutton);
                        ImageButton notificationButton = mView.findViewById(R.id.notifyButton);

                        db.collection("contactList").document(detail.key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    username = documentSnapshot.get("userName").toString();
                                    String stringAmountFormat = String.format("%,.2f", detail.value);
                                    details.setText("Getting back RM" + stringAmountFormat + " from " + username);
                                    Log.d("DEBUG-B", details.getText().toString());
                                }

                            }
                        });
                        bigLinearLayout.addView(mView);
                        buttonSettle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(GroupsSettleUp.this, GroupsSettleUpPay.class);
                                intent.putExtra("payeeId", auth.getCurrentUser().getUid());
                                intent.putExtra("payerId", detail.key);
                                intent.putExtra("amount", detail.value);
                                intent.putExtra("groupId", groupId);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                startActivity(intent);

                            }
                        });

                        notificationButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onSendReminder(detail.key);

                            }
                        });
                    }

                }
            }
        }
    }


    public void onSendReminder(String payeeUID){
        db.collection("contactList")
                .whereEqualTo("userUID",payeeUID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots
                            , @Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            Log.e("TAG", "onEvent: Fail"+e );
                            return;
                        }
                        List<RegisterUserObject> objects = snapshots.toObjects(RegisterUserObject.class);
                        for(RegisterUserObject obj : objects){
                            receiverEmail = obj.getUserEmail();
                            Log.e("TAG", "onEvent: email of receiver: "+receiverEmail );
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
                                                    Log.e("TAG", "onResponse: enter success 1 ");
                                                    Toast.makeText(bigLinearLayout.getContext(),
                                                            "A Reminder has been sent to the user's email",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(bigLinearLayout.getContext(),
                                                            "Error, Please Try Again Later",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(bigLinearLayout.getContext(),
                                                    "Error, Please Try Again Later!",
                                                    Toast.LENGTH_SHORT).show();
                                            Log.e("TAG", "onErrorResponse: "+error );
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("email", receiverEmail);
                                    params.put("senderName",auth.getCurrentUser().getDisplayName());
                                    return params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                            requestQueue.add(stringRequest);
                        }
                    }
                });


        Log.e("TAG", "onCreate: current user name: "+auth.getCurrentUser().getDisplayName() );


    }


}
