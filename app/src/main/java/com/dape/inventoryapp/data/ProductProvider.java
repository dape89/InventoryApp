package com.dape.inventoryapp.data;

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

import com.dape.inventoryapp.R;
import com.dape.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Danale on 09/03/2018.
 */

@SuppressWarnings("ALL")
public class ProductProvider extends ContentProvider {
    private ProductDbHelper productDbHelper;
    private static final int PRODUCT = 100;
    private static final int PRODUCT_ID = 101;
    private static final String THROW_QUERY = "Cannot query unknown URI ";
    private static final String THROW_GETTYPE_PRIMARY = "Unknown URI ";
    private static final String THROW_GETTYPE_SECONDARY = " with match ";
    private static final String THROW_DELETE = "Deletion is not supported for ";
    private static final String THROW_UPDATE = "Update is not supported for ";
    private static final String PRODUCT_MSG = "It's required a product name";
    private static final String PRICE_MSG = "It's required a price";
    private static final String QUANTITY_MSG = "It's required a quantity";
    private static final String SUPPLIER_MSG = "It's required a supplier name";
    private static final String PHONE_MSG = "It's required a phone number";
    private static final String EMAIL_MSG = "It's required an email address";
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();
    private static final String TAG_MSG = "Failed to insert row for ";

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_DATA, PRODUCT);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_DATA + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        productDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projecton, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sqlDb = productDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                cursor = sqlDb.query(ProductEntry.TABLE_NAME, projecton, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqlDb.query(ProductEntry.TABLE_NAME, projecton, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(THROW_QUERY + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(THROW_GETTYPE_PRIMARY + uri + THROW_GETTYPE_SECONDARY + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                getContext().getContentResolver().notifyChange(uri, null);
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException(R.string.product_save_error_msg + uri.toString());
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase sqlDb = productDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                rowsDeleted = sqlDb.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = sqlDb.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(THROW_DELETE + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                getContext().getContentResolver().notifyChange(uri, null);
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(THROW_UPDATE + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT)) {
            String product = values.getAsString(ProductEntry.COLUMN_PRODUCT);
            if (product == null) {
                throw new IllegalArgumentException(PRODUCT_MSG);
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(ProductEntry.COLUMN_PRICE);
            if (price == null) {
                throw new IllegalArgumentException(PRICE_MSG);
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException(QUANTITY_MSG);
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_SUPPLIER)) {
            String supplier = values.getAsString(ProductEntry.COLUMN_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException(SUPPLIER_MSG);
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_PHONE)) {
            Integer phone = values.getAsInteger(ProductEntry.COLUMN_PHONE);
            if (phone == null) {
                throw new IllegalArgumentException(PHONE_MSG);
            }
        }
        if (values.containsKey(ProductEntry.COLUMN_EMAIL)) {
            String email = values.getAsString(ProductEntry.COLUMN_EMAIL);
            if (email == null) {
                throw new IllegalArgumentException(EMAIL_MSG);
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase sqlDb = productDbHelper.getWritableDatabase();
        int rowsUpdated = sqlDb.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    public Uri insertProduct(Uri uri, ContentValues values) {
        String product = values.getAsString(ProductEntry.COLUMN_PRODUCT);
        if (product == null) {
            throw new IllegalArgumentException(PRODUCT_MSG);
        }
        Integer price = values.getAsInteger(ProductEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException(PRICE_MSG);
        }
        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException(QUANTITY_MSG);
        }
        String supplier = values.getAsString(ProductEntry.COLUMN_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException(SUPPLIER_MSG);
        }
        Integer phone = values.getAsInteger(ProductEntry.COLUMN_PHONE);
        if (phone == null) {
            throw new IllegalArgumentException(PHONE_MSG);
        }
        String email = values.getAsString(ProductEntry.COLUMN_EMAIL);
        if (email == null) {
            throw new IllegalArgumentException(EMAIL_MSG);
        }
        SQLiteDatabase sqlDb = productDbHelper.getWritableDatabase();
        long id = sqlDb.insert(ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, TAG_MSG + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }
}
