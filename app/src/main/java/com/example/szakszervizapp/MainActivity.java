package com.example.szakszervizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private FirebaseAuth mAuth;
    private EditText textEmailAddress;
    private EditText textPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textEmailAddress = findViewById(R.id.loginEmail);
        textPassword = findViewById(R.id.loginPassword);

        mAuth = FirebaseAuth.getInstance();
    }

    public void login(View view) {
        Log.i(LOG_TAG,"Login");

        String email = textEmailAddress.getText().toString();
        String password = textPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.i(LOG_TAG, "Successful login");
                    Intent intent = new Intent(getApplicationContext(), FoglalasActivity.class);
                    startActivity(intent);
                }
                else{
                    Log.i(LOG_TAG, "Unsuccessful login");
                    Log.i(LOG_TAG, email + ":" + password);
                }
            }
        });
    }

    public void register(View view) {
        Log.i(LOG_TAG,"Register");
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
