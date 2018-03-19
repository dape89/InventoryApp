package com.dape.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dape.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Danale on 07/03/2018.
 */

public class ProductDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = ProductDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDb) {
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_SUPPLIER + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PHONE + " INTEGER NOT NULL, "
                + ProductEntry.COLUMN_EMAIL + " TEXT NOT NULL);";
        sqlDb.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlDb, int oldVersion, int newVersion) {
        sqlDb.execSQL("DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME);
        onCreate(sqlDb);
    }
}
