package com.example.android.inventoryapp;

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

        int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        int qtyColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QTY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);
        Log.v(LOG_TAG, "nameColumnIndex is: " + nameColumnIndex);

        final int itemId = cursor.getInt(idColumnIndex);
        Log.v(LOG_TAG, "itemId is: " + itemId);
        String itemName = cursor.getString(nameColumnIndex);
        final int itemQty = cursor.getInt(qtyColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        Log.v(LOG_TAG, "itemName is: " + itemName);
        Log.v(LOG_TAG, "itemQty is: " + itemQty);

        nameTextView.setText(itemName);
        qtyTextView.setText("" + itemQty);       /**/
        priceTextView.setText(itemPrice);

        final int position = cursor.getPosition();

        Button saleButton = (Button) view.findViewById(R.id.sale);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position);
                Log.v(LOG_TAG, "The position is: " + position);
                Log.v(LOG_TAG, "itemId is: " + itemId);
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