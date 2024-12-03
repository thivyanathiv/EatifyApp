package com.example.eatifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        EditText nameEditText = findViewById(R.id.nameTextView);
        EditText phoneEditText = findViewById(R.id.phoneTextView);
        EditText addressEditText = findViewById(R.id.addressTextView);
        EditText emailEditText = findViewById(R.id.emailTextView);
        Button saveButton = findViewById(R.id.saveBtn);

        // Firebase setup
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("UserDetails");

        // Fetch and display existing data
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);

                    // Set the retrieved data in the EditText fields
                    nameEditText.setText(name != null ? name : "");
                    phoneEditText.setText(phone != null ? phone : "");
                    addressEditText.setText(address != null ? address : "");
                    emailEditText.setText(email != null ? email : "");
                } else {
                    Toast.makeText(Profile.this, "No user details found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Failed to fetch details", Toast.LENGTH_SHORT).show();
            }
        });

        // Save button functionality
        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();

            if (!name.isEmpty() && !phone.isEmpty() && !address.isEmpty() && !email.isEmpty()) {
                HashMap<String, String> userDetails = new HashMap<>();
                userDetails.put("name", name);
                userDetails.put("phone", phone);
                userDetails.put("address", address);
                userDetails.put("email", email);

                databaseReference.setValue(userDetails)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(Profile.this, "Details updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(Profile.this, "Failed to update details", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(Profile.this, "All fields are required", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile); // Set default item as Profile

        // Map of navigation items
        Map<Integer, Class<?>> navItemToActivityMap = new HashMap<>();
        navItemToActivityMap.put(R.id.nav_home, ShopActivity.class);
        navItemToActivityMap.put(R.id.nav_order, OrderViewActivity.class);
        navItemToActivityMap.put(R.id.nav_profile, Profile.class);
        navItemToActivityMap.put(R.id.nav_location, Location.class);

        // Navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Class<?> activityClass = navItemToActivityMap.get(item.getItemId());
            if (activityClass != null) {
                startActivity(new Intent(getApplicationContext(), activityClass));
                return true;
            }
            return false; // Default case
        });
    }
}
