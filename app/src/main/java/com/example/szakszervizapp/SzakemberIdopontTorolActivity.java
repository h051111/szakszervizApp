package com.example.szakszervizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.szakszervizapp.NotificationHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;

public class SzakemberIdopontTorolActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private EditText idopontText;
    private IdopontTorolAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<Idopont> mItemList;
    private NotificationHandler handler;
    private int gridNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szakember_idopont_torol);

        handler = new NotificationHandler(getApplicationContext());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView3);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));

        mItemList = new ArrayList<>();

        adapter = new IdopontTorolAdapter(this, mItemList);

        recyclerView.setAdapter(adapter);

        initializeData();
    }

    @Override
    protected void onPause() {
        if(FoglalasActivity.currentUser != null) {
            handler.send("Bejelentkezve: " + FoglalasActivity.currentUser.email);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        handler.cancel();
        Log.d("SzakemberIdopontTorol","onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        handler.cancel();
        Log.d("SzakemberIdopontTorol","onResume");
        super.onResume();
    }

    private void initializeData() {
        mItemList.clear();

        CollectionReference ref = mFirestore.collection("idopontok");
        ref.whereEqualTo("email", FoglalasActivity.currentUser.email).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                try {
                    Idopont idopont = new Idopont(document.getString("email"), document.getString("cim"), new Timestamp(document.getTimestamp("ido").toDate().getTime()));
                    mItemList.add(idopont);
                }
                catch(Exception ex){
                    //nem jó idő formátum valszeg
                }
            }

            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar_szakember, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                mFirebaseAuth.signOut();
                FoglalasActivity.currentUser = null;
                finish();
                return true;
            case R.id.hozzaadas:
                finish();
                Intent intent = new Intent(getApplicationContext(), SzakemberActivity.class);
                startActivity(intent);
                return true;
            case R.id.torles:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void torol(Idopont idopont) {
        CollectionReference ref = mFirestore.collection("idopontok");
        ref.whereEqualTo("email", idopont.email).whereEqualTo("ido", idopont.ido).whereEqualTo("cim", idopont.cim).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mFirestore.collection("idopontok").document(document.getId()).delete();
                            }
                        } else {
                            Log.d("FoglalasActivity", "Error getting documents: ", task.getException());
                        }
                        initializeData();
                        adapter.notifyDataSetChanged();
                    }
                });;
    }
}
