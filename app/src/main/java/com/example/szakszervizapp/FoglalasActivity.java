package com.example.szakszervizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

public class FoglalasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Idopont> mItemList;
    private IdopontAdapter adapter;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    public static User currentUser;  //akármi történik, mindíg itt lesz a currentUser
    private NotificationHandler handler;

    private int gridNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foglalas);
        handler = new NotificationHandler(getApplicationContext());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        CollectionReference ref = mFirestore.collection("felhasznaloAdatok");
        ref.whereEqualTo("email", mFirebaseAuth.getCurrentUser().getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("FoglalasActivity", document.getId() + " => " + document.getData());
                                currentUser = new User(document.getString("email"), document.getString("postaCim"), document.getBoolean("szakember"));
                                Log.d("FoglalasActivity", currentUser.email);

                                if (currentUser.szakember) {
                                    finish();
                                    Intent intent = new Intent(getApplicationContext(), SzakemberActivity.class);
                                    startActivity(intent);
                                }
                            }
                        } else {
                            Log.d("FoglalasActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));

        mItemList = new ArrayList<>();

        adapter = new IdopontAdapter(this, mItemList);

        recyclerView.setAdapter(adapter);

        initializeData();
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

    private void initializeData() {
        mItemList.clear();

        CollectionReference ref = mFirestore.collection("idopontok");
        ref.orderBy("ido").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Idopont idopont = new Idopont(document.getString("email"), document.getString("cim"), new Timestamp(document.getTimestamp("ido").toDate().getTime()));
                mItemList.add(idopont);
            }

            adapter.notifyDataSetChanged();
        });
    }

    public void foglal(Idopont idopont){
        Log.d("FoglalasActivity", idopont.ido.toString());
        CollectionReference ref = mFirestore.collection("foglalasok");
        ref.whereEqualTo("foglaloEmail", currentUser.email).whereEqualTo("ido", idopont.ido).whereEqualTo("szakemberEmail", idopont.email).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if(queryDocumentSnapshots.isEmpty()){
                        ref.add(new Foglalas(currentUser.email, idopont.ido, idopont.email));  //duplikált foglalások megakadájozása
                    }
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
                currentUser = null;
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
}