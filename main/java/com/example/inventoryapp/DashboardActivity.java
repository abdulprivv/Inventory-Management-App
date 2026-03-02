package com.example.inventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DashboardActivity extends AppCompatActivity {

    CardView productListCard, addProductCard2, saleProductCard, vendorListCard, addVendorCard;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard); // make sure XML file name matches

        // Do NOT call setSupportActionBar here!

        // Initialize views
        productListCard = findViewById(R.id.productListCard);
        addProductCard2 = findViewById(R.id.addProductCard2);
        saleProductCard = findViewById(R.id.saleProductCard);
        vendorListCard = findViewById(R.id.vendorListCard);
        addVendorCard = findViewById(R.id.addVendorCard);
        btnLogout = findViewById(R.id.btnLogout);

        // Navigation
        productListCard.setOnClickListener(v ->
                startActivity(new Intent(this, ProductListActivity.class)));
        addProductCard2.setOnClickListener(v ->
                startActivity(new Intent(this, AddProductActivity.class)));
        saleProductCard.setOnClickListener(v ->
                startActivity(new Intent(this, SaleProductActivity.class)));
        vendorListCard.setOnClickListener(v ->
                startActivity(new Intent(this, VendorListActivity.class)));
        addVendorCard.setOnClickListener(v ->
                startActivity(new Intent(this, AddVendorActivity.class)));

        // Logout
        btnLogout.setOnClickListener(v -> {
            // Clear login session
            MainActivity.logout(this);
            
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
