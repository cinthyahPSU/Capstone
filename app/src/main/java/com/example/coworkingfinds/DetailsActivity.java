package com.example.coworkingfinds;

import static com.example.coworkingfinds.MainActivity.API_KEY;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    private TextView nameText, addressText, amenitiesText, RatingText;
    private ImageView placeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initialize UI elements
        nameText = findViewById(R.id.location_name);
        addressText = findViewById(R.id.location_address);
        amenitiesText = findViewById(R.id.location_amenities);
        RatingText = findViewById(R.id.location_rating);
        placeImage = findViewById(R.id.place_image);

        // Get the object from intent
        CoworkingSpace space = (CoworkingSpace) getIntent().getSerializableExtra("coworking_space");

        if (space != null) {
            nameText.setText(space.getName());
            addressText.setText(space.getAddress());
            RatingText.setText("Rating: " + space.getRating());

            // Get amenities
            List<String> amenitiesList = space.getAmenities();
            if (amenitiesList != null && !amenitiesList.isEmpty()) {
                String amenities = "Amenities: " + String.join(", ", amenitiesList);
                amenitiesText.setText(amenities);
            } else {
                amenitiesText.setText("Amenities: No amenities listed");
            }

            // Load image if available
            if (space.getPhotoReference() != null && !space.getPhotoReference().isEmpty()) {
                String imageUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                        "maxwidth=800" +
                        "&photo_reference=" + space.getPhotoReference() +
                        "&key=" + API_KEY;

                Glide.with(this).load(imageUrl).into(placeImage);
            }
        } else {
            nameText.setText("No Details Available");
        }
    }
}
