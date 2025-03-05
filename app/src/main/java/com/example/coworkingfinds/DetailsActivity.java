package com.example.coworkingfinds;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class DetailsActivity extends AppCompatActivity {
    private TextView nameText, addressText, amenitiesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initialize UI elements
        nameText = findViewById(R.id.location_name);
        addressText = findViewById(R.id.location_address);
        amenitiesText = findViewById(R.id.location_amenities);

        // Get data from intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String address = intent.getStringExtra("address");
        double latitude = intent.getDoubleExtra("latitude", 0.0);
        double longitude = intent.getDoubleExtra("longitude", 0.0);
        String[] amenitiesArray = intent.getStringArrayExtra("amenities");
        String amenities = (amenitiesArray != null) ? Arrays.toString(amenitiesArray) : "No amenities listed";

        // Set text values
        nameText.setText(name);
        addressText.setText(address);
        amenitiesText.setText("Amenities: " + amenities);
    }
}
