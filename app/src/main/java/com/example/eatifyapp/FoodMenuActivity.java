package com.example.eatifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class FoodMenuActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);
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

        // Get the shop name passed from the ShopActivity
        String shopName = getIntent().getStringExtra("shop_name");
        // Set the title or any other UI element with the shop name
        // For example, a TextView to display the shop name
        // TextView shopTitle = findViewById(R.id.shopTitle);
        // shopTitle.setText(shopName);

        // Set click listeners for food items
        LinearLayout foodItem1 = findViewById(R.id.foodItem1);
        LinearLayout foodItem2 = findViewById(R.id.foodItem2);
        LinearLayout foodItem3 = findViewById(R.id.foodItem3);
        LinearLayout foodItem4 = findViewById(R.id.foodItem4);
        LinearLayout foodItem5 = findViewById(R.id.foodItem5);
        LinearLayout foodItem6 = findViewById(R.id.foodItem6);
        LinearLayout foodItem7 = findViewById(R.id.foodItem7);
        LinearLayout foodItem8 = findViewById(R.id.foodItem8);
        LinearLayout foodItem9 = findViewById(R.id.foodItem9);

        // When a food item is clicked, navigate to the order view
        foodItem1.setOnClickListener(v -> openOrderView("Fried Rice"));
        foodItem2.setOnClickListener(v -> openOrderView("Briyani"));
        foodItem3.setOnClickListener(v -> openOrderView("Mongolian"));
        foodItem4.setOnClickListener(v -> openOrderView("Burgar"));
        foodItem5.setOnClickListener(v -> openOrderView("Submerin"));
        foodItem6.setOnClickListener(v -> openOrderView("Pizza"));
        foodItem7.setOnClickListener(v -> openOrderView("Egg Fried Rice"));
        foodItem8.setOnClickListener(v -> openOrderView("Mutton Briyani"));
        foodItem9.setOnClickListener(v -> openOrderView("Noodels"));
    }

    // Method to navigate to the order view with food details
    private void openOrderView(String foodName) {
        Intent intent = new Intent(this, OrderViewActivity.class);
        intent.putExtra("food_name", foodName);
        startActivity(intent);
    }
}
