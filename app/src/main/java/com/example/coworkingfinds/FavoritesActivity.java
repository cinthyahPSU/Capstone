package com.example.coworkingfinds;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView favoritesRecyclerView;
    private CoworkingAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView noFavoritesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        noFavoritesText = findViewById(R.id.no_favorites_text);
        dbHelper = new DatabaseHelper(this);

        loadFavorites();
    }

    private void loadFavorites() {
        List<CoworkingSpace> favoritesList = dbHelper.getFavorites();

        if (favoritesList.isEmpty()) {
            noFavoritesText.setVisibility(View.VISIBLE);
            favoritesRecyclerView.setVisibility(View.GONE);
        } else {
            noFavoritesText.setVisibility(View.GONE);
            favoritesRecyclerView.setVisibility(View.VISIBLE);

            adapter = new CoworkingAdapter(this, favoritesList);
            favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            favoritesRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }
}
