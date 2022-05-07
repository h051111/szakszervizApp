package com.example.szakszervizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private CollectionReference mUserData;
    private EditText textEmail;
    private EditText textPassword;
    private EditText textPasswordAgain;
    private EditText textAddress;
    private RadioButton szakemberButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        textEmail = findViewById(R.id.textEmail);
        textPassword = findViewById(R.id.textPassword);
        textPasswordAgain = findViewById(R.id.textPasswordAgain);
        textAddress = findViewById(R.id.textPostal);
        szakemberButton = findViewById(R.id.szakemberButton);

        mFirestore = FirebaseFirestore.getInstance();
        mUserData = mFirestore.collection("felhasznaloAdatok");
    }

    public void register(View view) {
        Log.i(LOG_TAG, "register");
        String email = textEmail.getText().toString();
        String password = textPassword.getText().toString();
        String passwordAgain = textPasswordAgain.getText().toString();
        String cim = textAddress.getText().toString();

        if(password.equals(passwordAgain)){
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.i(LOG_TAG, "Registration successful");
                        mUserData.add(new User(email, cim, szakemberButton.isChecked()));
                    }
                    else{
                        Log.d(LOG_TAG, "Registration unsuccessful");
                    }
                }
            });
        }
        else{
            Log.d(LOG_TAG, "Jelszavak nem egyeznek meg");
        }
    }

    public void goBack(View view) {
        Log.i(LOG_TAG, "goBack");
        finish();
    }
}
