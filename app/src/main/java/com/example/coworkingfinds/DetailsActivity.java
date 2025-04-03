package com.example.coworkingfinds;

import static com.example.coworkingfinds.MainActivity.API_KEY;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    private TextView nameText, addressText, amenitiesText, ratingText, reviewsText;
    private ImageView placeImage;

    private static final String YELP_API_KEY = "YOUR_YELP_API_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        nameText = findViewById(R.id.location_name);
        addressText = findViewById(R.id.location_address);
        amenitiesText = findViewById(R.id.location_amenities);
        ratingText = findViewById(R.id.location_rating);
        reviewsText = findViewById(R.id.yelp_reviews_text);
        placeImage = findViewById(R.id.place_image);

        CoworkingSpace space = (CoworkingSpace) getIntent().getSerializableExtra("coworking_space");

        if (space != null) {
            nameText.setText(space.getName());
            addressText.setText(space.getAddress());
            ratingText.setText("Rating: " + space.getRating());

            // Show amenities
            List<String> amenitiesList = space.getAmenities();
            StringBuilder amenities = new StringBuilder("Amenities: ");
            if (amenitiesList != null && !amenitiesList.isEmpty()) {
                for (String amenity : amenitiesList) {
                    amenities.append(amenity).append(", ");
                }
                amenities.setLength(amenities.length() - 2);
            } else {
                amenities.append("No amenities listed");
            }
            amenitiesText.setText(amenities.toString());

            // Show image
            if (space.getPhotoReference() != null && !space.getPhotoReference().isEmpty()) {
                String imageUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                        "maxwidth=800" +
                        "&photo_reference=" + space.getPhotoReference() +
                        "&key=" + API_KEY;
                Glide.with(this).load(imageUrl).into(placeImage);
            }

            // Fetch Yelp reviews
            fetchYelpReviews(space.getLatitude(), space.getLongitude(), space.getName());
        }
    }

    private void fetchYelpReviews(double latitude, double longitude, String name) {
        String url = "https://api.yelp.com/v3/businesses/search?term=" + name +
                "&latitude=" + latitude + "&longitude=" + longitude + "&limit=1";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray businesses = response.getJSONArray("businesses");
                        if (businesses.length() > 0) {
                            String businessId = businesses.getJSONObject(0).getString("id");
                            fetchYelpBusinessReviews(businessId);
                        } else {
                            reviewsText.setText("No Yelp reviews found.");
                        }
                    } catch (Exception e) {
                        Log.e("YELP_SEARCH", "Parsing error", e);
                        reviewsText.setText("Failed to load Yelp reviews.");
                    }
                },
                error -> {
                    Log.e("YELP_SEARCH", "Request error", error);
                    reviewsText.setText("Failed to load Yelp reviews.");
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + YELP_API_KEY);
                return headers;
            }
        };

        queue.add(request);
    }

    private void fetchYelpBusinessReviews(String businessId) {
        String url = "https://api.yelp.com/v3/businesses/" + businessId + "/reviews";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray reviews = response.getJSONArray("reviews");
                        StringBuilder reviewsBuilder = new StringBuilder("Top Reviews:\n");
                        for (int i = 0; i < reviews.length(); i++) {
                            JSONObject review = reviews.getJSONObject(i);
                            String user = review.getJSONObject("user").getString("name");
                            String text = review.getString("text");
                            reviewsBuilder.append("- ").append(user).append(": ").append(text).append("\n\n");
                        }
                        reviewsText.setText(reviewsBuilder.toString().trim());
                    } catch (Exception e) {
                        Log.e("YELP_REVIEWS", "Parsing error", e);
                        reviewsText.setText("Failed to load reviews.");
                    }
                },
                error -> {
                    Log.e("YELP_REVIEWS", "Request error", error);
                    reviewsText.setText("Failed to load reviews.");
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + YELP_API_KEY);
                return headers;
            }
        };

        queue.add(request);
    }
}
