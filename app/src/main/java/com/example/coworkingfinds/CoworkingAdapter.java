package com.example.coworkingfinds;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CoworkingAdapter extends RecyclerView.Adapter<CoworkingAdapter.ViewHolder> {
    private List<CoworkingSpace> spacesList;

    public CoworkingAdapter(List<CoworkingSpace> spacesList) {
        this.spacesList = spacesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coworking_space, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CoworkingSpace space = spacesList.get(position);
        Log.d("RecyclerView", "Displaying: " + space.getName() + ", " + space.getAddress());

        holder.name.setText(space.getName());
        holder.address.setText(space.getAddress());
    }


    @Override
    public int getItemCount() {
        return spacesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.space_name);
            address = itemView.findViewById(R.id.space_address);
        }
    }
}
