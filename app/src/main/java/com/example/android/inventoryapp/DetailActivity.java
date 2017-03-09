package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import es.dmoral.toasty.Toasty;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_ITEM_LOADER = 0;
    private Uri mCurrentStockUri;
    private EditText mNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private int qty = 0;

    private boolean mStockHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mStockHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        mCurrentStockUri = intent.getData();

        if (mCurrentStockUri == null) {
            setTitle("Add an Item");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Item");
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, DetailActivity.this);
        }

        mNameEditText = (EditText) findViewById(R.id.name_edit_text);
        //mQuantityEditText = (EditText) findViewById(R.id.);
        mPriceEditText = (EditText) findViewById(R.id.price_edit_text);


        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);

        TextView plusIcon = (TextView) findViewById(R.id.plus);
        plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty > 100) {
                    Toasty.error(DetailActivity.this, "Quantity Cannot Be More Than 100",
                            Toast.LENGTH_SHORT).show();
                } else {
                    qty = qty + 1;
                    displayQuantity();
                }
            }
        });

        TextView minusIcon = (TextView) findViewById(R.id.minus);
        minusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty <= 0) {
                    Toasty.error(DetailActivity.this, "Quantity Cannot Be Less Than 0",
                            Toast.LENGTH_SHORT).show();
                } else {
                    qty = qty - 1;
                    displayQuantity();
                }
            }
        });

        Button saleButton = (Button) findViewById(R.id.sale_btn);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty <= 0) {
                    Toasty.error(DetailActivity.this, "Quantity Cannot Be Less Than 0",
                            Toast.LENGTH_SHORT).show();
                } else {
                    qty = qty - 1;
                    displayQuantity();
                }
            }
        });

        displayQuantity();
    }

    private void saveItem() {

        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();

        if (mCurrentStockUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                qty == 0) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_ITEM_NAME, nameString);
        values.put(InventoryEntry.COLUMN_ITEM_QTY, qty);
        values.put(InventoryEntry.COLUMN_ITEM_PRICE, priceString);

        if (mCurrentStockUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                Toasty.error(this, "Item Not Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toasty.success(this, "Item Saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentStockUri, values, null, null);

            if (rowsAffected == 0) {
                Toasty.error(this, "Item Not Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toasty.success(this, "Item Updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentStockUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveItem();
                finish();
                return true;

            case R.id.action_delete:
                // Do nothing
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayQuantity() {
        TextView quantityView = (TextView) findViewById(R.id.quantity);
        quantityView.setText(String.valueOf(qty));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_QTY,
                InventoryEntry.COLUMN_ITEM_PRICE };

        return new CursorLoader(this,
                mCurrentStockUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
            int qtyColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_QTY);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);

            String itemName = cursor.getString(nameColumnIndex);
            int itemQty = cursor.getInt(qtyColumnIndex);
            String itemPrice = cursor.getString(priceColumnIndex);

            mNameEditText.setText(itemName);
            mQuantityEditText.setText(Integer.toString(itemQty));
            mPriceEditText.setText(itemPrice);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mQuantityEditText.setSelection(0);
        mPriceEditText.setText("");
    }
}