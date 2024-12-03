package com.example.eatifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class OrderViewActivity extends AppCompatActivity {

    private TextView foodNameTextView, foodPriceTextView, totalAmountTextView;
    private EditText quantityEditText;
    private Button confirmButton, cancelButton, viewOrderButton;
    private double foodPrice = 1000.00;



    // Firebase references
    private DatabaseReference databaseReference;
    private String orderId; // To store the current order's unique ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);

        // Initialize Views
        foodNameTextView = findViewById(R.id.foodNameTextView);
        foodPriceTextView = findViewById(R.id.foodPriceTextView);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        quantityEditText = findViewById(R.id.quantityEditText);
        confirmButton = findViewById(R.id.conformButton);
        cancelButton = findViewById(R.id.cancelButton);
        viewOrderButton = findViewById(R.id.viewOrderButton); // View Orders button

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");

        // Get the food name passed from the FoodMenuActivity
        String foodName = getIntent().getStringExtra("food_name");

        // Set the food name in the TextView
        foodNameTextView.setText(foodName);

        // Example: Set the food price (this can be dynamic based on food item)
        foodPriceTextView.setText("Price: Rs" + foodPrice);

        // Set OnClickListener for Confirm Button
        confirmButton.setOnClickListener(v -> calculateAndSaveOrder());

        // Set OnClickListener for Cancel Button
        cancelButton.setOnClickListener(v -> cancelOrder());

        // Set OnClickListener for View Orders Button
        viewOrderButton.setOnClickListener(v -> viewOrders());

        // Initialize BottomNavigationView and set default item
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_order); // Set the default item selected

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

    // Method to calculate the total amount based on quantity and price
    private void calculateAndSaveOrder() {
        String quantityText = quantityEditText.getText().toString().trim();

        // Validate the quantity input
        if (quantityText.isEmpty()) {
            Toast.makeText(this, "Please enter a quantity!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse the quantity and calculate the total amount
        int quantity = Integer.parseInt(quantityText);
        double totalAmount = foodPrice * quantity;

        // Update the total amount display
        totalAmountTextView.setText("Total Amount: Rs" + String.format("%.2f", totalAmount));

        // Save the order details to Firebase
        saveOrderToFirebase(foodNameTextView.getText().toString(), quantity, totalAmount);
    }

    // Method to save the order details to Firebase
    private void saveOrderToFirebase(String foodName, int quantity, double totalAmount) {
        // Create an Order object
        Order order = new Order(foodName, quantity, totalAmount);

        // Push the order data to Firebase under the "Orders" node
        orderId = databaseReference.push().getKey(); // Save the unique ID
        if (orderId != null) {
            databaseReference.child(orderId).setValue(order)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(OrderViewActivity.this, "Order confirmed!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OrderViewActivity.this, "Failed to confirm order!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Method to cancel the order
    private void cancelOrder() {
        if (orderId != null) {
            databaseReference.child(orderId).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(OrderViewActivity.this, "Order cancelled!", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity
                        } else {
                            Toast.makeText(OrderViewActivity.this, "Failed to cancel order!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(OrderViewActivity.this, "No order to cancel!", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to view orders from Firebase
    private void viewOrders() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // List to store the fetched orders
                List<Order> orders = new ArrayList<>();

                // Loop through the data and add each order to the list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        orders.add(order);
                    }
                }

                // Display the orders in a toast message for now (you can update UI to show in a list)
                if (orders.isEmpty()) {
                    Toast.makeText(OrderViewActivity.this, "No orders found!", Toast.LENGTH_SHORT).show();
                } else {
                    StringBuilder ordersString = new StringBuilder();
                    for (Order order : orders) {
                        ordersString.append("Food: ").append(order.foodName)
                                .append("\nQuantity: ").append(order.quantity)
                                .append("\nTotal Amount: Rs ").append(order.totalAmount)
                                .append("\n\n");
                    }
                    // Show orders in a Toast (or you can update a RecyclerView for better UI)
                    Toast.makeText(OrderViewActivity.this, ordersString.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OrderViewActivity.this, "Failed to retrieve orders: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Order class to represent the order details
    public static class Order {
        public String foodName;
        public int quantity;
        public double totalAmount;

        public Order() {
            // Default constructor required for Firebase
        }

        public Order(String foodName, int quantity, double totalAmount) {
            this.foodName = foodName;
            this.quantity = quantity;
            this.totalAmount = totalAmount;
        }
    }
}
