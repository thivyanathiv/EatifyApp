package com.example.eatifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // UI Components
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;

    // Firebase Reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI Components
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Set OnClickListener for Login Button
        btnLogin.setOnClickListener(v -> validateAndLogin());

        // Set OnClickListener for Register Button
        btnRegister.setOnClickListener(v -> registerUser());
    }

    // Method to Validate and Handle Login
    private void validateAndLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate Input Fields
        if (username.isEmpty()) {
            Toast.makeText(MainActivity.this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if Username Exists in Firebase
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username found, now check password
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String storedPassword = userSnapshot.child("password").getValue(String.class);
                        if (storedPassword != null && storedPassword.equals(password)) {
                            // Successful login
                            Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this, ShopActivity.class);
                            startActivity(intent);
                            finish(); // Close the current activity
                        } else {
                            Toast.makeText(MainActivity.this, "Invalid Password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // If username doesn't exist
                    Toast.makeText(MainActivity.this, "Username not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error reading from database: " + databaseError.getMessage());
            }
        });
    }

    // Method to Handle User Registration
    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate Input Fields
        if (username.isEmpty()) {
            Toast.makeText(MainActivity.this, "Username cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique ID for the user
        String userId = databaseReference.push().getKey();

        if (userId != null) {
            // Store user details in Firebase
            HashMap<String, String> userDetails = new HashMap<>();
            userDetails.put("username", username);
            userDetails.put("password", password);

            databaseReference.child(userId).setValue(userDetails).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Registration Failed. Try again!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
