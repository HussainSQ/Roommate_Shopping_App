package edu.uga.cs.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button logbut;
    TextView register;
    ProgressBar progBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.emailLog);
        password = findViewById(R.id.passwordLog);
        progBar = findViewById(R.id.progressBarLog);
        auth = FirebaseAuth.getInstance();
        logbut = findViewById(R.id.logButton);
        register = findViewById(R.id.register);

        logbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailB = email.getText().toString().trim();
                String passwordB = password.getText().toString().trim();

                if(TextUtils.isEmpty(emailB)) {
                    email.setError("Why did you think this would work. Enter your email.");
                    return;
                }
                if(TextUtils.isEmpty(passwordB)) {
                    password.setError("Really? Enter a password.");
                    return;
                }

                progBar.setVisibility(View.VISIBLE);

                auth.signInWithEmailAndPassword(emailB, passwordB).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });
    }
}
