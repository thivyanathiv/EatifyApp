package com.example.eatifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class ShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // Set click listeners for each shop
        LinearLayout shop1 = findViewById(R.id.Shop1);
        LinearLayout shop2 = findViewById(R.id.Shop2);
        LinearLayout shop3 = findViewById(R.id.Shop3);
        LinearLayout shop4 = findViewById(R.id.Shop4);
        LinearLayout shop5 = findViewById(R.id.Shop5);

        // Navigate to the Food Menu when a shop is clicked
        shop1.setOnClickListener(v -> openFoodMenu("MR Chaii"));
        shop2.setOnClickListener(v -> openFoodMenu("Aahaa"));
        shop3.setOnClickListener(v -> openFoodMenu("Treatoo"));
        shop4.setOnClickListener(v -> openFoodMenu("SixFlave"));
        shop5.setOnClickListener(v -> openFoodMenu("GamaGama"));

        // Initialize BottomNavigationView and set default item
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home); // Set the default item selected

        // Create a map of item ID and the corresponding Intent
        Map<Integer, Class<?>> navItemToActivityMap = new HashMap<>();
        navItemToActivityMap.put(R.id.nav_home, ShopActivity.class);
        navItemToActivityMap.put(R.id.nav_order, OrderViewActivity.class);
        navItemToActivityMap.put(R.id.nav_profile, Profile.class);
        navItemToActivityMap.put(R.id.nav_location, Location.class);

        // Set listener for navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Class<?> activityClass = navItemToActivityMap.get(item.getItemId());
            if (activityClass != null) {
                startActivity(new Intent(getApplicationContext(), activityClass));
                return true;
            }
            return false; // Default case when the item ID is not found
        });
    }

    // Method to navigate to the Food Menu with the shop name
    private void openFoodMenu(String shopName) {
        Intent intent = new Intent(this, FoodMenuActivity.class);
        intent.putExtra("shop_name", shopName);
        startActivity(intent);
    }
}
