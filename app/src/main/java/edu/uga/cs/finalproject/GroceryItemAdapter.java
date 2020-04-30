package edu.uga.cs.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class GroceryItemAdapter extends FirestoreRecyclerAdapter<GroceryItem, GroceryItemAdapter.GroceryHolder>{
    private OnItemClickListener listener;

    public GroceryItemAdapter(@NonNull FirestoreRecyclerOptions<GroceryItem> options, OnItemClickListener onItemClickListener) {
        super(options);
        this.listener = onItemClickListener;
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
        return new GroceryHolder(v, listener);
    }

    public void removeItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class GroceryHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        TextView desc;
        TextView prio;
        OnItemClickListener onItemClickListener;

        public GroceryHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            title = itemView.findViewById(R.id.text_view_title);
            desc =  itemView.findViewById(R.id.text_desc);
            prio = itemView.findViewById(R.id.text_view_priority);
            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(getSnapshots().getSnapshot(pos),pos, v);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position, View v);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
