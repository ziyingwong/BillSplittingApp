package com.example.billsplittingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class QuickSplitMain extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ziying_quick_main, container, false);
        RecyclerView recycler = v.findViewById(R.id.quickMainRecycler);
        String uid = auth.getCurrentUser().getUid();
        setHasOptionsMenu(true);

        Query query = db.collection("InstantSplit").whereArrayContains("splitWith", uid).orderBy("createTime", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<QuickSplitBillObjects> options = new FirestoreRecyclerOptions.Builder<QuickSplitBillObjects>()
                .setQuery(query, QuickSplitBillObjects.class)
                .build();
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        QuickSplitMainRecyclerAdapter adapter = new QuickSplitMainRecyclerAdapter(options);
        adapter.startListening();
        recycler.setAdapter(adapter);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_instantsplit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add) {
            Intent intent = new Intent(getActivity(), QuickSplitCreditorAdd.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } else if (item.getItemId() == R.id.scan) {
            //generate qr CODE
//            try {
//                QRGEncoder qrgEncoder = new QRGEncoder("hello", null, QRGContents.Type.TEXT, 600);
//                Bitmap bm = qrgEncoder.encodeAsBitmap();
//                View v = getLayoutInflater().inflate(R.layout.ziying_quick_creditor_showqrcode, null);
//                AlertDialog dialog = new AlertDialog.Builder(getActivity())
//                        .setNeutralButton("Dismiss", null)
//                        .setView(v)
//                        .create();
//                ImageView qrCode = v.findViewById(R.id.qrCodeView);
//                qrCode.setImageBitmap(bm);
//                dialog.show();
//            } catch (WriterException e) {
//                e.printStackTrace();
//            }

            Intent intent = new Intent(getActivity(), QuickSplitGeneralEnterShare.class);
            intent.putExtra("billId","8fipAwL00wxCTBeQbDXz");

//            Intent intent = new Intent(getActivity(), QuickSplitDebtorScanCamera.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        }
        return true;
    }

}
