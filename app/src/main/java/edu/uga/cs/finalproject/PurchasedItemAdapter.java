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

public class PurchasedItemAdapter extends FirestoreRecyclerAdapter<PurchasedItem, PurchasedItemAdapter.PurchasedHolder> {
    private PurchasedItemAdapter.OnItemClickListener listener;

    public PurchasedItemAdapter(@NonNull FirestoreRecyclerOptions<PurchasedItem> options, PurchasedItemAdapter.OnItemClickListener onItemClickListener) {
        super(options);
        this.listener = onItemClickListener;
    }


    @Override
    protected void onBindViewHolder(@NonNull PurchasedItemAdapter.PurchasedHolder holder, int position, @NonNull PurchasedItem model) {
        holder.cost.setText(model.getCost().toString());
        holder.itemName.setText(model.getItemID());
        holder.user.setText(model.getPurchaserID());
    }

    @NonNull
    @Override
    public PurchasedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchased_item, parent, false);
        return new PurchasedHolder(v, listener);
    }

    class PurchasedHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView itemName;
        TextView user;
        TextView cost;
        PurchasedItemAdapter.OnItemClickListener onItemClickListener;

        public PurchasedHolder(@NonNull View itemView, PurchasedItemAdapter.OnItemClickListener onItemClickListener) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            user =  itemView.findViewById(R.id.text_buyer);
            cost = itemView.findViewById(R.id.text_cost);
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

    public void removeItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }


    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position, View v);
    }

    public void setOnItemClickListener(PurchasedItemAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

}
