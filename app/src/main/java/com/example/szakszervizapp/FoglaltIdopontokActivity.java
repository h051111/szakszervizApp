package com.example.szakszervizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.util.ArrayList;

public class FoglaltIdopontokActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Idopont> mItemList;
    private FoglaltIdopontAdapter adapter;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private NotificationHandler handler;

    private int gridNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foglalt_idopontok);
        handler = new NotificationHandler(getApplicationContext());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));

        mItemList = new ArrayList<>();

        adapter = new FoglaltIdopontAdapter(this, mItemList);

        recyclerView.setAdapter(adapter);

        initializeData();
    }

    private void initializeData() {
        mItemList.clear();

        CollectionReference ref = mFirestore.collection("foglalasok");
        ref.whereEqualTo("foglaloEmail", FoglalasActivity.currentUser.email).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Foglalas foglalas = new Foglalas(document.getString("foglaloEmail"), new Timestamp(document.getTimestamp("ido").toDate().getTime()), document.getString("szakemberEmail"));
                mItemList.add(new Idopont(foglalas.szakemberEmail, "", foglalas.ido));
            }

            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
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
            case R.id.foglalasaim:
                finish();
                Intent intent1 = new Intent(getApplicationContext(), FoglaltIdopontokActivity.class);
                startActivity(intent1);
                return true;
            case R.id.idoponfoglalas:
                finish();
                Intent intent2 = new Intent(getApplicationContext(), FoglalasActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        if(FoglalasActivity.currentUser != null) {
            handler.send("Bejelentkezve: " + FoglalasActivity.currentUser.email);
        }
        Log.d("SzakemberIdopontTorol","onPause");
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

    public void torol(Idopont idopont) {
        CollectionReference ref = mFirestore.collection("foglalasok");
        ref.whereEqualTo("foglaloEmail", FoglalasActivity.currentUser.email).whereEqualTo("ido", idopont.ido).whereEqualTo("szakemberEmail", idopont.email).get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            mFirestore.collection("foglalasok").document(document.getId()).delete();
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
