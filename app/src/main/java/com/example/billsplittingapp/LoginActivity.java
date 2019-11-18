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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    private EditText etemail, etpw;
    private Button btnlogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etemail = (EditText)findViewById(R.id.login_email);
        etpw = (EditText)findViewById(R.id.login_password);
        btnlogin = (Button)findViewById(R.id.login_button);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etemail.getText().toString().trim();
                String password = etpw.getText().toString().trim();
                if(email.equals("")||password.equals("")){
                    Toast.makeText(getApplicationContext(),
                            "Field(s) are empty",
                            Toast.LENGTH_SHORT).show();
                } else{
                    progressBar.setVisibility(View.VISIBLE);
                    btnlogin.setVisibility(View.GONE);
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(
                            LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(),
                                                "Login Fail", Toast.LENGTH_SHORT)
                                                .show();
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "Login Success", Toast.LENGTH_SHORT)
                                                .show();
                                        Intent i = new Intent(LoginActivity.this,
                                                MainActivity.class);
                                        startActivity(i);
                                    }
                                }
                            }
                    );
                }
            }
        });
    }

    public void onNavRegisterActivity(View v){
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("mytag", "Welcome back");
        if (auth.getCurrentUser() != null) {
            final Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
