package com.example.inventoryapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;
import android.content.DialogInterface;
import android.widget.LinearLayout;

public class ProductListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private ListView productListView;
    private SimpleCursorAdapter productAdapter;
    private TextView btnAvailableProducts;
    private TextView btnSoldProducts;
    private LinearLayout buttonContainer;
    private boolean showingAvailableProducts = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        Toolbar toolbar = findViewById(R.id.toolbar_product_list);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        dbHelper = new DatabaseHelper(this);
        productListView = findViewById(R.id.product_list_view);
        btnAvailableProducts = findViewById(R.id.btnAvailableProducts);
        btnSoldProducts = findViewById(R.id.btnSoldProducts);
        buttonContainer = findViewById(R.id.buttonContainer);

        // Set up button click listeners
        btnAvailableProducts.setOnClickListener(v -> showAvailableProducts());
        btnSoldProducts.setOnClickListener(v -> showSoldProducts());

        // Show available products by default
        showAvailableProducts();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // Refresh the product list if a sale was completed
        if (resultCode == RESULT_OK) {
            // Always refresh both lists when a sale is completed
            loadAvailableProducts();
            loadSoldProducts();
            
            // Then show the current list based on what's currently selected
            if (showingAvailableProducts) {
                loadAvailableProducts();
            } else {
                loadSoldProducts();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Refresh the current list when activity resumes
        if (showingAvailableProducts) {
            loadAvailableProducts();
        } else {
            loadSoldProducts();
        }
    }

    private void showAvailableProducts() {
        showingAvailableProducts = true;
        updateButtonStyles();
        loadAvailableProducts();
        // Change button container background color
        buttonContainer.setBackgroundColor(0xFFBFE7DE);
    }

    private void showSoldProducts() {
        showingAvailableProducts = false;
        updateButtonStyles();
        loadSoldProducts();
        // Change button container background color
        buttonContainer.setBackgroundColor(0xFFBFE7DE);
    }

    private void updateButtonStyles() {
        if (showingAvailableProducts) {
            // Available Products button - Active (Dark Green)
            btnAvailableProducts.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF013137));
            btnAvailableProducts.setTextColor(0xFFFFFFFF);
            btnAvailableProducts.setElevation(8f);
            
            // Sold Products button - Inactive (White)
            btnSoldProducts.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFFFFF));
            btnSoldProducts.setTextColor(0xFF013137);
            btnSoldProducts.setElevation(2f);
        } else {
            // Available Products button - Inactive (White)
            btnAvailableProducts.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFFFFF));
            btnAvailableProducts.setTextColor(0xFF013137);
            btnAvailableProducts.setElevation(2f);
            
            // Sold Products button - Active (Dark Green)
            btnSoldProducts.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF013137));
            btnSoldProducts.setTextColor(0xFFFFFFFF);
            btnSoldProducts.setElevation(8f);
        }
    }

    private void loadAvailableProducts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_PRODUCT_NAME + " NOT LIKE '%(Sale)%'";
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS,
                null, selection, null, null, null, null);

        setupProductList(cursor);
    }

    private void loadSoldProducts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COLUMN_PRODUCT_NAME + " LIKE '%(Sale)%'";
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS,
                null, selection, null, null, null, 
                DatabaseHelper.COLUMN_ID + " DESC");

        setupProductList(cursor);
    }

    private void setupProductList(Cursor cursor) {
        String[] fromColumns = {
                DatabaseHelper.COLUMN_PRODUCT_NAME,
                DatabaseHelper.COLUMN_PRODUCT_PRICE,
                DatabaseHelper.COLUMN_PRODUCT_QUANTITY,
                DatabaseHelper.COLUMN_PRODUCT_DATE
        };

        int[] toViews = {
                R.id.tvProductName,
                R.id.tvPrice,
                R.id.tvQuantity,
                R.id.tvDate
        };

        productAdapter = new SimpleCursorAdapter(
                this,
                R.layout.list_item_product,
                cursor,
                fromColumns,
                toViews,
                0
        ) {
            @Override
            public void bindView(View view, android.content.Context context, Cursor cursor) {
                super.bindView(view, context, cursor);
                
                // Display the full price string with currency symbols
                TextView priceView = view.findViewById(R.id.tvPrice);
                String price = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE));
                // Capitalize first letter of price
                if (!price.isEmpty()) {
                    price = price.substring(0, 1).toUpperCase() + price.substring(1);
                }
                priceView.setText(price);
                
                // Display product name (remove "(Sale)" suffix for display)
                TextView nameView = view.findViewById(R.id.tvProductName);
                String productName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME));
                if (productName.contains("(Sale)")) {
                    productName = productName.replace("(Sale)", "").trim();
                }
                // Capitalize first letter of product name
                if (!productName.isEmpty()) {
                    productName = productName.substring(0, 1).toUpperCase() + productName.substring(1);
                }
                nameView.setText(productName);
                
                // Display quantity as string
                TextView quantityView = view.findViewById(R.id.tvQuantity);
                TextView billView = view.findViewById(R.id.tvBill);
                String quantity = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_QUANTITY));
                
                // Get the bill container to hide/show the entire line
                View billContainer = view.findViewById(R.id.billContainer);
                
                // If showing sold products, extract and display only sold quantity and bill
                if (!showingAvailableProducts && quantity.contains("Sold:")) {
                    // Extract sold quantity and bill information
                    String[] parts = quantity.split("Sold:");
                    if (parts.length > 1) {
                        String soldInfo = parts[1].trim();
                        // Split by "Bill:" to separate quantity and bill
                        String[] soldParts = soldInfo.split("Bill:");
                        if (soldParts.length > 1) {
                            String soldQuantity = soldParts[0].trim();
                            String billAmount = soldParts[1].trim();
                            
                            // Remove "-" and "()" symbols and capitalize first letter
                            soldQuantity = soldQuantity.replace("-", "").replace("(", "").replace(")", "").trim();
                            billAmount = billAmount.replace("-", "").replace("(", "").replace(")", "").trim();
                            
                            // Capitalize first letter
                            if (!soldQuantity.isEmpty()) {
                                soldQuantity = soldQuantity.substring(0, 1).toUpperCase() + soldQuantity.substring(1);
                            }
                            if (!billAmount.isEmpty()) {
                                billAmount = billAmount.substring(0, 1).toUpperCase() + billAmount.substring(1);
                            }
                            
                            quantityView.setText(soldQuantity);
                            billView.setText(billAmount);
                            billContainer.setVisibility(View.VISIBLE);
                        } else {
                            // Only sold quantity, no bill
                            String soldQuantity = soldInfo.replace("-", "").replace("(", "").replace(")", "").trim();
                            if (!soldQuantity.isEmpty()) {
                                soldQuantity = soldQuantity.substring(0, 1).toUpperCase() + soldQuantity.substring(1);
                            }
                            quantityView.setText(soldQuantity);
                            billContainer.setVisibility(View.GONE);
                        }
                    } else {
                        // Capitalize first letter for sold products without bill
                        if (!quantity.isEmpty()) {
                            quantity = quantity.substring(0, 1).toUpperCase() + quantity.substring(1);
                        }
                        quantityView.setText(quantity);
                        billContainer.setVisibility(View.GONE);
                    }
                } else {
                    // Capitalize first letter for available products
                    if (!quantity.isEmpty()) {
                        quantity = quantity.substring(0, 1).toUpperCase() + quantity.substring(1);
                    }
                    quantityView.setText(quantity);
                    billContainer.setVisibility(View.GONE);
                }
                
                // Format date (keep as is since it's already formatted)
                TextView dateView = view.findViewById(R.id.tvDate);
                String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_DATE));
                // Capitalize first letter of date
                if (!date.isEmpty()) {
                    date = date.substring(0, 1).toUpperCase() + date.substring(1);
                }
                dateView.setText(date);
                
                // Set up delete button click listener
                TextView deleteButton = view.findViewById(R.id.btnDelete);
                long productId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(productId));
            }
        };

        productListView.setAdapter(productAdapter);
    }

    private void showDeleteConfirmationDialog(long productId) {
        // Create custom dialog layout
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(0xFF013137);
        
        // Add title
        android.widget.TextView titleView = new android.widget.TextView(this);
        titleView.setText("Delete Product");
        titleView.setTextColor(0xFFFFFFFF);
        titleView.setTextSize(18);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setPadding(0, 0, 0, 16);
        layout.addView(titleView);
        
        // Add message
        android.widget.TextView messageView = new android.widget.TextView(this);
        messageView.setText("Are you sure you want to delete this product?");
        messageView.setTextColor(0xFFFFFFFF);
        messageView.setTextSize(16);
        messageView.setPadding(0, 0, 0, 32);
        layout.addView(messageView);
        
        // Create button container
        android.widget.LinearLayout buttonContainer = new android.widget.LinearLayout(this);
        buttonContainer.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        buttonContainer.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        
        // Create Cancel button
        android.widget.Button cancelButton = new android.widget.Button(this);
        cancelButton.setText("Cancel");
        cancelButton.setBackgroundColor(0xFFBFE7DE);
        cancelButton.setTextColor(0xFF013137);
        cancelButton.setPadding(32, 16, 32, 16);
        cancelButton.setBackgroundResource(R.drawable.rounded_button_background);
        android.widget.LinearLayout.LayoutParams cancelParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cancelParams.weight = 1;
        cancelParams.setMargins(0, 0, 16, 0);
        cancelButton.setLayoutParams(cancelParams);
        
        // Create Delete button
        android.widget.Button deleteButton = new android.widget.Button(this);
        deleteButton.setText("Delete");
        deleteButton.setBackgroundColor(0xFFBFE7DE);
        deleteButton.setTextColor(0xFF013137);
        deleteButton.setPadding(32, 16, 32, 16);
        deleteButton.setBackgroundResource(R.drawable.rounded_button_background);
        android.widget.LinearLayout.LayoutParams deleteParams = new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        );
        deleteParams.weight = 1;
        deleteParams.setMargins(16, 0, 0, 0);
        deleteButton.setLayoutParams(deleteParams);
        
        // Add buttons to container
        buttonContainer.addView(cancelButton);
        buttonContainer.addView(deleteButton);
        layout.addView(buttonContainer);
        
        // Create and show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        
        // Set button click listeners
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        deleteButton.setOnClickListener(v -> {
            dialog.dismiss();
            deleteProduct(productId);
        });
    }

    private void deleteProduct(long productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(productId)};
        
        int deletedRows = db.delete(DatabaseHelper.TABLE_PRODUCTS, selection, selectionArgs);
        
        if (deletedRows > 0) {
            // Refresh the current list
            if (showingAvailableProducts) {
                loadAvailableProducts();
            } else {
                loadSoldProducts();
            }
            // Show success message
            android.widget.Toast.makeText(this, "Product deleted successfully", android.widget.Toast.LENGTH_SHORT).show();
        } else {
            android.widget.Toast.makeText(this, "Error deleting product", android.widget.Toast.LENGTH_SHORT).show();
        }
    }
}