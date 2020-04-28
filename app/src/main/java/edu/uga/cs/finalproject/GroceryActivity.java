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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroceryActivity extends AppCompatActivity implements GroceryItemAdapter.OnItemClickListener{

    Spinner spin;
    RecyclerView recyclerView;
    TextView grocHeader;
    TextView grocHead;
    FirebaseAuth auth;
    FloatingActionButton add;
    FirebaseFirestore store = FirebaseFirestore.getInstance();
    private CollectionReference ref = store.collection("Item");
    private GroceryItemAdapter adapter;
    String userID;
    List<String> groceryList = new ArrayList<>();
    String grocerySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery);

        auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();
        spin = findViewById(R.id.spinner);
        grocHeader = findViewById(R.id.grocHeader);
        grocHead = findViewById(R.id.grocName);
        add = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recycler_view);
        userID = auth.getCurrentUser().getUid();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateList();
        setUpRecyclerView();
        recyclerView.setVisibility(View.VISIBLE);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groceryList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(arrayAdapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(GroceryActivity.this,parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                updateTitle();
                grocerySelected = groceryList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewItemActivity.class));
            }
        });
    }

    private void populateList(){
        store.collection("UserConnector").whereEqualTo("UserID", userID).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("DEBUG", document.getId() + " => " + document.getData());
                        groceryList.add(document.getString("GroceryListID"));
                    }
                }
            }
        });
    }

    private void updateTitle(){
        DocumentReference documentReference = store.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                grocHead.setText(documentSnapshot.getString("GroceryListID"));
            }
        });
    }

    private void setUpRecyclerView(){
        Query query = ref.orderBy("priority", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<GroceryItem> options = new FirestoreRecyclerOptions.Builder<GroceryItem>().setQuery(query, GroceryItem.class).build();
        adapter = new GroceryItemAdapter(options, this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags,swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.removeItem(viewHolder.getAdapterPosition());
                Toast.makeText(GroceryActivity.this,"Item been deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new GroceryItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position, View v) {
                String id = documentSnapshot.getId();
                Toast.makeText(getApplicationContext(), "work pls", Toast.LENGTH_SHORT).show();
                purchase(id, grocerySelected, position, v);
            }
        });

     /**
        adapter.setOnItemClickListener(new GroceryItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                groceryList.get(position);
                GroceryItem groceryItem = documentSnapshot.toObject(GroceryItem.class);
                String id = documentSnapshot.getId();
                String path = documentSnapshot.getReference().getPath();
                purchase(id, grocerySelected, position);

            }
        });

         **/
    }

    public void purchase(final String id, final String grocerySelected, final int position, View v){
        final EditText create = new EditText(v.getContext());
        final AlertDialog.Builder createList = new AlertDialog.Builder(v.getContext());
        createList.setTitle("Purchased item?");
        createList.setMessage("Cost of item :");
        createList.setView(create);
        createList.setPositiveButton("Yup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String cost = create.getText().toString();
                DocumentReference documentReference = store.collection("PurchasedList").document(String.valueOf(Math.random() * 3));
                Map<String, Object> itemList = new HashMap<>();
                itemList.put("purchaser", userID);
                itemList.put("Item", id);
                itemList.put("groceryList", grocerySelected);
                itemList.put("cost", cost);
                documentReference.set(itemList).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DEBUG", "onSuccess: grocery list was created " + userID);
                        Toast.makeText(GroceryActivity.this, "Item has been putchased", Toast.LENGTH_SHORT).show();
                        adapter.removeItem(position);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("DEBUG", "onFailure: " + e.toString());
                        Toast.makeText(GroceryActivity.this, "Error: Item could not be purchased" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        createList.setNegativeButton("Nope", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        createList.create().show();
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
        purchase(id, grocerySelected, position, v);
    }
}
