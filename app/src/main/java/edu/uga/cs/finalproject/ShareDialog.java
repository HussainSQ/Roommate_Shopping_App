package edu.uga.cs.finalproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Config;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareDialog extends AppCompatDialogFragment {
    private EditText emailDia;
    private EditText grocList;
    FirebaseFirestore store;
    String userID;
    FirebaseAuth auth;
    private static ArrayList<Type> arrayList = new ArrayList<>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        auth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();
        userID = auth.getCurrentUser().getUid();


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("Share List")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String email = emailDia.getText().toString();
                        final String groc = grocList.getText().toString();


                        if (TextUtils.isEmpty(email) || email.equals("")) {
                            emailDia.setError("Enter an email");
                            return;
                        }
                        if (TextUtils.isEmpty(groc) || groc.equals("")) {
                            grocList.setError("Enter a grocery list");
                            return;
                        }
                        store.collection("GroceryList").document(groc).update("users", FieldValue.arrayUnion(email));

                    }
                });

        emailDia = view.findViewById(R.id.emailDialog);
        grocList = view.findViewById(R.id.grocDialog);

        return builder.create();
    }
}
