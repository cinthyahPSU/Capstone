package com.example.coworkingfinds;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CoworkingAdapter extends RecyclerView.Adapter<CoworkingAdapter.ViewHolder> {
    private List<CoworkingSpace> spacesList;
    private final Context context;
    private DatabaseHelper dbHelper;

    public CoworkingAdapter(Context context, List<CoworkingSpace> spacesList) {
        this.context = context;
        this.spacesList = spacesList;
        this.dbHelper = new DatabaseHelper(context);
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

        if (dbHelper.isFavorite(space.getName())) {
            holder.favoriteIcon.setImageResource(R.drawable.baseline_star_24);
        } else {
            holder.favoriteIcon.setImageResource(R.drawable.baseline_star_border_24); // Empty star
        }

        holder.favoriteIcon.setOnClickListener(v -> {
            if (dbHelper.isFavorite(space.getName())) {
                dbHelper.removeFavorite(space.getName());
                holder.favoriteIcon.setImageResource(R.drawable.baseline_star_border_24);
            } else {
                dbHelper.addFavorite(space);
                holder.favoriteIcon.setImageResource(R.drawable.baseline_star_24);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("coworking_space", space);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return spacesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address;
        ImageView favoriteIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.space_name);
            address = itemView.findViewById(R.id.space_address);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);
        }
    }
}
