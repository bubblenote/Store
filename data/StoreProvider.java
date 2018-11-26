package com.example.emilykuo.store.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class StoreProvider extends ContentProvider {

    private static final int STORE = 100;
    private static final int STORE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String LOG_TAG = StoreProvider.class.getSimpleName();

    static{
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_STORE, STORE);
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_STORE + "/#", STORE_ID );
    }

    private StoreDBHelper storeHelper;


    @Override
    public boolean onCreate() {
        storeHelper = new StoreDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = storeHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch(match){
            case STORE:
                //cursor = database.query(StoreContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor = database.query(StoreContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case STORE_ID:
                selection = StoreContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(StoreContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;


            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case STORE:
                return StoreContract.ProductEntry.CONTENT_LIST_TYPE;
            case STORE_ID:
                return StoreContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI" + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case STORE:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    private Uri insertProduct(Uri uri, ContentValues values){

        String name = values.getAsString(StoreContract.ProductEntry.COLUMN_NAME);
        if (name == null){
            throw new IllegalArgumentException("The product requires a name");
        }

        String supplier = values.getAsString(StoreContract.ProductEntry.COLUMN_SUPPLIER_NAME);
        if (supplier == null){
            throw new IllegalArgumentException("The supplier needs a name ");
        }

        String supplierNumber = values.getAsString(StoreContract.ProductEntry.COLUMN_SUPPLIER_NUMBER);
        if (supplierNumber == null){
            throw new IllegalArgumentException("The supplier requires a phone number");
        }

        Integer price = values.getAsInteger(StoreContract.ProductEntry.COLUMN_PRICE);
        if (price < 0 || price == null){
            throw new IllegalArgumentException("Please enter a valid price");
        }

        Integer quantity = values.getAsInteger(StoreContract.ProductEntry.COLUMN_QUANTITY);
        if(quantity != null && quantity < 0){
            throw new IllegalArgumentException("Please enter a valid quantity");
        }


        SQLiteDatabase db = storeHelper.getWritableDatabase();
        long id = db.insert(StoreContract.ProductEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);


    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = storeHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match){
            case STORE:
                rowsDeleted = db.delete(StoreContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STORE_ID:
                selection = StoreContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(StoreContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STORE:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case STORE_ID:
                // For the ITEM_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = StoreContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link ItemEntry#COLUMN_PRODUCT_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(StoreContract.ProductEntry.COLUMN_NAME)) {
            String name = values.getAsString(StoreContract.ProductEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("This product requires a name");
            }
        }

        if (values.containsKey(StoreContract.ProductEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(StoreContract.ProductEntry.COLUMN_QUANTITY);
            // Check that the quantity is greater than 0
            if (quantity == null && quantity < 0) {
                throw new IllegalArgumentException("This item requires a valid quantity");
            }
        }

        if (values.containsKey(StoreContract.ProductEntry.COLUMN_PRICE)) {
            // Check that the price is greater than or equal to 0
            Integer weight = values.getAsInteger(StoreContract.ProductEntry.COLUMN_PRICE);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("This item requires a valid price");
            }
        }

        if (values.containsKey(StoreContract.ProductEntry.COLUMN_SUPPLIER_NAME)) {
            String name = values.getAsString(StoreContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            if (name == null) {
                throw new IllegalArgumentException("This supplier requires a valid name");
            }
        }

        if (values.containsKey(StoreContract.ProductEntry.COLUMN_SUPPLIER_NUMBER)) {
            String name = values.getAsString(StoreContract.ProductEntry.COLUMN_SUPPLIER_NUMBER);
            if (name == null) {
                throw new IllegalArgumentException("This supplier requires a valid phone number");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase db = storeHelper.getWritableDatabase();
        int rowsUpdated = db.update(StoreContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;

    }

}

