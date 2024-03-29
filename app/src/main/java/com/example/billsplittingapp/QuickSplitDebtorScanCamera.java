package com.example.billsplittingapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QuickSplitDebtorScanCamera extends AppCompatActivity {
    private ZXingScannerView scannerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    int CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ziying_quick_debtor_scan);
        scannerView = findViewById(R.id.qrCodeScanner);
        scannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
            @Override
            public void handleResult(final Result result) {

                final String billId = result.getText();
                Map<String, Object> splitWithUser = new HashMap<>();
                splitWithUser.put("status", QuickSplitMemberStatus.NOTHING_SELECTED);
                splitWithUser.put("displayName", auth.getCurrentUser().getDisplayName());
                splitWithUser.put("uid", auth.getCurrentUser().getUid());
                db.collection("QuickSplit").document(billId)
                        .collection("splitWith").document(auth.getCurrentUser().getUid())
                        .set(splitWithUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("QuickSplit").document(billId)
                                .update("splitWith", FieldValue.arrayUnion(auth.getCurrentUser().getUid()))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Map<String, Object> billStatus = new HashMap<>();
                                        billStatus.put("status", "pending");
                                        db.collection("QuickSplit").document(billId).set(billStatus, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.e("mytag", "pending");
                                                Intent intent = new Intent(QuickSplitDebtorScanCamera.this, QuickSplitGeneralEnterShare.class);
                                                intent.putExtra("billId", billId);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                                Log.e("mytag", billId);
                                                startActivity(intent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("mytag", e.toString());
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("mytag", e.toString());
                                Toast.makeText(QuickSplitDebtorScanCamera.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("mytag", e.toString());
                        Toast.makeText(QuickSplitDebtorScanCamera.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            scannerView.startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                scannerView.startCamera();
            }
        }
    }

    @Override
    protected void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }
}
