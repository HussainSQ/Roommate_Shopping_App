package edu.uga.cs.finalproject;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.w3c.dom.Text;

public class GroceryItemAdapter extends FirestoreRecyclerAdapter<GroceryItem, GroceryItemAdapter.GroceryHolder>{

    public GroceryItemAdapter(@NonNull FirestoreRecyclerOptions<GroceryItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull GroceryHolder holder, int position, @NonNull GroceryItem model) {
        holder.title.setText(model.getTitle());
        holder.desc.setText(model.getDescription());
        holder.prio.setText(String.valueOf(model.getPriority()));
    }

    @NonNull
    @Override
    public GroceryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_item, parent, false);
        return new GroceryHolder(v);
    }

    public void buyItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class GroceryHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView desc;
        TextView prio;

        public GroceryHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_view_title);
            desc =  itemView.findViewById(R.id.text_desc);
            prio = itemView.findViewById(R.id.text_view_priority);


        }
    }
}
