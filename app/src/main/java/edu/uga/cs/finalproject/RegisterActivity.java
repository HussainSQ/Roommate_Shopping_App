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

import org.w3c.dom.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText fullName, email, password, passwordConf;
    Button buttonReg;
    TextView loginBut;
    FirebaseAuth auth;
    ProgressBar progBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        passwordConf = findViewById(R.id.passwordConf);
        buttonReg = findViewById(R.id.regButton);
        loginBut = findViewById(R.id.registeredAlr);
        auth = FirebaseAuth.getInstance();
        progBar = findViewById(R.id.progressBar);

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameB = fullName.getText().toString().trim();
                String emailB = email.getText().toString().trim();
                String passwordB = password.getText().toString().trim();
                String passwordConfB = passwordConf.getText().toString().trim();
                Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
                Matcher match = special.matcher(passwordB);
                boolean check = match.find();

                if(auth.getCurrentUser() != null){
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }

                if(TextUtils.isEmpty(nameB)){
                    fullName.setError("Enter your name -.-");
                }
                if(TextUtils.isEmpty(emailB)) {
                    email.setError("Why did you think this would work. Enter your email.");
                    return;
                }
                if(TextUtils.isEmpty(passwordB)) {
                    password.setError("Really? Enter a password.");
                    return;
                }
                if(!passwordB.equals(passwordConfB)) {
                    passwordConf.setError("Passwords do not match. Git gud.");
                    return;
                }
                if(passwordB.length() < 6){
                    password.setError("Password must be >= 6 characters");
                    return;
                } else {
                    boolean cap = true, digit = true, spec = true, low = true;
                    for(int i=0;i < passwordB.length(); i++){
                        System.out.println("Letter: " + passwordB.charAt(i));
                        if(Character.isUpperCase(passwordB.charAt(i)))
                            cap = false;
                        if(Character.isLowerCase(passwordB.charAt(i)))
                            low = false;
                        if(Character.isDigit(passwordB.charAt(i)))
                            digit = false;
                    }
                    if(check)
                        spec = false;
                    if(cap || low || digit || spec) {
                        password.setError("Password must contain a lowercase, an uppercase, a digit, and a special character");
                        return;
                    }
                }

                progBar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(emailB,passwordB).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "User Created. ", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                            System.out.println("Error: " + task.getException());
                        }
                    }
                });
            }
        });

        loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }
}
