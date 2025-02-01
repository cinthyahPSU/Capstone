package com.example.coworkingfinds;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView welcomeText;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        welcomeText = findViewById(R.id.welcome_text);
        logoutButton = findViewById(R.id.logout_button);

        // Get Current User
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // If no user is logged in, redirect to login screen
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            finish();
        } else {
            // Fetch user data from Firestore
            String userId = user.getUid();
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

        // Logout button functionality
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            finish();
        });
    }
}
