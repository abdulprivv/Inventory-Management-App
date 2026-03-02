package com.example.inventoryapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.Calendar;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class SaleProductActivity extends AppCompatActivity {

    EditText etProductName, etPrice, etQuantity, etDate;
    TextView tvBillAmount;
    Button btnSaleProduct;
    DatabaseHelper dbHelper;
    private LinearLayout multipleSalesContainer;
    private ScrollView scrollView;
    private TextView btnSingleSale, btnMultipleSales;
    private LinearLayout singleSaleSection, multipleSalesSection, buttonContainer;
    private boolean showingSingleSale = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_product);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar_sale_product);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        etProductName = findViewById(R.id.etProductName);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etDate = findViewById(R.id.etDate);
        tvBillAmount = findViewById(R.id.tvBillAmount);
        btnSaleProduct = findViewById(R.id.btnSaleProduct);
        multipleSalesContainer = findViewById(R.id.multipleSalesContainer);
        scrollView = findViewById(R.id.scrollView);
        
        btnSingleSale = findViewById(R.id.btnSingleSale);
        btnMultipleSales = findViewById(R.id.btnMultipleSales);
        singleSaleSection = findViewById(R.id.singleSaleSection);
        multipleSalesSection = findViewById(R.id.multipleSalesSection);
        buttonContainer = findViewById(R.id.buttonContainer);

        btnSingleSale.setOnClickListener(v -> showSingleSale());
        btnMultipleSales.setOnClickListener(v -> showMultipleSales());

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

        etPrice.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                calculateBill();
            }
        });

        etQuantity.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                calculateBill();
            }
        });

        etDate.setOnClickListener(v -> showDatePicker());

        btnSaleProduct.setOnClickListener(v -> saleProduct());

        showSingleSale();
    }

    private void showSingleSale() {
        showingSingleSale = true;
        updateButtonStyles();
        singleSaleSection.setVisibility(View.VISIBLE);
        multipleSalesSection.setVisibility(View.GONE);
        buttonContainer.setBackgroundColor(0xFFBFE7DE);
    }

    private void showMultipleSales() {
        showingSingleSale = false;
        updateButtonStyles();
        singleSaleSection.setVisibility(View.GONE);
        multipleSalesSection.setVisibility(View.VISIBLE);
        buttonContainer.setBackgroundColor(0xFFBFE7DE);
        
        if (multipleSalesContainer.getChildCount() == 0) {
            addSaleRow();
        }
    }

    private void updateButtonStyles() {
        if (showingSingleSale) {
            btnSingleSale.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF013137));
            btnSingleSale.setTextColor(0xFFFFFFFF);
            btnSingleSale.setElevation(8f);
            
            btnMultipleSales.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFFFFF));
            btnMultipleSales.setTextColor(0xFF013137);
            btnMultipleSales.setElevation(2f);
        } else {
            btnSingleSale.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFFFFF));
            btnSingleSale.setTextColor(0xFF013137);
            btnSingleSale.setElevation(2f);
            
            btnMultipleSales.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF013137));
            btnMultipleSales.setTextColor(0xFFFFFFFF);
            btnMultipleSales.setElevation(8f);
        }
    }

    private void addSaleRow() {
        View saleRow = getLayoutInflater().inflate(R.layout.multiple_sale_row, multipleSalesContainer, false);
        
        EditText etName = saleRow.findViewById(R.id.etProductName);
        EditText etPrice = saleRow.findViewById(R.id.etPrice);
        EditText etQuantity = saleRow.findViewById(R.id.etQuantity);
        EditText etDate = saleRow.findViewById(R.id.etDate);
        TextView tvBill = saleRow.findViewById(R.id.tvBill);
        Button btnRemove = saleRow.findViewById(R.id.btnRemove);
        
        etDate.setOnClickListener(v -> showDatePickerForRow(etDate));
        
        etName.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String text = s.toString();
                if (!text.isEmpty()) {
                    // Auto-fill price and quantity
                    autoFillProductPriceForRow(text.trim(), etPrice, etQuantity);
                }
            }
        });

        // Set input type to automatically capitalize words
        etName.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        etPrice.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                calculateBillForRow(etPrice, etQuantity, tvBill);
                updateTotalBill();
            }
        });

        etQuantity.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                calculateBillForRow(etPrice, etQuantity, tvBill);
                updateTotalBill();
            }
        });
        
        btnRemove.setOnClickListener(v -> {
            multipleSalesContainer.removeView(saleRow);
            updateTotalBill();
        });
        
        View btnAddMore = multipleSalesContainer.findViewById(2001);
        View btnProcessAll = multipleSalesContainer.findViewById(2002);
        View totalBillView = multipleSalesContainer.findViewById(2003);
        if (btnAddMore != null) {
            multipleSalesContainer.removeView(btnAddMore);
        }
        if (btnProcessAll != null) {
            multipleSalesContainer.removeView(btnProcessAll);
        }
        if (totalBillView != null) {
            multipleSalesContainer.removeView(totalBillView);
        }
        
        multipleSalesContainer.addView(saleRow);
        
        Button btnAddMoreNew = new Button(this);
        btnAddMoreNew.setId(2001);
        btnAddMoreNew.setText("Add Another Sale");
        btnAddMoreNew.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF013137));
        btnAddMoreNew.setTextColor(0xFFFFFFFF);
        btnAddMoreNew.setPadding(16, 12, 16, 12);
        btnAddMoreNew.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        btnAddMoreNew.setBackgroundResource(R.drawable.rounded_button_background);
        btnAddMoreNew.setOnClickListener(v -> addSaleRow());
        
        TextView totalBillTextView = new TextView(this);
        totalBillTextView.setId(2003);
        totalBillTextView.setText("Total Bill: $0.00");
        totalBillTextView.setTextColor(0xFF013137);
        totalBillTextView.setTextSize(18);
        totalBillTextView.setTypeface(null, android.graphics.Typeface.BOLD);
        totalBillTextView.setBackgroundColor(0xFFBFE7DE);
        totalBillTextView.setPadding(16, 12, 16, 12);
        totalBillTextView.setGravity(android.view.Gravity.CENTER);
        totalBillTextView.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        
        Button btnProcessAllNew = new Button(this);
        btnProcessAllNew.setId(2002);
        btnProcessAllNew.setText("Process All Sales");
        btnProcessAllNew.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF013137));
        btnProcessAllNew.setTextColor(0xFFFFFFFF);
        btnProcessAllNew.setPadding(16, 12, 16, 12);
        btnProcessAllNew.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        btnProcessAllNew.setBackgroundResource(R.drawable.rounded_button_background);
        btnProcessAllNew.setOnClickListener(v -> processMultipleSales());
        
        multipleSalesContainer.addView(btnAddMoreNew);
        multipleSalesContainer.addView(totalBillTextView);
        multipleSalesContainer.addView(btnProcessAllNew);
        
        updateTotalBill();
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

    private void autoFillProductPriceForRow(String productName, EditText etPrice, EditText etQuantity) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_PRODUCT_PRICE, DatabaseHelper.COLUMN_PRODUCT_QUANTITY};
        String selection = DatabaseHelper.COLUMN_PRODUCT_NAME + " COLLATE NOCASE = ?";
        String[] selectionArgs = {productName};
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String productPrice = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE));
            String productQuantity = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_QUANTITY));
            
            etPrice.setText(productPrice);
            
            String unit = extractUnitFromQuantity(productQuantity);
            if (!unit.isEmpty()) {
                etQuantity.setHint("Enter quantity (" + unit + ")");
            }
        }
        cursor.close();
    }

    private void calculateBillForRow(EditText etPrice, EditText etQuantity, TextView tvBill) {
        String price = etPrice.getText().toString().trim();
        String quantity = etQuantity.getText().toString().trim();

        if (!price.isEmpty() && !quantity.isEmpty()) {
            try {
                String cleanPrice = price.replaceAll("[^0-9.]", "");
                String cleanQuantity = quantity.replaceAll("[^0-9.]", "");
                
                double priceValue = cleanPrice.isEmpty() ? 0.0 : Double.parseDouble(cleanPrice);
                double quantityValue = cleanQuantity.isEmpty() ? 0.0 : Double.parseDouble(cleanQuantity);
                double totalBill = priceValue * quantityValue;
                
                String currencySymbol = extractCurrencySymbol(price);
                tvBill.setText("Bill: " + currencySymbol + String.format("%.2f", totalBill));
            } catch (NumberFormatException e) {
                tvBill.setText("Bill: $0.00");
            }
        } else {
            tvBill.setText("Bill: $0.00");
        }
    }

    private void processMultipleSales() {
        if (multipleSalesContainer.getChildCount() == 0) {
            Toast.makeText(this, "Please add at least one sale", Toast.LENGTH_SHORT).show();
            return;
        }

        int successCount = 0;
        int totalCount = 0;

        for (int i = 0; i < multipleSalesContainer.getChildCount(); i++) {
            View saleRow = multipleSalesContainer.getChildAt(i);
            
            if (saleRow.getId() == 2001 || saleRow.getId() == 2002 || saleRow.getId() == 2003) {
                continue;
            }
            
            EditText etName = saleRow.findViewById(R.id.etProductName);
            EditText etPrice = saleRow.findViewById(R.id.etPrice);
            EditText etQuantity = saleRow.findViewById(R.id.etQuantity);
            EditText etDate = saleRow.findViewById(R.id.etDate);
            
            String name = etName.getText().toString().trim();
            String price = etPrice.getText().toString().trim();
            String quantity = etQuantity.getText().toString().trim();
            String date = etDate.getText().toString().trim();

            if (!name.isEmpty() && !price.isEmpty() && !quantity.isEmpty() && !date.isEmpty()) {
                totalCount++;
                // Capitalize the product name before processing
                String capitalizedName = capitalizeWords(name);
                if (processSingleSale(capitalizedName, price, quantity, date)) {
                    successCount++;
                }
            }
        }

        String message = "Processed " + successCount + " out of " + totalCount + " sales";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        
        setResult(RESULT_OK);
        
        multipleSalesContainer.removeAllViews();
        showSingleSale();
    }

    private boolean processSingleSale(String productName, String price, String quantityToSell, String date) {
        try {
            // Capitalize the product name
            String capitalizedName = capitalizeWords(productName);
            
            // Check available stock first
            if (!checkAvailableStock(capitalizedName, quantityToSell)) {
                return false;
            }
            
            String cleanPrice = price.replaceAll("[^0-9.]", "");
            double priceValue = Double.parseDouble(cleanPrice);
            
            String cleanQuantity = quantityToSell.replaceAll("[^0-9.]", "");
            double quantityValue = cleanQuantity.isEmpty() ? 1.0 : Double.parseDouble(cleanQuantity);
            double totalBill = priceValue * quantityValue;
            
            String currencySymbol = extractCurrencySymbol(price);
            
            // Process the sale with automatic removal logic
            return processSaleWithRemoval(capitalizedName, quantityToSell, totalBill, currencySymbol, date);
            
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean processSaleWithRemoval(String productName, String quantityToSell, double totalBill, String currencySymbol, String saleDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        String[] columns = {DatabaseHelper.COLUMN_PRODUCT_QUANTITY, DatabaseHelper.COLUMN_PRODUCT_PRICE};
        String selection = DatabaseHelper.COLUMN_PRODUCT_NAME + " COLLATE NOCASE = ? AND " + 
                          DatabaseHelper.COLUMN_PRODUCT_NAME + " NOT LIKE '%(Sale)%'";
        String[] selectionArgs = {productName};
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String currentQuantity = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_QUANTITY));
            String productPrice = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE));
            
            String cleanCurrentQuantity = currentQuantity.replaceAll("[^0-9.]", "");
            String cleanQuantityToSell = quantityToSell.replaceAll("[^0-9.]", "");
            
            double currentQty = cleanCurrentQuantity.isEmpty() ? 0.0 : Double.parseDouble(cleanCurrentQuantity);
            double sellQty = cleanQuantityToSell.isEmpty() ? 0.0 : Double.parseDouble(cleanQuantityToSell);
            double remainingQty = currentQty - sellQty;
            
            String unit = extractUnitFromQuantity(currentQuantity);
            
            // Check if remaining quantity is 0 or less
            if (remainingQty <= 0) {
                // Delete the product from available products (it will still exist in sold products)
                String deleteSelection = DatabaseHelper.COLUMN_PRODUCT_NAME + " COLLATE NOCASE = ? AND " + 
                                       DatabaseHelper.COLUMN_PRODUCT_NAME + " NOT LIKE '%(Sale)%'";
                String[] deleteSelectionArgs = {productName};
                db.delete(DatabaseHelper.TABLE_PRODUCTS, deleteSelection, deleteSelectionArgs);
            } else {
                // Update the original product with remaining quantity
                ContentValues updateValues = new ContentValues();
                updateValues.put(DatabaseHelper.COLUMN_PRODUCT_QUANTITY, remainingQty + " " + unit);
                
                String updateSelection = DatabaseHelper.COLUMN_PRODUCT_NAME + " COLLATE NOCASE = ? AND " + 
                                      DatabaseHelper.COLUMN_PRODUCT_NAME + " NOT LIKE '%(Sale)%'";
                String[] updateSelectionArgs = {productName};
                
                db.update(DatabaseHelper.TABLE_PRODUCTS, updateValues, updateSelection, updateSelectionArgs);
            }
            
            // Create a separate sale record with the user-selected sale date
            ContentValues saleValues = new ContentValues();
            saleValues.put(DatabaseHelper.COLUMN_PRODUCT_NAME, productName + " (Sale)");
            saleValues.put(DatabaseHelper.COLUMN_PRODUCT_PRICE, productPrice);
            saleValues.put(DatabaseHelper.COLUMN_PRODUCT_QUANTITY, "Sold: " + quantityToSell + " Bill: " + currencySymbol + String.format("%.2f", totalBill));
            saleValues.put(DatabaseHelper.COLUMN_PRODUCT_DATE, saleDate);
            
            db.insert(DatabaseHelper.TABLE_PRODUCTS, null, saleValues);
            
            cursor.close();
            return true;
            
        } else {
            cursor.close();
            return false;
        }
    }

    private void calculateBill() {
        String priceStr = etPrice.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();

        String currencySymbol = extractCurrencySymbol(priceStr);
        
        if (!priceStr.isEmpty() && !quantityStr.isEmpty()) {
            try {
                String cleanPrice = priceStr.replaceAll("[^0-9.]", "");
                double price = Double.parseDouble(cleanPrice);
                
                String cleanQuantity = quantityStr.replaceAll("[^0-9.]", "");
                double quantity = cleanQuantity.isEmpty() ? 1.0 : Double.parseDouble(cleanQuantity);
                
                double totalBill = price * quantity;
                tvBillAmount.setText(String.format("Total Bill: %s%.2f", currencySymbol, totalBill));
            } catch (NumberFormatException e) {
                tvBillAmount.setText(String.format("Total Bill: %s0.00", currencySymbol));
            }
        } else if (!priceStr.isEmpty()) {
            tvBillAmount.setText(String.format("Total Bill: %s0.00", currencySymbol));
        } else {
            tvBillAmount.setText("Total Bill: $0.00");
        }
    }

    private String extractCurrencySymbol(String priceStr) {
        for (int i = 0; i < priceStr.length(); i++) {
            char c = priceStr.charAt(i);
            if (!Character.isDigit(c) && c != '.') {
                return String.valueOf(c);
            }
        }
        return "$";
    }

    private void autoFillProductPrice(String productName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_PRODUCT_PRICE, DatabaseHelper.COLUMN_PRODUCT_QUANTITY};
        String selection = DatabaseHelper.COLUMN_PRODUCT_NAME + " COLLATE NOCASE = ?";
        String[] selectionArgs = {productName};
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String productPrice = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE));
            String productQuantity = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_QUANTITY));
            
            etPrice.setText(productPrice);
            
            String unit = extractUnitFromQuantity(productQuantity);
            if (!unit.isEmpty()) {
                etQuantity.setHint("Enter quantity (" + unit + ")");
            }
            
            calculateBill();
        } else {
            etPrice.setText("");
            etQuantity.setHint("Enter quantity");
            String currencySymbol = extractCurrencySymbol(etPrice.getText().toString());
            tvBillAmount.setText("Total Bill: " + currencySymbol + "0.00");
        }
        cursor.close();
    }

    private String extractUnitFromQuantity(String quantity) {
        if (quantity != null && !quantity.isEmpty()) {
            String unit = quantity.replaceAll("[0-9.]", "").trim();
            unit = unit.replaceAll("(?i)sold|bill|\\$|€|₹|£|¥", "").trim();
            return unit;
        }
        return "";
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

    private void saleProduct() {
        String name = etProductName.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String quantityToSell = etQuantity.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty() || quantityToSell.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            try {
                // Capitalize the product name
                String capitalizedName = capitalizeWords(name);
                
                if (!checkAvailableStock(capitalizedName, quantityToSell)) {
                    return;
                }
                
                String cleanPrice = price.replaceAll("[^0-9.]", "");
                double priceValue = Double.parseDouble(cleanPrice);
                
                String cleanQuantity = quantityToSell.replaceAll("[^0-9.]", "");
                double quantityValue = cleanQuantity.isEmpty() ? 1.0 : Double.parseDouble(cleanQuantity);
                double totalBill = priceValue * quantityValue;
                
                String currencySymbol = extractCurrencySymbol(price);
                
                // Process the sale directly without confirmation
                updateProductQuantity(capitalizedName, quantityToSell, totalBill, currencySymbol, date);
                clearFields();
                
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkAvailableStock(String productName, String quantityToSell) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_PRODUCT_QUANTITY};
        String selection = DatabaseHelper.COLUMN_PRODUCT_NAME + " COLLATE NOCASE = ? AND " + 
                          DatabaseHelper.COLUMN_PRODUCT_NAME + " NOT LIKE '%(Sale)%'";
        String[] selectionArgs = {productName};
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String availableQuantity = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_QUANTITY));
            
            String cleanAvailableQuantity = availableQuantity.replaceAll("[^0-9.]", "");
            String cleanQuantityToSell = quantityToSell.replaceAll("[^0-9.]", "");
            
            if (!cleanAvailableQuantity.isEmpty() && !cleanQuantityToSell.isEmpty()) {
                double availableQty = Double.parseDouble(cleanAvailableQuantity);
                double sellQty = Double.parseDouble(cleanQuantityToSell);
                
                if (sellQty > availableQty) {
                    Toast.makeText(this, "Insufficient stock! Available: " + availableQuantity + ", Requested: " + quantityToSell, Toast.LENGTH_LONG).show();
                    cursor.close();
                    return false;
                }
            }
        } else {
            Toast.makeText(this, "Product not found in inventory", Toast.LENGTH_SHORT).show();
            cursor.close();
            return false;
        }
        
        cursor.close();
        return true;
    }

    private void updateProductQuantity(String productName, String quantityToSell, double totalBill, String currencySymbol, String saleDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        String[] columns = {DatabaseHelper.COLUMN_PRODUCT_QUANTITY, DatabaseHelper.COLUMN_PRODUCT_PRICE};
        String selection = DatabaseHelper.COLUMN_PRODUCT_NAME + " COLLATE NOCASE = ? AND " + 
                          DatabaseHelper.COLUMN_PRODUCT_NAME + " NOT LIKE '%(Sale)%'";
        String[] selectionArgs = {productName};
        Cursor cursor = db.query(DatabaseHelper.TABLE_PRODUCTS, columns, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            String currentQuantity = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_QUANTITY));
            String productPrice = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE));
            
            String cleanCurrentQuantity = currentQuantity.replaceAll("[^0-9.]", "");
            String cleanQuantityToSell = quantityToSell.replaceAll("[^0-9.]", "");
            
            double currentQty = cleanCurrentQuantity.isEmpty() ? 0.0 : Double.parseDouble(cleanCurrentQuantity);
            double sellQty = cleanQuantityToSell.isEmpty() ? 0.0 : Double.parseDouble(cleanQuantityToSell);
            double remainingQty = currentQty - sellQty;
            
            String unit = extractUnitFromQuantity(currentQuantity);
            
            // Check if remaining quantity is 0 or less
            if (remainingQty <= 0) {
                // Delete the product from available products (it will still exist in sold products)
                String deleteSelection = DatabaseHelper.COLUMN_PRODUCT_NAME + " COLLATE NOCASE = ? AND " + 
                                       DatabaseHelper.COLUMN_PRODUCT_NAME + " NOT LIKE '%(Sale)%'";
                String[] deleteSelectionArgs = {productName};
                db.delete(DatabaseHelper.TABLE_PRODUCTS, deleteSelection, deleteSelectionArgs);
                
                Toast.makeText(this, "Product sold out and removed from inventory!", Toast.LENGTH_SHORT).show();
            } else {
                // Update the original product with remaining quantity
                ContentValues updateValues = new ContentValues();
                updateValues.put(DatabaseHelper.COLUMN_PRODUCT_QUANTITY, remainingQty + " " + unit);
                
                String updateSelection = DatabaseHelper.COLUMN_PRODUCT_NAME + " COLLATE NOCASE = ? AND " + 
                                      DatabaseHelper.COLUMN_PRODUCT_NAME + " NOT LIKE '%(Sale)%'";
                String[] updateSelectionArgs = {productName};
                
                db.update(DatabaseHelper.TABLE_PRODUCTS, updateValues, updateSelection, updateSelectionArgs);
                
                Toast.makeText(this, "Sale completed successfully!", Toast.LENGTH_SHORT).show();
            }
            
            // Create a separate sale record with the user-selected sale date
            ContentValues saleValues = new ContentValues();
            saleValues.put(DatabaseHelper.COLUMN_PRODUCT_NAME, productName + " (Sale)");
            saleValues.put(DatabaseHelper.COLUMN_PRODUCT_PRICE, productPrice);
            saleValues.put(DatabaseHelper.COLUMN_PRODUCT_QUANTITY, "Sold: " + quantityToSell + " Bill: " + currencySymbol + String.format("%.2f", totalBill));
            saleValues.put(DatabaseHelper.COLUMN_PRODUCT_DATE, saleDate);
            
            db.insert(DatabaseHelper.TABLE_PRODUCTS, null, saleValues);
            
            setResult(RESULT_OK);
            
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
        }
        
        cursor.close();
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + month + "-" + day;
    }

    private boolean isValidSaleDate(String saleDate) {
        try {
            // Parse the sale date
            String[] dateParts = saleDate.split("-");
            if (dateParts.length != 3) {
                return false;
            }
            
            int saleYear = Integer.parseInt(dateParts[0]);
            int saleMonth = Integer.parseInt(dateParts[1]);
            int saleDay = Integer.parseInt(dateParts[2]);
            
            // Get current date
            Calendar currentDate = Calendar.getInstance();
            int currentYear = currentDate.get(Calendar.YEAR);
            int currentMonth = currentDate.get(Calendar.MONTH) + 1;
            int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
            
            // Check if sale date is not in the future
            if (saleYear > currentYear) {
                return false;
            } else if (saleYear == currentYear) {
                if (saleMonth > currentMonth) {
                    return false;
                } else if (saleMonth == currentMonth) {
                    if (saleDay > currentDay) {
                        return false;
                    }
                }
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void clearFields() {
        etProductName.setText("");
        etPrice.setText("");
        etQuantity.setText("");
        etDate.setText("");
        tvBillAmount.setText("Total Bill: $0.00");
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

    private void updateTotalBill() {
        double totalBill = 0.0;
        String currencySymbol = "$"; // Default currency symbol
        
        if (multipleSalesContainer == null) {
            return;
        }
        
        for (int i = 0; i < multipleSalesContainer.getChildCount(); i++) {
            View saleRow = multipleSalesContainer.getChildAt(i);
            
            if (saleRow.getId() == 2001 || saleRow.getId() == 2002 || saleRow.getId() == 2003) {
                continue;
            }
            
            EditText etPrice = saleRow.findViewById(R.id.etPrice);
            EditText etQuantity = saleRow.findViewById(R.id.etQuantity);
            
            if (etPrice == null || etQuantity == null) {
                continue;
            }
            
            String priceStr = etPrice.getText().toString().trim();
            String quantityStr = etQuantity.getText().toString().trim();
            
            if (!priceStr.isEmpty() && !quantityStr.isEmpty()) {
                try {
                    // Extract currency symbol from price
                    currencySymbol = extractCurrencySymbol(priceStr);
                    
                    // Extract numeric values
                    String cleanPrice = priceStr.replaceAll("[^0-9.]", "");
                    String cleanQuantity = quantityStr.replaceAll("[^0-9.]", "");
                    
                    double price = cleanPrice.isEmpty() ? 0.0 : Double.parseDouble(cleanPrice);
                    double quantity = cleanQuantity.isEmpty() ? 1.0 : Double.parseDouble(cleanQuantity);
                    
                    // Calculate bill for this row
                    double rowBill = price * quantity;
                    totalBill += rowBill;
                    
                } catch (NumberFormatException e) {
                    // Ignore invalid format
                }
            }
        }
        
        // Update the total bill display - try multiple ways to find the TextView
        TextView totalBillTextView = multipleSalesContainer.findViewById(2003);
        if (totalBillTextView == null) {
            // Try to find it by searching through all child views
            for (int i = 0; i < multipleSalesContainer.getChildCount(); i++) {
                View child = multipleSalesContainer.getChildAt(i);
                if (child instanceof TextView && child.getId() == 2003) {
                    totalBillTextView = (TextView) child;
                    break;
                }
            }
        }
        
        if (totalBillTextView != null) {
            totalBillTextView.setText(String.format("Total Bill: %s%.2f", currencySymbol, totalBill));
        }
    }
}