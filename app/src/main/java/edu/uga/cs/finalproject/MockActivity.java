package edu.uga.cs.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collection;

public class MockActivity extends AppCompatActivity {
    EditText title;
    private EditText desc;
    private NumberPicker pick;
    FirebaseFirestore store = FirebaseFirestore.getInstance();
    String grocerySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        Intent intent = getIntent();
        grocerySelected = intent.getStringExtra("groc");
        title = findViewById(R.id.new_title);
        desc = findViewById(R.id.new_desc);
        pick = findViewById(R.id.picker_prio);

        pick.setMinValue(1);
        pick.setMaxValue(50);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Item");

    }
}
