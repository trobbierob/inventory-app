package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    public static final String LOG_TAG = InventoryCursorAdapter.class.getSimpleName();
    private Uri mCurrentStockUri;

    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        final TextView qtyTextView = (TextView) view.findViewById(R.id.qty);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);

        final int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        final int qtyColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QTY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
        int emailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_EMAIL);
        int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PHONE);
        int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_IMAGE);

        //final int itemId = cursor.getInt(idColumnIndex);
        final long itemId = cursor.getLong(idColumnIndex);


        Log.v(LOG_TAG, "itemId is: " + itemId);
        final String itemName = cursor.getString(nameColumnIndex);
        final int itemQty = cursor.getInt(qtyColumnIndex);
        final String itemPrice = cursor.getString(priceColumnIndex);
        final String itemEmail = cursor.getString(emailColumnIndex);
        final String itemPhone = cursor.getString(phoneColumnIndex);
        //final byte[] itemImage = cursor.getBlob(imageColumnIndex);

        nameTextView.setText(itemName);
        qtyTextView.setText("" + itemQty);       /**/
        priceTextView.setText(itemPrice);

        final int position = cursor.getPosition();
        cursor.moveToPosition(position);
        Button saleButton = (Button) view.findViewById(R.id.sale);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri currentItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, itemId);

                if(itemQty > 0) {


                    ContentValues values = new ContentValues();

                    //values.put(InventoryEntry.COLUMN_ITEM_NAME, itemName);
                    values.put(InventoryEntry.COLUMN_ITEM_QTY, subtract(itemQty));
                    Log.v(LOG_TAG, "itemQty is: " + (subtract(itemQty)));
                    //values.put(InventoryEntry.COLUMN_ITEM_PRICE, itemPrice);
                    //values.put(InventoryEntry.COLUMN_ITEM_EMAIL, itemEmail);
                    //values.put(InventoryEntry.COLUMN_ITEM_PHONE, itemPhone);
                    //values.put(InventoryEntry.COLUMN_ITEM_IMAGE, byteArray);

                    context.getContentResolver().update(currentItemUri, values, null, null);
                }
            }
        });
    }

    private int subtract(int qty) {
        if(qty <= 0) {
            // Do nothing
        } else {
            qty = qty - 1;
        }
        return qty;
    }
}