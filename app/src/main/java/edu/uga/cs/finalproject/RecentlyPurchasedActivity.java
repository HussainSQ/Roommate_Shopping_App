package edu.uga.cs.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.List;

public class RecentlyPurchasedActivity extends AppCompatActivity implements PurchasedItemAdapter.OnItemClickListener {


    Spinner spin;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recently_purchased);

        settle = findViewById(R.id.settle);
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
                Toast.makeText(RecentlyPurchasedActivity.this,parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                updateTitle();
                grocerySelected = groceryList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        settle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecentlyPurchasedActivity.this, CostsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateTitle() {
        DocumentReference documentReference = store.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                grocHead.setText(documentSnapshot.getString("GroceryListID"));
            }
        });
    }

    private void populateList() {
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

    private void setUpRecyclerView() {
        Query query = ref.orderBy("cost", Query.Direction.DESCENDING);
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
                Toast.makeText(getApplicationContext(), "Touch.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(DocumentSnapshot documentSnapshot, int position, View v) {
        String id = documentSnapshot.getId();
        //purchase(id, grocerySelected, position, v);
    }
}
