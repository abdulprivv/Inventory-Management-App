package com.example.inventoryapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DividerItemDecoration;

public class VendorListActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView vendorRecyclerView;
    private VendorAdapter vendorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_list);

        Toolbar toolbar = findViewById(R.id.toolbar_vendor_list);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        dbHelper = new DatabaseHelper(this);
        vendorRecyclerView = findViewById(R.id.vendor_recycler_view);
        vendorRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Add spacing between items to match product list
        vendorRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(android.graphics.Rect outRect, android.view.View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = 1;
                outRect.bottom = 1;
            }
        });

        displayVendors();
    }

    private void displayVendors() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_VENDORS,
                null, null, null, null, null, null);

        vendorAdapter = new VendorAdapter(cursor, this);
        vendorRecyclerView.setAdapter(vendorAdapter);
    }
}