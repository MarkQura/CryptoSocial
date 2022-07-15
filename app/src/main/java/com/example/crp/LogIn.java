package com.example.crp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity {

    private EditText email, password;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.tv_email);
        password = findViewById(R.id.tv_password);
        Button logIn = findViewById(R.id.btn_login);
        Button register = findViewById(R.id.btn_register);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        logIn.setOnClickListener(v -> Login());

        register.setOnClickListener(v -> {
            Intent intent = new Intent(LogIn.this, Register.class);
            startActivity(intent);
        });

        FirebaseUser user = firebaseAuth.getCurrentUser();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (user != null || account != null) {
            Intent intent = new Intent(LogIn.this, MainScreen.class);
            startActivity(intent);
        }
    }

    private void Login() {
        String getEmail, getPassword;

        getEmail = email.getText().toString();
        getPassword = password.getText().toString();

        if (TextUtils.isEmpty(getEmail)) {
            email.setError("No email found");
            return;
        }

        if (TextUtils.isEmpty(getPassword)) {
            password.setError("No password found");
            return;
        }

        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth.signInWithEmailAndPassword(getEmail, getPassword).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(LogIn.this, "LogIn Successful", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LogIn.this, MainScreen.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LogIn.this, "LogIn failed", Toast.LENGTH_LONG).show();
            }
        });

                progressDialog.dismiss();
    }
}