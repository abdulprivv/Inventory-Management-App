package com.example.inventoryapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnRegister;
    DatabaseHelper dbHelper;
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_EMAIL = "userEmail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is already logged in
        if (isUserLoggedIn()) {
            // User is already logged in, go directly to dashboard
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return;
        }
        
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (checkUser(email, password)) {
                // Save login session
                saveLoginSession(email);
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, DashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private boolean checkUser(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COLUMN_ID};
        String selection = DatabaseHelper.COLUMN_USER_EMAIL + " = ?" + " AND " + DatabaseHelper.COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount > 0;
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void saveLoginSession(String email) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public static void logout(android.content.Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
