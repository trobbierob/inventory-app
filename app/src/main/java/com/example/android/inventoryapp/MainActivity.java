package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements
        android.app.LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private InventoryCursorAdapter mCursorAdapter;

    private static final int INVENTORY_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });

        ListView inListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        inListView.setEmptyView(emptyView);

        mCursorAdapter = new InventoryCursorAdapter(this, null);
        inListView.setAdapter(mCursorAdapter);

        inListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Uri currentItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                intent.setData(currentItemUri);
                startActivity(intent);
            }
        });


        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    private void insertItem() {

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_ITEM_NAME, "Headphones");
        values.put(InventoryEntry.COLUMN_ITEM_QTY, 8);
        values.put(InventoryEntry.COLUMN_ITEM_PRICE, 7);
        values.put(InventoryEntry.COLUMN_ITEM_EMAIL, "cis@amazon.com");
        values.put(InventoryEntry.COLUMN_ITEM_PHONE, "8882804331");

        Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
            case R.id.action_delete:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_QTY,
                InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryEntry.COLUMN_ITEM_EMAIL,
                InventoryEntry.COLUMN_ITEM_PHONE};

        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}