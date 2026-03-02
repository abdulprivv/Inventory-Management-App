package com.example.inventoryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_VENDORS = "vendors";

    // Common column names
    public static final String COLUMN_ID = "_id";

    // USERS Table - column names
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";

    // PRODUCTS Table - column names
    public static final String COLUMN_PRODUCT_NAME = "name";
    public static final String COLUMN_PRODUCT_PRICE = "price";
    public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
    public static final String COLUMN_PRODUCT_DATE = "date";

    // VENDORS Table - column names
    public static final String COLUMN_VENDOR_NAME = "name";
    public static final String COLUMN_VENDOR_CONTACT = "contact";
    public static final String COLUMN_VENDOR_EMAIL = "email";
    public static final String COLUMN_VENDOR_ADDRESS = "address";

    // Table Create Statements
    // Users table create statement
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT,"
            + COLUMN_USER_PASSWORD + " TEXT" + ")";

    // Products table create statement
    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PRODUCT_NAME + " TEXT,"
            + COLUMN_PRODUCT_PRICE + " TEXT,"
            + COLUMN_PRODUCT_QUANTITY + " TEXT,"
            + COLUMN_PRODUCT_DATE + " TEXT" + ")";

    // Vendors table create statement
    private static final String CREATE_TABLE_VENDORS = "CREATE TABLE " + TABLE_VENDORS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_VENDOR_NAME + " TEXT,"
            + COLUMN_VENDOR_CONTACT + " TEXT,"
            + COLUMN_VENDOR_EMAIL + " TEXT,"
            + COLUMN_VENDOR_ADDRESS + " TEXT" + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_VENDORS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENDORS);

        // create new tables
        onCreate(db);
    }
} 