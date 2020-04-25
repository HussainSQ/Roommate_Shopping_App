package edu.uga.cs.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;

public class GroceryActivity extends AppCompatActivity {

    Button backout;
    Spinner spin;
    TextView grocHeader;
    FirebaseAuth auth;
    FirebaseFirestore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery);

        auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();
        backout = findViewById(R.id.backout);
        spin = findViewById(R.id.spin);
        grocHeader = findViewById(R.id.grocHeader);

        backout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });


    }
}
