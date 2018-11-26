package com.example.emilykuo.store.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StoreDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Store.db";
    public static final String LOG_TAG = StoreDBHelper.class.getSimpleName();

    public StoreDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + StoreContract.ProductEntry.TABLE_NAME +
                " (" + StoreContract.ProductEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StoreContract.ProductEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + StoreContract.ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + StoreContract.ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + StoreContract.ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + StoreContract.ProductEntry.COLUMN_SUPPLIER_NUMBER
                + " TEXT NOT NULL); ";

        Log.v(LOG_TAG, SQL_CREATE_PRODUCTS_TABLE);
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}