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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText etPassword, etConfirmPassword, etOldPassword;
    private Button btnChange;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private FirebaseUser user;
    private String oldPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        etPassword = (EditText)findViewById(R.id.et_new_password);
        etConfirmPassword = (EditText)findViewById(R.id.et_new_confirm_password);
        etOldPassword = (EditText)findViewById(R.id.et_old_password);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        btnChange = (Button)findViewById(R.id.btn_change_password);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                btnChange.setVisibility(View.GONE);
                oldPw = etOldPassword.getText().toString().trim();
                String pw1 = etPassword.getText().toString().trim();
                String pw2 = etConfirmPassword.getText().toString().trim();
                if(!pw1.equals(pw2)){
                    Toast.makeText(getApplicationContext(),"Passwords are not the same",
                            Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                    etConfirmPassword.setText("");
                    etOldPassword.setText("");
                    progressBar.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);
                } else if(pw1.length()<6){
                    Toast.makeText(getApplicationContext(),"Please Enter At Least 6 Characters",
                            Toast.LENGTH_SHORT).show();
                    etPassword.setText("");
                    etConfirmPassword.setText("");
                    etOldPassword.setText("");
                    progressBar.setVisibility(View.GONE);
                    btnChange.setVisibility(View.VISIBLE);
                }

                else{
                    Log.e("TAG", "email: "+user.getEmail());
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user.getEmail(), oldPw);


                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.e("TAG", "authentication success ");
                                        user.updatePassword(etPassword.getText().toString().trim())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ChangePasswordActivity.this, "Password is updated!", Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(ChangePasswordActivity.this,MainActivity.class);
                                                    startActivity(i);
                                                } else {
                                                    Log.e("TAG", task.getException().getMessage());
                                                    Toast.makeText(ChangePasswordActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    btnChange.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getApplicationContext(),"Incorrect Old Password",
                                                Toast.LENGTH_SHORT).show();
                                        etPassword.setText("");
                                        etConfirmPassword.setText("");
                                        etOldPassword.setText("");
                                        progressBar.setVisibility(View.GONE);
                                        btnChange.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                }
            }
        });
    }
}
