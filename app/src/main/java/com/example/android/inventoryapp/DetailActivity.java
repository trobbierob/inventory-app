package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.ByteArrayOutputStream;

import es.dmoral.toasty.Toasty;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int EXISTING_ITEM_LOADER = 0;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private EditText mEmailEditText;
    private EditText mPhoneEditText;
    private EditText mNameEditText;
    private Uri mCurrentStockUri;
    private Button mCameraButton;
    private ImageView mImageView;
    private byte[] byteArray;
    Bitmap imageBitmap;

    private int qty = 0;

    private String itemPhone = "";
    private String itemEmail = "";

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
            setTitle(getString(R.string.add_item));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_item));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.name_edit_text);
        mQuantityEditText = (EditText) findViewById(R.id.quantity_edit_text);
        mPriceEditText = (EditText) findViewById(R.id.price_edit_text);
        mEmailEditText = (EditText) findViewById(R.id.email_edit_text);
        mPhoneEditText = (EditText) findViewById(R.id.phone_edit_text);
        mCameraButton = (Button) findViewById(R.id.photo_btn);
        mImageView = (ImageView) findViewById(R.id.image_view);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mEmailEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
        mCameraButton.setOnTouchListener(mTouchListener);
        mImageView.setOnTouchListener(mTouchListener);


        TextView plusIcon = (TextView) findViewById(R.id.plus);
        plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (qty > 100) {
                    Toasty.error(DetailActivity.this, getString(R.string.not_more_than_100),
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
                    Toasty.error(DetailActivity.this, getString(R.string.not_less_than_0),
                            Toast.LENGTH_SHORT).show();
                } else {
                    qty = qty - 1;
                    displayQuantity();
                }
            }
        });

        FloatingActionButton email = new FloatingActionButton(getBaseContext());
        email.setColorNormalResId(R.color.colorAccent);
        email.setColorPressedResId(R.color.colorAccent);
        email.setIcon(R.drawable.ic_email_white_24dp);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(getString(R.string.mailto)));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{itemEmail});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.place_order));

                try {
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
                    finish();
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(DetailActivity.this,
                            R.string.missing_email_client, Toast.LENGTH_SHORT).show();
                }
            }
        });

        FloatingActionButton call = new FloatingActionButton(this);
        call.setColorNormalResId(R.color.colorAccent);
        call.setColorPressedResId(R.color.colorAccent);
        call.setIcon(R.drawable.ic_phone_white_24dp);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse(getString(R.string.telephone_abbr) + itemPhone);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                if (callIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(callIntent);
                }
            }
        });

        /**
         * Creates a fab that will display two fabs to contact the manufacturer
         * by email or by phone number
         */
        final FloatingActionsMenu multiContact = (FloatingActionsMenu) findViewById(R.id.multiple_contact);
        multiContact.addButton(email);
        multiContact.addButton(call);

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });
        displayQuantity();
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            getBytes(imageBitmap);
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    public void getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byteArray = stream.toByteArray();
    }

    private void saveItem() {

        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String qtyString = mQuantityEditText.getText().toString().trim();
        String emailString = mEmailEditText.getText().toString().trim();
        String phoneString = mPhoneEditText.getText().toString().trim();

        if (mCurrentStockUri == null &&
                TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(qtyString) || TextUtils.isEmpty(emailString) ||
                TextUtils.isEmpty(phoneString)) {

            Toasty.info(this, getString(R.string.no_item_added), Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_ITEM_NAME, nameString);
        values.put(InventoryEntry.COLUMN_ITEM_QTY, qtyString);
        values.put(InventoryEntry.COLUMN_ITEM_PRICE, priceString);
        values.put(InventoryEntry.COLUMN_ITEM_EMAIL, emailString);
        values.put(InventoryEntry.COLUMN_ITEM_PHONE, phoneString);
        values.put(InventoryEntry.COLUMN_ITEM_IMAGE, byteArray);

        if (mCurrentStockUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null || TextUtils.isEmpty(nameString)) {
                Toasty.error(this, getString(R.string.item_not_saved), Toast.LENGTH_SHORT).show();
            } else {
                Toasty.success(this, getString(R.string.item_saved), Toast.LENGTH_SHORT).show();
            }

        } else {
            int rowsAffected = getContentResolver().update(mCurrentStockUri, values, null, null);

            if (rowsAffected == 0) {
                Toasty.error(this, getString(R.string.item_not_updated), Toast.LENGTH_SHORT).show();
            } else {
                Toasty.success(this, getString(R.string.item_updated), Toast.LENGTH_SHORT).show();
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
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mStockHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayQuantity() {
        EditText quantityViewEditText = (EditText) findViewById(R.id.quantity_edit_text);
        quantityViewEditText.setText(String.valueOf(qty));
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You sure you want to do that?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!mStockHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Delete inventory item.
     */
    private void deleteItem() {
        // Only perform the delete if this is an existing item.
        if (mCurrentStockUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentStockUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toasty.error(DetailActivity.this, getString(R.string.delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toasty.success(DetailActivity.this, getString(R.string.delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_QTY,
                InventoryEntry.COLUMN_ITEM_PRICE,
                InventoryEntry.COLUMN_ITEM_EMAIL,
                InventoryEntry.COLUMN_ITEM_PHONE,
                InventoryEntry.COLUMN_ITEM_IMAGE};

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
            int emailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_EMAIL);
            int phoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PHONE);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_IMAGE);

            String itemName = cursor.getString(nameColumnIndex);
            String itemQty = cursor.getString(qtyColumnIndex);
            String itemPrice = cursor.getString(priceColumnIndex);
            itemEmail = cursor.getString(emailColumnIndex);
            byte[] image = cursor.getBlob(imageColumnIndex);
            itemPhone = cursor.getString(phoneColumnIndex);

            if (image == null) {

            } else {
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                mImageView.setImageBitmap(bitmap);
            }

            mNameEditText.setText(itemName);
            mQuantityEditText.setText(itemQty);
            mPriceEditText.setText(itemPrice);
            mEmailEditText.setText(itemEmail);
            mPhoneEditText.setText(itemPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mEmailEditText.setText("");
        mPhoneEditText.setText("");
        mImageView.setImageBitmap(null);
    }
}