package com.dape.inventoryapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dape.inventoryapp.data.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText edtProduct;
    private EditText edtPrice;
    private EditText edtQuantity;
    private EditText edtSupplier;
    private EditText edtPhone;
    private EditText edtEmail;
    private int quantity = 0;
    private static final int EXISTING_DATA_LOADER = 0;
    private static final String PHONE = "tel";
    private Uri currentUri;
    private boolean dataHasChanged = false;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentUri == null) {
            MenuItem menuItemDelete = menu.findItem(R.id.action_delete);
            MenuItem menuItemCall = menu.findItem(R.id.action_call);
            MenuItem menuItemEmail = menu.findItem(R.id.action_send_email);
            menuItemDelete.setVisible(false);
            menuItemCall.setVisible(false);
            menuItemEmail.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                break;
            case R.id.action_call:
                call();
                break;
            case R.id.action_send_email:
                sendEmail();
                break;
            case android.R.id.home:
                if (!dataHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        edtProduct = findViewById(R.id.edtProduct);
        edtPrice = findViewById(R.id.edtPrice);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtSupplier = findViewById(R.id.edtSupplier);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        Button btnIncrement = findViewById(R.id.btnIncrement);
        Button btnDecrement = findViewById(R.id.btnDecrement);
        Intent intent = getIntent();
        currentUri = intent.getData();
        if (currentUri == null) {
            setTitle(getString(R.string.title_activity_add));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.title_activity_edit));
            getLoaderManager().initLoader(EXISTING_DATA_LOADER, null, this);
        }
        edtProduct.setOnTouchListener(touchListener);
        edtPrice.setOnTouchListener(touchListener);
        edtQuantity.setOnTouchListener(touchListener);
        edtSupplier.setOnTouchListener(touchListener);
        edtPhone.setOnTouchListener(touchListener);
        edtEmail.setOnTouchListener(touchListener);
        btnIncrement.setOnTouchListener(touchListener);
        btnDecrement.setOnTouchListener(touchListener);
        btnIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = Integer.parseInt(edtQuantity.getText().toString());
                quantity = quantity + 1;
                edtQuantity.setText("" + quantity);
            }
        });
        btnDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity = Integer.parseInt(edtQuantity.getText().toString());
                if (quantity == 0) {
                    return;
                }
                quantity = quantity - 1;
                edtQuantity.setText("" + quantity);
            }
        });
    }

    private void saveProduct() {
        String strProduct = edtProduct.getText().toString().trim();
        String strPrice = edtPrice.getText().toString().trim();
        String strQuantity = edtQuantity.getText().toString().trim();
        String strSupplier = edtSupplier.getText().toString().trim();
        String strPhone = edtPhone.getText().toString().trim();
        String strEmail = edtEmail.getText().toString().trim();
        if (TextUtils.isEmpty(strProduct) || TextUtils.isEmpty(strPrice) ||
                TextUtils.isEmpty(strQuantity) || TextUtils.isEmpty(strSupplier) ||
                TextUtils.isEmpty(strPhone) || TextUtils.isEmpty(strEmail)) {
            Toast.makeText(EditorActivity.this, getString(R.string.empty_field_msg), Toast.LENGTH_SHORT).show();
        } else {
            int price = Integer.parseInt(strPrice);
            int quantity = Integer.parseInt(strQuantity);
            int phone = Integer.parseInt(strPhone);
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT, strProduct);
            values.put(ProductEntry.COLUMN_PRICE, price);
            values.put(ProductEntry.COLUMN_QUANTITY, quantity);
            values.put(ProductEntry.COLUMN_SUPPLIER, strSupplier);
            values.put(ProductEntry.COLUMN_PHONE, phone);
            values.put(ProductEntry.COLUMN_EMAIL, strEmail);
            if (currentUri == null) {
                Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.product_save_error_msg), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.product_save_msg), Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(currentUri, values, null, null);
                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projecton = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER,
                ProductEntry.COLUMN_PHONE,
                ProductEntry.COLUMN_EMAIL};
        return new CursorLoader(this,
                currentUri,
                projecton,
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
            int productColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PHONE);
            int emailColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_EMAIL);
            String product = cursor.getString(productColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            int phone = cursor.getInt(phoneColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            edtProduct.setText(product);
            edtPrice.setText(Integer.toString(price));
            edtQuantity.setText(Integer.toString(quantity));
            edtSupplier.setText(supplier);
            edtPhone.setText(Integer.toString(phone));
            edtEmail.setText(email);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        edtProduct.setText("");
        edtPrice.setText("");
        edtQuantity.setText("");
        edtSupplier.setText("");
        edtPhone.setText("");
        edtEmail.setText("");
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            dataHasChanged = true;
            return false;
        }
    };

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!dataHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonOnClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonOnClickListener);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (currentUri != null) {
            int rowsDeleted = getContentResolver().delete(currentUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void call() {
        String phone = edtPhone.getText().toString().trim();
        Intent intentCall = new Intent(Intent.ACTION_DIAL, Uri.fromParts(PHONE, phone, null));
        startActivity(intentCall);
    }

    private void sendEmail() {
        Intent intentSendEmail = new Intent(Intent.ACTION_SEND);
        intentSendEmail.setData(Uri.parse(getString(R.string.intent_email_setData)));
        intentSendEmail.setType(getString(R.string.intent_email_setType));
        String[] to = {edtEmail.getText().toString()};
        intentSendEmail.putExtra(Intent.EXTRA_EMAIL, to);
        if (intentSendEmail.resolveActivity(getPackageManager()) != null) {
            startActivity(intentSendEmail);
        }
    }
}
