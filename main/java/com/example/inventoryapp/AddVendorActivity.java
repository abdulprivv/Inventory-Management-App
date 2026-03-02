package com.example.inventoryapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class AddVendorActivity extends AppCompatActivity {

    EditText etVendorName, etVendorContact, etVendorEmail, etVendorAddress;
    Button btnSaveVendor;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vendor);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar_add_vendor);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        etVendorName = findViewById(R.id.etVendorName);
        etVendorContact = findViewById(R.id.etVendorContact);
        etVendorEmail = findViewById(R.id.etVendorEmail);
        etVendorAddress = findViewById(R.id.etVendorAddress);
        btnSaveVendor = findViewById(R.id.btnSaveVendor);

        btnSaveVendor.setOnClickListener(v -> {
            saveVendor();
            clearFields();
        });
    }

    private void saveVendor() {
        String name = etVendorName.getText().toString().trim();
        String contact = etVendorContact.getText().toString().trim();
        String email = etVendorEmail.getText().toString().trim();
        String address = etVendorAddress.getText().toString().trim();

        if (name.isEmpty() || contact.isEmpty() || email.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_VENDOR_NAME, name);
            values.put(DatabaseHelper.COLUMN_VENDOR_CONTACT, contact);
            values.put(DatabaseHelper.COLUMN_VENDOR_EMAIL, email);
            values.put(DatabaseHelper.COLUMN_VENDOR_ADDRESS, address);
            long newRowId = db.insert(DatabaseHelper.TABLE_VENDORS, null, values);

            if (newRowId != -1) {
                Toast.makeText(this, "Vendor Saved Successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error saving vendor", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void clearFields() {
        etVendorName.setText("");
        etVendorContact.setText("");
        etVendorEmail.setText("");
        etVendorAddress.setText("");
        etVendorName.requestFocus();
    }
}
