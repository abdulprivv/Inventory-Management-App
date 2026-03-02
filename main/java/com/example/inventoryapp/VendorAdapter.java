package com.example.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {

    private Cursor cursor;
    private Context context;
    private DatabaseHelper dbHelper;

    public VendorAdapter(Cursor cursor, Context context) {
        this.cursor = cursor;
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vendor_card_item, parent, false);
        return new VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            long vendorId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VENDOR_NAME));
            String contact = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VENDOR_CONTACT));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VENDOR_EMAIL));
            String address = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VENDOR_ADDRESS));

            holder.tvVendorName.setText(name);
            holder.tvVendorContact.setText(contact);
            holder.tvVendorEmail.setText(email);
            holder.tvVendorAddress.setText(address);

            // Set up delete button click listener
            holder.btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog(vendorId));
        }
    }

    @Override
    public int getItemCount() {
        return (cursor == null) ? 0 : cursor.getCount();
    }

    private void showDeleteConfirmationDialog(long vendorId) {
        // Create custom dialog layout
        android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(0xFF013137);
        
        // Add title
        android.widget.TextView titleView = new android.widget.TextView(context);
        titleView.setText("Delete Vendor");
        titleView.setTextColor(0xFFFFFFFF);
        titleView.setTextSize(18);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleView.setPadding(0, 0, 0, 16);
        layout.addView(titleView);
        
        // Add message
        android.widget.TextView messageView = new android.widget.TextView(context);
        messageView.setText("Are you sure you want to delete this vendor?");
        messageView.setTextColor(0xFFFFFFFF);
        messageView.setTextSize(16);
        messageView.setPadding(0, 0, 0, 32);
        layout.addView(messageView);
        
        // Create button container
        android.widget.LinearLayout buttonContainer = new android.widget.LinearLayout(context);
        buttonContainer.setOrientation(android.widget.LinearLayout.HORIZONTAL);
        buttonContainer.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        
        // Create Cancel button
        android.widget.Button cancelButton = new android.widget.Button(context);
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
        android.widget.Button deleteButton = new android.widget.Button(context);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        
        // Set button click listeners
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        deleteButton.setOnClickListener(v -> {
            dialog.dismiss();
            deleteVendor(vendorId);
        });
    }

    private void deleteVendor(long vendorId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(vendorId)};
        
        int deletedRows = db.delete(DatabaseHelper.TABLE_VENDORS, selection, selectionArgs);
        
        if (deletedRows > 0) {
            // Refresh the cursor
            refreshCursor();
            // Show success message
            Toast.makeText(context, "Vendor deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Error deleting vendor", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor newCursor = db.query(DatabaseHelper.TABLE_VENDORS,
                null, null, null, null, null, null);
        
        // Close old cursor and update with new one
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    static class VendorViewHolder extends RecyclerView.ViewHolder {
        TextView tvVendorName, tvVendorContact, tvVendorEmail, tvVendorAddress, btnDelete;

        public VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVendorName = itemView.findViewById(R.id.tvVendorName);
            tvVendorContact = itemView.findViewById(R.id.tvVendorContact);
            tvVendorEmail = itemView.findViewById(R.id.tvVendorEmail);
            tvVendorAddress = itemView.findViewById(R.id.tvVendorAddress);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
} 