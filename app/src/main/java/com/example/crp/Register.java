package com.example.crp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {

    private EditText email, password1, password2, username;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.res_email);
        password1 = findViewById(R.id.res_password);
        password2 = findViewById(R.id.res_password2);
        username = findViewById(R.id.res_username);
        Button logIn = findViewById(R.id.btn_res_login);
        Button register = findViewById(R.id.btn_res_register);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        logIn.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, LogIn.class);
            startActivity(intent);
        });

        register.setOnClickListener(v -> regitrer());
    }

    public void regitrer() {
        String getEmail, getPassword1, getPassword2, getUsername;

        getEmail = email.getText().toString();
        getPassword1 = password1.getText().toString();
        getPassword2 = password2.getText().toString();
        getUsername = username.getText().toString();

        if (TextUtils.isEmpty(getUsername)) {
            username.setError("No username found");
            return;
        }

        else if (TextUtils.isEmpty(getEmail)) {
            email.setError("No email found");
            return;
        }

        else if (TextUtils.isEmpty(getPassword1)) {
            password1.setError("No password found");
            return;
        }

        else if (TextUtils.isEmpty(getPassword2) && !password1.equals(password2)) {
            password2.setError("Passwords do not match");
            return;
        }

        else if (getPassword1.length() < 6) {
            password1.setError("Password has to have more than 6 char");
            return;
        }

        else if (!isValidEmail(getEmail)) {
            email.setError("Invalid Email");
            return;
        }

        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth.createUserWithEmailAndPassword(getEmail, getPassword1).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Register.this, "Register Successful",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Register.this, MainScreen.class);

                String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                DocumentReference reference = firebaseFirestore.collection("users").document(userID);
                Map<String, Object> user = new HashMap<>();
                user.put("username", getUsername);
                user.put("email", getEmail);
                user.put("firstTime", 1);
                reference.set(user).addOnSuccessListener(aVoid -> Log.d("TAG", "onSuccess: user profile was created for " + user)).addOnFailureListener(e -> Log.d("TAG", "onFailure: " +e));

                startActivity(intent);
            } else {
                Toast.makeText(Register.this, "Register failed",Toast.LENGTH_LONG).show();
            }
        });

        progressDialog.dismiss();
    }

    private boolean isValidEmail(CharSequence target) {
        return (Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}