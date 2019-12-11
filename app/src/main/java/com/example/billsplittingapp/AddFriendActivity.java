package com.example.billsplittingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddFriendActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    boolean result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

    }


    public void onSearchFriend(View view) {
        final LinearLayout linearLayout = (LinearLayout)findViewById(R.id.layout_add_friend);
        EditText searchET = (EditText)findViewById(R.id.search_textView);
        String emailText = searchET.getText().toString();
        firestore.collection("contactList")
            .whereEqualTo("userEmail",emailText)
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
                    final String name = obj.getUserName();
                    final String email = obj.getUserEmail();
                    final String uid = obj.getUserUID();
                    Log.e("TAG", "onEvent: name: "+name );
                    Log.e("TAG", "onEvent: name: "+email );
                    Log.e("TAG", "onEvent: name: "+uid );
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.activity_add_friend_field, linearLayout, false);
                    linearLayout.addView(rowView, linearLayout.getChildCount() - 1);
                    TextView nameResult = (TextView)rowView.findViewById(R.id.result_name_text);
                    nameResult.setText(name);
                    TextView emailResult = (TextView)rowView.findViewById(R.id.result_email_text);
                    emailResult.setText(email);
                    final ProgressBar progressBar = (ProgressBar)rowView.findViewById(R.id.progress_bar);
                    final Button btnAdd = (Button)rowView.findViewById(R.id.add_friend_button);
                    final Button btnAdded = (Button)rowView.findViewById(R.id.added_friend_button);
                    btnAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            progressBar.setVisibility(View.VISIBLE);
                            btnAdd.setVisibility(View.GONE);
                            if(uid.equals(auth.getUid())){
                                Toast.makeText(getApplicationContext(),"Invalid Action",Toast.LENGTH_SHORT).show();
                            } else{
                                addFriend(uid,email,name);
                                addFriend2(uid);
                            }
                            progressBar.setVisibility(View.GONE);
                            btnAdded.setVisibility(View.VISIBLE);
                        }
                    });
                }

            }
        });
    }

    public void addFriend(String uid, String email, String name){
        MyFriendsObject object = new MyFriendsObject(uid,name,email, "Settled Up");
        firestore.collection("contactList")
                .document(auth.getUid())
                .collection("friend")
                .document(uid)
                .set(object)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.e("TAG", "onComplete: Success" );
                        } else{
                            Log.e("TAG", "onComplete: Failure" );
                        }
                    }
                });
    }

    public void addFriend2(String uid){
        MyFriendsObject object = new MyFriendsObject(auth.getUid(),auth.getCurrentUser().getDisplayName()
                ,auth.getCurrentUser().getEmail(),
                "Settled Up");
        firestore.collection("contactList")
                .document(uid)
                .collection("friend")
                .document(auth.getUid())
                .set(object)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.e("TAG", "onComplete: Success" );
                        } else{
                            Log.e("TAG", "onComplete: Failure" );
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(AddFriendActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }
}
