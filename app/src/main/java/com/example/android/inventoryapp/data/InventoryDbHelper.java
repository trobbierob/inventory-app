package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "stock.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String TEXT_TYPE_NOT_NULL = " TEXT NOT NULL";
    private static final String INT_TYPE = " INTEGER";
    private static final String INT_TYPE_NOT_NULL = " INTEGER NOT NULL";
    private static final String DEFAULT_ZERO = " DEFAULT 0";
    private static final String COMMA_SEP = ", ";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
            + InventoryEntry.TABLE_NAME + " (" + InventoryEntry._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT"
            + COMMA_SEP + InventoryEntry.COLUMN_ITEM_NAME
            + TEXT_TYPE + COMMA_SEP
            + InventoryEntry.COLUMN_ITEM_QTY + INT_TYPE + COMMA_SEP
            + InventoryEntry.COLUMN_ITEM_PRICE + INT_TYPE + COMMA_SEP
            + InventoryEntry.COLUMN_ITEM_EMAIL + TEXT_TYPE + COMMA_SEP
            + InventoryEntry.COLUMN_ITEM_PHONE + TEXT_TYPE + ")";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(SQL_CREATE_ENTRIES);
        //onCreate(db);
    }
}
