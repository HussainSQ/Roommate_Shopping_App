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

public class GroceryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, GroceryItemAdapter.OnItemClickListener {

    RecyclerView recyclerView;
    TextView grocHeader;
    TextView grocHead;
    FirebaseAuth auth;
    FloatingActionButton add;
    FirebaseFirestore store = FirebaseFirestore.getInstance();
    private GroceryItemAdapter adapter;
    String userID;
    List<String> groceryList = new ArrayList<>();
    String grocerySelected;
    String name = "";

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery);
        Log.d("DEBUG", "Beginning of ONCREATE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();
        grocHeader = findViewById(R.id.grocHeader);
        grocHead = findViewById(R.id.grocName);
        add = findViewById(R.id.floatingActionButton);
        recyclerView = findViewById(R.id.recycler_view);
        userID = auth.getCurrentUser().getUid();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        populateList();
        setUpRecyclerView();
        recyclerView.setVisibility(View.VISIBLE);

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(GroceryActivity.this,
                android.R.layout.simple_spinner_item, groceryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        adapter.add("Select Grocery List");
        adapter.notifyDataSetChanged();
        spinner.setOnItemSelectedListener(this);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewItemActivity.class);
                intent.putExtra("groc", grocerySelected);
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
                Intent intent = new Intent(getApplicationContext(), NewItemActivity.class);
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
        CollectionReference ref = store.collection("ItemList");
        Query query = ref.whereEqualTo("groceryList", grocerySelected);
        System.out.println(query);
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
                purchase(documentSnapshot, id, grocerySelected, position, v);
            }
        });
    }

    public void purchase(final DocumentSnapshot doc, final String id, final String grocerySelected, final int position, View v){
        final EditText create = new EditText(v.getContext());
        create.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final AlertDialog.Builder createList = new AlertDialog.Builder(v.getContext());
        createList.setTitle("Purchased item?");
        createList.setMessage("Cost of item :");
        createList.setView(create);
        createList.setPositiveButton("Yup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String cost = create.getText().toString();
                store.collection("ItemList")
                        .whereEqualTo("groceryList", grocerySelected)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot query = task.getResult();
                            for (DocumentSnapshot elem : query) {
                                if (elem.get("title").equals(doc.getData().get("title"))) {
                                    PurchasedItem purchasedItem = new PurchasedItem(cost, elem.get("title").toString(), grocerySelected, auth.getCurrentUser().getEmail());
                                    store.collection("PurchasedList").document(elem.get("title").toString()).set(purchasedItem);
                                    store.collection("ItemList").document(elem.get("title").toString()).delete();
                                }
                            }
                        }
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
        purchase(documentSnapshot, id, grocerySelected, position, v);
    }
}
