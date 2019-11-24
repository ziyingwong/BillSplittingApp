package com.example.billsplittingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
private FirebaseAuth auth;
private EditText etemail,etname,etpw,etconfirmpw;
private Button btnRegister;
private TextView btnLogin;
private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etemail = (EditText)findViewById(R.id.email_input);
        etname = (EditText)findViewById(R.id.name_input);
        etpw = (EditText)findViewById(R.id.password_input);
        etconfirmpw = (EditText)findViewById(R.id.confirm_password_input);
        btnRegister = (Button)findViewById(R.id.register_button);
        btnLogin = (TextView)findViewById(R.id.btn_to_login);
        auth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = etemail.getText().toString().trim();
                final String name = etname.getText().toString().trim();
                String pw = etpw.getText().toString().trim();
                String confirmpw = etconfirmpw.getText().toString().trim();
                if(email.equals("") || name.equals("")
                        || pw.equals("") || confirmpw.equals("")){
                    Toast.makeText(getApplicationContext(),
                            "Field(s) are empty",
                            Toast.LENGTH_SHORT).show();
                } else if(!pw.equals(confirmpw)){
                    Toast.makeText(getApplicationContext(),
                            "Passwords are not the same",
                            Toast.LENGTH_SHORT).show();
                } else{
                    progressBar.setVisibility(View.VISIBLE);
                    btnRegister.setVisibility(View.GONE);
                    auth.createUserWithEmailAndPassword(email,pw)
                            .addOnCompleteListener(RegisterActivity.this,
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            Toast.makeText(RegisterActivity.this,
                                                    "Register Success"
                                            , Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            if(!task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this,
                                                        "Authentication Fail"
                                                                +task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                            } else{
                                                registerUser(auth.getUid(),email,name);
                                                Intent i = new Intent(RegisterActivity.this
                                                        ,LoginActivity.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        }
                                    });
                }
            }
        });
    }

    public void registerUser(String uid, String email, String name){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        RegisterUserObject object = new RegisterUserObject(uid,email,name);
        firestore.collection("contactList")
                .document(uid)
                .set(object)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.e("TAG", "onComplete: Register Success" );
                        }
                        else{
                            Log.e("TAG", "onComplete: Register Fail" );
                        }
                    }
                });
    }
}
