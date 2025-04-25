package com.example.coworkingfinds;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    static final String API_KEY = "AIzaSyDY1GvJtZ7CNAoNnKmZQ1em1s2Momlqs18";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView welcomeText;
    private Button logoutButton, openMapButton, applyFilterButton;
    private SearchView searchView;
    private Spinner filterSpinner;
    private RecyclerView recyclerView;
    private CoworkingAdapter adapter;
    List<CoworkingSpace> coworkingSpacesList;
    private FusedLocationProviderClient fusedLocationClient;
    Location lastUserLocation;

    private String photoReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(androidx.appcompat.R.style.Theme_AppCompat);
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        filterSpinner = findViewById(R.id.filter_spinner);
        applyFilterButton = findViewById(R.id.apply_filter_button);
        welcomeText = findViewById(R.id.welcome_text);
        logoutButton = findViewById(R.id.logout_button);
        openMapButton = findViewById(R.id.openMapButton);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);

        ImageButton openFavoritesButton = findViewById(R.id.openFavoritesButton);
        openFavoritesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.filter_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        applyFilterButton.setOnClickListener(v -> applyFilters());

        coworkingSpacesList = new ArrayList<>();
        this.adapter = new CoworkingAdapter(this,coworkingSpacesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(this.adapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchUserLocation();
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            finish();
        } else {
            loadUserData(user.getUid());
        }

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            finish();
        });

        openMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCoworkingSpaces(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchCoworkingSpaces(newText);
                return true;
            }
        });
    }

    private void loadUserData(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        welcomeText.setText("Welcome, " + email + "!");
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(MainActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show()
                );
    }

    private void fetchUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                lastUserLocation = location;
                applyFilters();
            } else {
                Log.e("MainActivity", "Failed to get location");
                Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void applyFilters() {
        if (lastUserLocation == null) {
            Log.e("FILTER_ERROR", "User location is not available yet.");
            Toast.makeText(this, "Fetching location, please wait...", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedFilter = filterSpinner.getSelectedItem().toString();
        double radius = 10000;
        String keyword = "coworking";
        String priceLevel = "";

        switch (selectedFilter) {
            case "No Filters":
                searchNearbyCoworkingSpaces(lastUserLocation.getLatitude(), lastUserLocation.getLongitude(), 10000, "coworking", "");
                return;
            case "Within 1 km":
                radius = 1000;
                break;
            case "Within 5 km":
                radius = 5000;
                break;
            case "Cheap ($)":
                priceLevel = "&minprice=0&maxprice=1";
                break;
            case "Mid-range ($$)":
                priceLevel = "&minprice=2&maxprice=3";
                break;
            case "Expensive ($$$)":
                priceLevel = "&minprice=4";
                break;
            case "Has WiFi":
                keyword += " wifi";
                break;
            case "Has Parking":
                keyword += " parking";
                break;
        }

        searchNearbyCoworkingSpaces(lastUserLocation.getLatitude(), lastUserLocation.getLongitude(), radius, keyword, priceLevel);
    }

    private void searchNearbyCoworkingSpaces(double latitude, double longitude, double radius, String keyword, String priceFilter) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + latitude + "," + longitude +
                "&radius=" + radius +
                "&type=cafe" +
                "&keyword=" + keyword +
                priceFilter +
                "&key=" + API_KEY;

        Log.d("PLACES_API_REQUEST", "Request URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        coworkingSpacesList.clear();

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);
                            String name = place.optString("name", "Unknown Name");
                            String address = place.optString("vicinity", "No Address Available");

                            String photoReference = "";
                            if (place.has("photos")) {
                                JSONArray photos = place.getJSONArray("photos");
                                if (photos.length() > 0) {
                                    photoReference = photos.getJSONObject(0).optString("photo_reference", "");
                                }
                            }

                            double rating = place.optDouble("rating", 0.0);

                            JSONArray typesArray = place.optJSONArray("types");
                            List<String> amenitiesList = new ArrayList<>();
                            if (typesArray != null) {
                                for (int j = 0; j < typesArray.length(); j++) {
                                    String type = typesArray.getString(j);
                                    if (type.contains("wifi") || type.equals("internet_cafe")) {
                                        amenitiesList.add("WiFi");
                                    }
                                    if (type.contains("parking") || type.equals("car_parking")) {
                                        amenitiesList.add("Parking");
                                    }
                                }
                            }

                            JSONObject geometry = place.optJSONObject("geometry");
                            JSONObject location = geometry != null ? geometry.optJSONObject("location") : null;
                            double lat = location != null ? location.optDouble("lat", 0.0) : 0.0;
                            double lng = location != null ? location.optDouble("lng", 0.0) : 0.0;

                            CoworkingSpace space = new CoworkingSpace(name, address, photoReference, amenitiesList, rating, lat, lng);

                            // Calculate distance
                            Location userLoc = new Location("");
                            userLoc.setLatitude(latitude);
                            userLoc.setLongitude(longitude);

                            Location placeLoc = new Location("");
                            placeLoc.setLatitude(lat);
                            placeLoc.setLongitude(lng);

                            double distanceInKm = userLoc.distanceTo(placeLoc) / 1000.0;
                            space.setDistance(distanceInKm);

                            // Heuristic scoring
                            double score = ((5 - distanceInKm) * 0.3) + (rating * 0.7);
                            space.setHeuristicScore(score);

                            coworkingSpacesList.add(space);
                        }

                        // Sort by heuristic score descending
                        coworkingSpacesList.sort((a, b) -> Double.compare(b.getHeuristicScore(), a.getHeuristicScore()));

                        adapter = new CoworkingAdapter(this, coworkingSpacesList);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Log.e("PLACES_API_ERROR", "Error parsing places JSON", e);
                    }
                },
                error -> Log.e("PLACES_API_ERROR", "API request failed", error)
        );

        requestQueue.add(request);
    }


    private void searchCoworkingSpaces(String keyword) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?" +
                "query=" + Uri.encode(keyword + " coworking") +
                "&key=" + API_KEY;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        coworkingSpacesList.clear();

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);
                            String name = place.optString("name", "Unknown Name");
                            String address = place.optString("formatted_address", "No Address Available");

                            String photoReference = "";
                            if (place.has("photos")) {
                                JSONArray photos = place.getJSONArray("photos");
                                if (photos.length() > 0) {
                                    photoReference = photos.getJSONObject(0).optString("photo_reference", "");
                                }
                            }

                            double rating = place.optDouble("rating", 0.0);
                            double lat = place.optJSONObject("geometry").optJSONObject("location").optDouble("lat");
                            double lng = place.optJSONObject("geometry").optJSONObject("location").optDouble("lng");

                            coworkingSpacesList.add(new CoworkingSpace(name, address, photoReference, new ArrayList<>(), rating, lat, lng));
                        }

                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("PLACES_API_ERROR", "Parsing error", e);
                    }
                },
                error -> Log.e("PLACES_API_ERROR", "API request failed", error)
        );

        requestQueue.add(request);
    }
}
