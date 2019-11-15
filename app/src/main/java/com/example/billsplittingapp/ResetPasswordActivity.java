package com.example.billsplittingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
    }

    public void onReset(View view){
        AlertDialog diaBox = AskOption();
        diaBox.show();
    }

    private AlertDialog AskOption() {
        final AlertDialog resetPasswordConfirmation =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Reset Password")
                .setMessage("Are you sure you want to Reset Password?")
//                .setIcon(R.drawable.ic_cancel_button)

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        AlertDialog diaBox = DisplayResetMessage();
                        diaBox.show();
                    }

                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return resetPasswordConfirmation;
    }

    private AlertDialog DisplayResetMessage() {
        final AlertDialog resetMessage =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Reset Password")
                .setMessage("Your password has been successfully Reset")
//                .setIcon(R.drawable.ic_cancel_button)
                .setCancelable(true)
                .create();
        return resetMessage;
    }
}
