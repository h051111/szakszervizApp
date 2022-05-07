package com.example.szakszervizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;

public class SzakemberActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private EditText idopontText;
    private NotificationHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szakember);
        handler = new NotificationHandler(getApplicationContext());

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        idopontText = findViewById(R.id.textAddIdopont);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar_szakember, menu);
        return true;
    }

    @Override
    protected void onPause() {
        if(FoglalasActivity.currentUser != null) {
            handler.send("Bejelentkezve: " + FoglalasActivity.currentUser.email);
            Log.d("SzakemberIdopontTorol", "onPause");
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
                return true;
            case R.id.torles:
                finish();
                Intent intent = new Intent(getApplicationContext(), SzakemberIdopontTorolActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void idopontAdd(View view) {
        Timestamp ido = Timestamp.valueOf(idopontText.getText().toString());
        mFirestore.collection("idopontok").add(new Idopont(FoglalasActivity.currentUser.email, FoglalasActivity.currentUser.postaCim, ido));
    }
}