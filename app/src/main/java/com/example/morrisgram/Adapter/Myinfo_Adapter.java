package com.example.morrisgram.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class Myinfo_Adapter extends RecyclerView.Adapter<Myinfo_Adapter.ItemViewHolder>{
    public Myinfo_Adapter() {
    }

    @NonNull
    @Override
    public Myinfo_Adapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewtype) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull Myinfo_Adapter.ItemViewHolder itemViewHolder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
