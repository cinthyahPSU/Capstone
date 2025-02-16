package com.example.coworkingfinds;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String API_KEY = "AIzaSyDY1GvJtZ7CNAoNnKmZQ1em1s2Momlqs18";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView welcomeText;
    private Button logoutButton, openMapButton;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private CoworkingAdapter adapter;
    private List<CoworkingSpace> coworkingSpacesList;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), API_KEY);
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI Elements
        welcomeText = findViewById(R.id.welcome_text);
        logoutButton = findViewById(R.id.logout_button);
        openMapButton = findViewById(R.id.openMapButton);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);

        // RecyclerView setup
        coworkingSpacesList = new ArrayList<>();
        adapter = new CoworkingAdapter(coworkingSpacesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Initialize Location Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request user location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchUserLocation();
        }

        // Check current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            finish();
        } else {
            loadUserData(user.getUid());
        }

        // Logout functionality
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            finish();
        });

        // Open Map when the button is clicked
        openMapButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });

        // SearchView functionality
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

    // Request user location
    private void fetchUserLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                searchNearbyCoworkingSpaces(location.getLatitude(), location.getLongitude());
            } else {
                Log.e("MainActivity", "Failed to get location");
                Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Search coworking spaces using Google Places API
    private void searchNearbyCoworkingSpaces(double latitude, double longitude) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + latitude + "," + longitude +
                "&radius=5000" +
                "&type=cafe" +
                "&keyword=coworking" +
                "&key=" + API_KEY;

        Log.d("PLACES_API_REQUEST", "Request URL: " + url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("PLACES_API_RESPONSE", response.toString());  // Log API response
                    try {
                        JSONArray results = response.getJSONArray("results");
                        coworkingSpacesList.clear();

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);
                            String name = place.optString("name", "Unknown Name");
                            String address = place.optString("vicinity", "No Address Available");

                            Log.d("PLACES_API_DATA", "Coworking Space: " + name + ", Address: " + address);
                            coworkingSpacesList.add(new CoworkingSpace(name, address));
                        }

                        Log.d("PLACES_API_RESULT_COUNT", "Total Places Found: " + coworkingSpacesList.size());
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e("PLACES_API_ERROR", "Error parsing places JSON", e);
                    }
                },
                error -> Log.e("PLACES_API_ERROR", "API request failed", error)
        );

        requestQueue.add(request);
    }

    // Load user data from Firestore
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

    // Search coworking spaces from Firestore (Not Google Maps)
    private void searchCoworkingSpaces(String keyword) {
        CollectionReference spacesRef = db.collection("coworking_spaces");

        spacesRef.whereGreaterThanOrEqualTo("name", keyword)
                .whereLessThanOrEqualTo("name", keyword + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        coworkingSpacesList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CoworkingSpace space = document.toObject(CoworkingSpace.class);
                            coworkingSpacesList.add(space);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("FirestoreSearch", "Error getting documents.", task.getException());
                    }
                });
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchUserLocation();
            } else {
                Toast.makeText(this, "Location permission required!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
