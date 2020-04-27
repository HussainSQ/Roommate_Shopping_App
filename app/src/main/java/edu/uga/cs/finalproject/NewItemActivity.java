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

public class NewItemActivity extends AppCompatActivity {
    EditText title;
    private EditText desc;
    private NumberPicker pick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        title = findViewById(R.id.new_title);
        desc = findViewById(R.id.new_desc);
        pick = findViewById(R.id.picker_prio);

        pick.setMinValue(1);
        pick.setMaxValue(50);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Item");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.insert_item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.save:
                saveItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveItem(){
        String itemTitle = title.getText().toString();
        String description = desc.getText().toString();
        int priority = pick.getValue();

        if(itemTitle.trim().isEmpty() || description.trim().isEmpty()) {
            Toast.makeText(this, "Insert a item and note about item", Toast.LENGTH_LONG).show();
            return;
        }

        CollectionReference ref = FirebaseFirestore.getInstance().collection("Item");
        ref.add(new GroceryItem(itemTitle, description, priority));
        Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
        finish();
    }

}
