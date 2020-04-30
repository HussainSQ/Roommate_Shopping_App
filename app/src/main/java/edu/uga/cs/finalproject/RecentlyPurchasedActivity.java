package edu.uga.cs.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecentlyPurchasedActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, PurchasedItemAdapter.OnItemClickListener {

    RecyclerView recyclerView;
    TextView grocHeader;
    TextView grocHead;
    FirebaseAuth auth;
    FloatingActionButton add;
    FirebaseFirestore store = FirebaseFirestore.getInstance();
    private CollectionReference ref = store.collection("PurchasedList");
    private PurchasedItemAdapter adapter;
    String userID;
    List<String> groceryList = new ArrayList<>();
    String grocerySelected;
    Button settle;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_purchased);

        settle = findViewById(R.id.settle);
        auth = FirebaseAuth.getInstance();
        grocHeader = findViewById(R.id.grocHeader);
        grocHead = findViewById(R.id.grocName);
        add = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recycler_view1);
        userID = auth.getCurrentUser().getUid();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateList();
        setUpRecyclerView();
        recyclerView.setVisibility(View.VISIBLE);

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(RecentlyPurchasedActivity.this,
                android.R.layout.simple_spinner_item, groceryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        adapter.add("Select Grocery List");
        adapter.notifyDataSetChanged();
        spinner.setOnItemSelectedListener(this);

        settle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecentlyPurchasedActivity.this, CostsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        switch (position) {
            case 0:
                //NOTHING HAPPENS HERE
                // Whatever you want to happen when the first item gets selected
                break;
            default:
                grocerySelected = parent.getItemAtPosition(position).toString();
                System.out.println(grocerySelected);
                Intent intent = new Intent(getApplicationContext(), MockActivity.class);
                intent.putExtra("groc", grocerySelected);
                startActivity(intent);                //Make some call to populate recycle list
                setUpRecyclerView();

                Log.d("DEBUG", "JUST CHANGED LIST SELECTION!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    private void populateList(){
        store.collection("GroceryList").whereArrayContains("users", auth.getCurrentUser().getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DEBUG", document.getId() + " => " + document.getData());
                                groceryList.add(document.getString("name"));
                            }
                        }
                    }
                });
    }

    private void updateTitle(){
        grocHead.setText(grocerySelected);
    }

    private void setUpRecyclerView(){
        Log.d("DEBUG", "JUST SETUP RECYCLE VIEW!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!5555555555555555555555555555");
        CollectionReference ref = store.collection("PurchasedList");
        Query query = ref.whereEqualTo("groceryList", grocerySelected);
        System.out.println(query);
        FirestoreRecyclerOptions<PurchasedItem> options = new FirestoreRecyclerOptions.Builder<PurchasedItem>().setQuery(query, PurchasedItem.class).build();
        adapter = new PurchasedItemAdapter(options, this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.removeItem(viewHolder.getAdapterPosition());
                Toast.makeText(RecentlyPurchasedActivity.this, "Item been deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new PurchasedItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, View v) {
                String id = documentSnapshot.getId();
                Toast.makeText(getApplicationContext(), "Touch.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        setUpRecyclerView();

        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, int position, View v) {
        String id = documentSnapshot.getId();
    }
}
