package com.example.morrisgram.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class Home_Adapter extends RecyclerView.Adapter<Home_Adapter.ItemViewHolder>{

    public Home_Adapter() {
    }

    @NonNull
    @Override
    public Home_Adapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewtype) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull Home_Adapter.ItemViewHolder itemViewHolder, int position) {

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
