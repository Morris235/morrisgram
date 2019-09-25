package com.example.morrisgram.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class Search_Adapter extends RecyclerView.Adapter<Search_Adapter.ItemViewHolder>{
    public Search_Adapter() {
    }

    @NonNull
    @Override
    public Search_Adapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewtype) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull Search_Adapter.ItemViewHolder itemViewHolder, int position) {

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
