package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
                    Toasty.error(DetailActivity.this, "Quantity Cannot Be More Than 100", Toast.LENGTH_SHORT).show();
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
                    Toasty.error(DetailActivity.this, "Quantity Cannot Be Less Than 0", Toast.LENGTH_SHORT).show();
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
                    Toasty.error(DetailActivity.this, "Quantity Cannot Be Less Than 0", Toast.LENGTH_SHORT).show();
                } else {
                    qty = qty - 1;
                    displayQuantity();
                }
            }
        });

        displayQuantity();
    }

    public void displayQuantity() {
        TextView quantityView = (TextView) findViewById(R.id.quantity);
        quantityView.setText(String.valueOf(qty));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
