package com.example.eatifyapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {



    private Button viewOrderButton;
    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders");

        // Initialize the view order button
        viewOrderButton = findViewById(R.id.viewOrderButton);

        // Initialize the RecyclerView
        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the order list and adapter
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList);
        orderRecyclerView.setAdapter(orderAdapter);

        // Set click listener for the view order button
        viewOrderButton.setOnClickListener(v -> {
            // Call method to fetch order details and display them
            fetchOrdersFromFirebase();
        });
    }

    private void fetchOrdersFromFirebase() {
        // Show RecyclerView and hide Button
        orderRecyclerView.setVisibility(View.VISIBLE);
        viewOrderButton.setVisibility(View.GONE);

        // Retrieve data from Firebase Database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear existing order list to avoid duplicates
                orderList.clear();

                // Loop through the data and add each order to the list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }

                // Notify the adapter to update the RecyclerView
                orderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OrderListActivity.this, "Failed to load orders: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
