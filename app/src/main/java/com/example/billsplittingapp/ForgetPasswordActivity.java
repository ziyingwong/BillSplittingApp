package com.example.billsplittingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    private EditText etEmail;
    private Button btnReset,btnBack;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private TextView successText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        etEmail = (EditText)findViewById(R.id.reset_email);
        btnReset = (Button)findViewById(R.id.btn_reset_password);
        btnBack = (Button)findViewById(R.id.btn_back);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        auth = FirebaseAuth.getInstance();
        successText = (TextView)findViewById(R.id.success_text);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ForgetPasswordActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                if(email.equals("")){
                    Toast.makeText(getApplicationContext(),
                            "Please Enter Your Email",Toast.LENGTH_SHORT)
                            .show();
                } else{
                    progressBar.setVisibility(View.VISIBLE);
                    btnReset.setVisibility(View.GONE);
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
//                                        Toast.makeText(getApplicationContext(),
//                                                "Please Check Your Email to Reset Password",Toast.LENGTH_SHORT)
//                                                .show();
                                        btnReset.setVisibility(View.GONE);
                                        etEmail.setVisibility(View.GONE);
                                        successText.setVisibility(View.VISIBLE);
                                    } else{
                                        Toast.makeText(getApplicationContext(),
                                                "Fail to Reset Password, Please Try Again Later",Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });
    }
}
