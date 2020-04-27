package edu.uga.cs.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    Button logout;
    TextView welcome;
    FirebaseFirestore store;
    String userID;
    FirebaseAuth auth;
    Button grocList;
    Button purchList;
    Button share;
    ImageButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logout = findViewById(R.id.logoutBut);
        welcome = findViewById(R.id.name);
        grocList = findViewById(R.id.groceryList);
        purchList = findViewById(R.id.purchasedList);
        auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();
        share = findViewById(R.id.shareList);
        add = findViewById(R.id.addButt);


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog shareDialog = new ShareDialog();
                shareDialog.show(getSupportFragmentManager(),"Share dialog");

                /**
                final EditText email = new EditText(v.getContext());
                final EditText list = new EditText(v.getContext());
                email.setHint("Email of Recipient");
                list.setHint("Name of Grocery List");
                AlertDialog.Builder createList = new AlertDialog.Builder(v.getContext());
                createList.setTitle("Share Your List");
                createList.setView(email);
                createList.setView(list);
                createList.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DocumentReference documentReference = store.collection("UserConnector").document(userID);
                        Map<String,Object> userConnector = new HashMap<>();
                        userConnector.put("email", email);
                        userConnector.put("grocery list", list);
                        documentReference.set(userConnector).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("DEBUG", "onSuccess: list was shared " + userID);
                                Toast.makeText(HomeActivity.this,"List has been shared", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("DEBUG", "onFailure: " + e.toString());
                                Toast.makeText(HomeActivity.this, "Error: List was not created" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                createList.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                createList.create().show();
                **/
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText create = new EditText(v.getContext());
                AlertDialog.Builder createList = new AlertDialog.Builder(v.getContext());
                createList.setTitle("New Grocery List");
                createList.setMessage("Enter name of the list: ");
                createList.setView(create);
                createList.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String list = create.getText().toString();
                        DocumentReference documentReference = store.collection("GroceryList").document(list);
                        Map<String,Object> groceryLists = new HashMap<>();
                        groceryLists.put("name", list);
                        documentReference.set(groceryLists).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("DEBUG", "onSuccess: grocery list was created " + userID);
                                Toast.makeText(HomeActivity.this,"List has been created", Toast.LENGTH_SHORT).show();


                                DocumentReference ref = store.collection("UserConnector").document(String.valueOf(Math.random()*2));
                                Map<String,Object> userCon = new HashMap<>();
                                userCon.put("UserID", userID);
                                userCon.put("GroceryListID", list);
                                ref.set(userCon).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("DEBUG", "onSuccess: UserConnector was created " + userID);
                                        Toast.makeText(HomeActivity.this,"Connector has been updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("DEBUG", "onFailure: " + e.toString());
                                        Toast.makeText(HomeActivity.this, "Error: UserConnector was not created" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("DEBUG", "onFailure: " + e.toString());
                                Toast.makeText(HomeActivity.this, "Error: List was not created" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                createList.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                createList.create().show();
            }
        });

        final DocumentReference documentReference = store.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                welcome.setText("Welcome, " + documentSnapshot.getString("fullname"));
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

        grocList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, GroceryActivity.class);
                startActivity(intent);
            }
        });
    }
}
