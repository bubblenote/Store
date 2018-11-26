package com.example.emilykuo.store;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.emilykuo.store.data.StoreContract.ProductEntry;

public class StoreCursorAdapter extends CursorAdapter {

    public StoreCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView itemNameTextView = view.findViewById(R.id.list_item_name);
        TextView priceTextView = view.findViewById(R.id.list_price);
        final TextView quantityTextView = view.findViewById(R.id.list_quantity);

        int itemIDColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
        int itemNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);

        final long id = cursor.getLong(itemIDColumnIndex);
        final String itemName = cursor.getString(itemNameColumnIndex);
        final String price = cursor.getString(priceColumnIndex);
        final String quantity = cursor.getString(quantityColumnIndex);

        itemNameTextView.setText(itemName);
        priceTextView.setText(price);
        quantityTextView.setText(quantity);

        Button button = view.findViewById(R.id.list_sale_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri currentInventoryUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                ContentValues values = new ContentValues();
                values.put(ProductEntry.COLUMN_NAME, itemName);
                values.put(ProductEntry.COLUMN_PRICE, price);


                if (Integer.valueOf(quantity) > 0) {
                    values.put(ProductEntry.COLUMN_QUANTITY, Integer.valueOf(quantity) - 1);
                    context.getContentResolver().update(currentInventoryUri, values, null, null);

                }
            }
        });


    }


}