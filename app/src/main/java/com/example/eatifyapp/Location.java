package com.example.eatifyapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Location extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // BottomNavigationView setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_location); // Highlight "Location" tab

        Map<Integer, Class<?>> navItemToActivityMap = new HashMap<>();
        navItemToActivityMap.put(R.id.nav_home, ShopActivity.class);
        navItemToActivityMap.put(R.id.nav_order, OrderViewActivity.class);
        navItemToActivityMap.put(R.id.nav_profile, Profile.class);
        navItemToActivityMap.put(R.id.nav_location, Location.class);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Class<?> activityClass = navItemToActivityMap.get(item.getItemId());
            if (activityClass != null && !activityClass.equals(this.getClass())) {
                startActivity(new Intent(getApplicationContext(), activityClass));
                return true;
            }
            return false; // Default case when item ID is not found
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            enableUserLocation();
        }
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getDeviceLocation();
        }
    }

    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(userLocation).title("You are here"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                        // Save the location to Firebase
                        saveLocationToFirebase(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(Location.this, "Unable to fetch location. Try again.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(Location.this, "Failed to get location", Toast.LENGTH_SHORT).show());
    }

    private void saveLocationToFirebase(double latitude, double longitude) {
        // Get a reference to the Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference locationsRef = database.getReference("locations");

        // Create a new map for location data
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", latitude);
        locationData.put("longitude", longitude);

        // Save the location data to Firebase under a unique ID
        locationsRef.push().setValue(locationData)
                .addOnSuccessListener(aVoid -> Toast.makeText(Location.this, "Location saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(Location.this, "Failed to save location", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
