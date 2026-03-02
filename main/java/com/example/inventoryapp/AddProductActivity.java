package com.example.inventoryapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.Calendar;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.database.Cursor;

public class AddProductActivity extends AppCompatActivity {

    EditText etProductName, etPrice, etQuantity, etDate;
    Button btnSaveProduct;
    DatabaseHelper dbHelper;
    private LinearLayout multipleProductsContainer;
    private ScrollView scrollView;
    private TextView btnSingleProduct, btnMultipleProducts;
    private LinearLayout singleProductSection, multipleProductsSection, buttonContainer;
    private boolean showingSingleProduct = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar_add_product);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        etProductName = findViewById(R.id.etProductName);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etDate = findViewById(R.id.etDate);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);
        multipleProductsContainer = findViewById(R.id.multipleProductsContainer);
        scrollView = findViewById(R.id.scrollView);
        
        btnSingleProduct = findViewById(R.id.btnSingleProduct);
        btnMultipleProducts = findViewById(R.id.btnMultipleProducts);
        singleProductSection = findViewById(R.id.singleProductSection);
        multipleProductsSection = findViewById(R.id.multipleProductsSection);
        buttonContainer = findViewById(R.id.buttonContainer);

        btnSingleProduct.setOnClickListener(v -> showSingleProduct());
        btnMultipleProducts.setOnClickListener(v -> showMultipleProducts());

        etDate.setOnClickListener(v -> showDatePicker());

        // Set up text change listener for auto-fill price
        etProductName.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (!s.toString().trim().isEmpty()) {
                    autoFillProductPrice(s.toString().trim());
                }
            }
        });

        btnSaveProduct.setOnClickListener(v -> saveProduct());

        showSingleProduct();
    }

    private void showSingleProduct() {
        showingSingleProduct = true;
        updateButtonStyles();
        singleProductSection.setVisibility(View.VISIBLE);
        multipleProductsSection.setVisibility(View.GONE);
        buttonContainer.setBackgroundColor(0xFFBFE7DE);
    }

    private void showMultipleProducts() {
        showingSingleProduct = false;
        updateButtonStyles();
        singleProductSection.setVisibility(View.GONE);
        multipleProductsSection.setVisibility(View.VISIBLE);
        buttonContainer.setBackgroundColor(0xFFBFE7DE);
        
        if (multipleProductsContainer.getChildCount() == 0) {
            addProductRow();
        }
    }

    private void updateButtonStyles() {
        if (showingSingleProduct) {
            btnSingleProduct.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF013137));
            btnSingleProduct.setTextColor(0xFFFFFFFF);
            btnSingleProduct.setElevation(8f);
            
            btnMultipleProducts.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFFFFF));
            btnMultipleProducts.setTextColor(0xFF013137);
            btnMultipleProducts.setElevation(2f);
        } else {
            btnSingleProduct.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFFFFF));
            btnSingleProduct.setTextColor(0xFF013137);
            btnSingleProduct.setElevation(2f);
            
            btnMultipleProducts.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF013137));
            btnMultipleProducts.setTextColor(0xFFFFFFFF);
            btnMultipleProducts.setElevation(8f);
        }
    }

    private void addProductRow() {
        View productRow = getLayoutInflater().inflate(R.layout.multiple_product_row, multipleProductsContainer, false);
        
        EditText etName = productRow.findViewById(R.id.etProductName);
        EditText etPrice = productRow.findViewById(R.id.etPrice);
        EditText etQuantity = productRow.findViewById(R.id.etQuantity);
        EditText etDate = productRow.findViewById(R.id.etDate);
        Button btnRemove = productRow.findViewById(R.id.btnRemove);
        
        // Set up date picker for this row
        etDate.setOnClickListener(v -> showDatePickerForRow(etDate));
        
        // Set up text change listener for auto-fill price
        etName.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String text = s.toString();
                if (!text.isEmpty()) {
                    // Auto-fill price
                    autoFillProductPriceForRow(text.trim(), etPrice);
                }
            }
        });
        
        // Set input type to automatically capitalize words
        etName.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        
        btnRemove.setOnClickListener(v -> {
            multipleProductsContainer.removeView(productRow);
        });
        
        // Remove existing buttons if they exist
        View btnAddMore = multipleProductsContainer.findViewById(1001);
        View btnSaveAll = multipleProductsContainer.findViewById(1002);
        if (btnAddMore != null) {
            multipleProductsContainer.removeView(btnAddMore);
        }
        if (btnSaveAll != null) {
            multipleProductsContainer.removeView(btnSaveAll);
        }
        
        // Add the product row first
        multipleProductsContainer.addView(productRow);
        
        // Add "Add More" button at the bottom
        Button btnAddMoreNew = new Button(this);
        btnAddMoreNew.setId(1001);
        btnAddMoreNew.setText("Add Another Product");
        btnAddMoreNew.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF013137));
        btnAddMoreNew.setTextColor(0xFFFFFFFF);
        btnAddMoreNew.setPadding(16, 12, 16, 12);
        btnAddMoreNew.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        btnAddMoreNew.setBackgroundResource(R.drawable.rounded_button_background);
        btnAddMoreNew.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF013137));
        btnAddMoreNew.setOnClickListener(v -> addProductRow());
        
        // Add "Save All" button at the bottom
        Button btnSaveAllNew = new Button(this);
        btnSaveAllNew.setId(1002);
        btnSaveAllNew.setText("Save All Products");
        btnSaveAllNew.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF013137));
        btnSaveAllNew.setTextColor(0xFFFFFFFF);
        btnSaveAllNew.setPadding(16, 12, 16, 12);
        btnSaveAllNew.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        // Add top margin to create vertical gap
        ((LinearLayout.LayoutParams) btnSaveAllNew.getLayoutParams()).setMargins(0, 16, 0, 0);
        btnSaveAllNew.setBackgroundResource(R.drawable.rounded_button_background);
        btnSaveAllNew.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF013137));
        btnSaveAllNew.setOnClickListener(v -> saveMultipleProducts());
        
        // Add buttons at the bottom
        multipleProductsContainer.addView(btnAddMoreNew);
        multipleProductsContainer.addView(btnSaveAllNew);
    }

    private void showDatePickerForRow(EditText dateField) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                dateField.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void saveMultipleProducts() {
        if (multipleProductsContainer.getChildCount() == 0) {
            Toast.makeText(this, "Please add at least one product", Toast.LENGTH_SHORT).show();
            return;
        }

        int successCount = 0;
        int totalCount = 0;

        for (int i = 0; i < multipleProductsContainer.getChildCount(); i++) {
            View productRow = multipleProductsContainer.getChildAt(i);
            
            // Skip buttons (check by ID)
            if (productRow.getId() == 1001 || productRow.getId() == 1002) {
                continue;
            }
            
            EditText etName = productRow.findViewById(R.id.etProductName);
            EditText etPrice = productRow.findViewById(R.id.etPrice);
            EditText etQuantity = productRow.findViewById(R.id.etQuantity);
            EditText etDate = productRow.findViewById(R.id.etDate);
            
            String name = etName.getText().toString().trim();
            String price = etPrice.getText().toString().trim();
            String quantity = etQuantity.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            if (!name.isEmpty() && !price.isEmpty() && !quantity.isEmpty() && !date.isEmpty()) {
                totalCount++;
                // Capitalize the product name before saving
                String capitalizedName = capitalizeWords(name);
                if (saveSingleProduct(capitalizedName, price, quantity, date)) {
                    successCount++;
                }
            }
        }

        String message = "Saved " + successCount + " out of " + totalCount + " products";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        
        // Set result to notify calling activity to refresh product list
        setResult(RESULT_OK);
        
        // Clear multiple products container
        multipleProductsContainer.removeAllViews();
        showSingleProduct();
    }

    private boolean saveSingleProduct(String name, String price, String quantity, String date) {
        try {
            // Capitalize the product name
            String capitalizedName = capitalizeWords(name);
            
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            
            String[] columns = {DatabaseHelper.COLUMN_PRODUCT_QUANTITY, DatabaseHelper.COLUMN_PRODUCT_PRICE};
            String selection = DatabaseHelper.COLUMN_PRODUCT_NAME + " COLLATE NOCASE = ? AND " + 
                             DatabaseHelper.COLUMN_PRODUCT_NAME + " NOT LIKE '%(Sale)%'";
            String[] selectionArgs = {capitalizedName};
            android.database.Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, columns, selection, selectionArgs, null, null, null);

            if (cursor.moveToFirst()) {
                String existingQuantity = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_QUANTITY));
                
                String cleanExistingQuantity = existingQuantity.replaceAll("[^0-9.]", "");
                String cleanNewQuantity = quantity.replaceAll("[^0-9.]", "");
                
                double existingQuantityValue = cleanExistingQuantity.isEmpty() ? 0.0 : Double.parseDouble(cleanExistingQuantity);
                double newQuantityValue = cleanNewQuantity.isEmpty() ? 0.0 : Double.parseDouble(cleanNewQuantity);
                
                double totalQuantity = existingQuantityValue + newQuantityValue;
                
                String unit = extractUnitFromQuantity(existingQuantity);
                if (unit.isEmpty()) {
                    unit = extractUnitFromQuantity(quantity);
                }
                
                ContentValues updateValues = new ContentValues();
                updateValues.put(DatabaseHelper.COLUMN_PRODUCT_QUANTITY, totalQuantity + " " + unit);
                
                String updateSelection = DatabaseHelper.COLUMN_PRODUCT_NAME + " COLLATE NOCASE = ? AND " + 
                                      DatabaseHelper.COLUMN_PRODUCT_NAME + " NOT LIKE '%(Sale)%'";
                String[] updateSelectionArgs = {capitalizedName};
                
                int updatedRows = db.update(DatabaseHelper.TABLE_PRODUCTS, updateValues, updateSelection, updateSelectionArgs);
                cursor.close();
                return updatedRows > 0;
                
            } else {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_PRODUCT_NAME, capitalizedName);
                values.put(DatabaseHelper.COLUMN_PRODUCT_PRICE, price);
                values.put(DatabaseHelper.COLUMN_PRODUCT_QUANTITY, quantity);
                values.put(DatabaseHelper.COLUMN_PRODUCT_DATE, date);

                long newRowId = db.insert(DatabaseHelper.TABLE_PRODUCTS, null, values);
                cursor.close();
                return newRowId != -1;
            }
            
        } catch (Exception e) {
            return false;
        }
    }

    private void saveProduct() {
        String name = etProductName.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String quantity = etQuantity.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty() || quantity.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            // Capitalize the product name
            String capitalizedName = capitalizeWords(name);
            if (saveSingleProduct(capitalizedName, price, quantity, date)) {
                Toast.makeText(this, "Product Saved", Toast.LENGTH_SHORT).show();
                // Set result to notify calling activity to refresh product list
                setResult(RESULT_OK);
                clearFields();
            } else {
                Toast.makeText(this, "Error saving product", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                etDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private String extractUnitFromQuantity(String quantity) {
        if (quantity != null && !quantity.isEmpty()) {
            String unit = quantity.replaceAll("[0-9.]", "").trim();
            unit = unit.replaceAll("(?i)sold|bill|\\$|€|₹|£|¥", "").trim();
            return unit;
        }
        return "";
    }

    private void clearFields() {
        etProductName.setText("");
        etPrice.setText("");
        etQuantity.setText("");
        etDate.setText("");
        etProductName.requestFocus();
    }

    private String capitalizeWords(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        String[] words = text.split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            if (i > 0) {
                result.append(" ");
            }
            if (!words[i].isEmpty()) {
                result.append(words[i].substring(0, 1).toUpperCase())
                      .append(words[i].substring(1).toLowerCase());
            }
        }
        
        return result.toString();
    }

    private void autoFillProductPrice(String productName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_PRODUCT_PRICE};
        String selection = DatabaseHelper.COLUMN_PRODUCT_NAME + " COLLATE NOCASE = ? AND " + 
                          DatabaseHelper.COLUMN_PRODUCT_NAME + " NOT LIKE '%(Sale)%'";
        String[] selectionArgs = {productName};
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String productPrice = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE));
            etPrice.setText(productPrice);
        } else {
            // Product not found, clear price field
            etPrice.setText("");
        }
        cursor.close();
    }

    private void autoFillProductPriceForRow(String productName, EditText priceField) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_PRODUCT_PRICE};
        String selection = DatabaseHelper.COLUMN_PRODUCT_NAME + " COLLATE NOCASE = ? AND " + 
                          DatabaseHelper.COLUMN_PRODUCT_NAME + " NOT LIKE '%(Sale)%'";
        String[] selectionArgs = {productName};
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String productPrice = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE));
            priceField.setText(productPrice);
        } else {
            // Product not found, clear price field
            priceField.setText("");
        }
        cursor.close();
    }
}