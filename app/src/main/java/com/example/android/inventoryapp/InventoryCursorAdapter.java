package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
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

    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        final TextView qtyTextView = (TextView) view.findViewById(R.id.qty);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        Button saleButton = (Button) view.findViewById(R.id.sale);

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
        int qtyColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QTY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);

        String itemName = cursor.getString(nameColumnIndex);
        final String itemQty = cursor.getString(qtyColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);

        Log.v(LOG_TAG, "String itemQty is: " + itemQty);

        nameTextView.setText(itemName);
        qtyTextView.setText(itemQty);
        priceTextView.setText(itemPrice);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qtyTextView.setText(change(itemQty));
                Log.v(LOG_TAG, "This is the button");
            }
        });
    }

    private String change(String qty) {

        Log.v(LOG_TAG, "Change method, qty is: " + qty);
        int qtyNum = Integer.parseInt(qty);
        Log.v(LOG_TAG, "qtyNum is: " + qtyNum);

        if(qtyNum <= 0) {

        } else {
            qtyNum = qtyNum - 1;
        }
        return Integer.toString(qtyNum);
    }

}