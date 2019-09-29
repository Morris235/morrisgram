package com.example.morrisgram.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class LikeAlarm_Adapter extends RecyclerView.Adapter<LikeAlarm_Adapter.ItemViewHolder>{
    public LikeAlarm_Adapter() {
    }

    @NonNull
    @Override
    public LikeAlarm_Adapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewtype) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull LikeAlarm_Adapter.ItemViewHolder itemViewHolder, int position) {

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
