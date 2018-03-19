package com.dape.inventoryapp.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dape.inventoryapp.R;
import com.dape.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Danale on 10/03/2018.
 */

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView tvProduct = view.findViewById(R.id.tvProduct);
        TextView tvPrice = view.findViewById(R.id.tvPrice);
        final TextView tvQuantity = view.findViewById(R.id.tvQuantity);
        ImageView imvItem = view.findViewById(R.id.imvSale);
        int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
        int productColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QUANTITY);
        final int id = cursor.getInt(idColumnIndex);
        String product = cursor.getString(productColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        final String quantity = cursor.getString(quantityColumnIndex);
        tvProduct.setText(product);
        tvPrice.setText(price + " €");
        tvQuantity.setText(quantity);
        imvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQuantity = Integer.valueOf(quantity);
                Uri currentUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                sale(context, newQuantity, currentUri);
            }
        });
    }

    private void sale(Context context, int quantity, Uri uri) {
        if (quantity == 0) {
            Toast.makeText(context, R.string.sale_msg, Toast.LENGTH_SHORT).show();
        } else {
            int updateQuantity = quantity - 1;
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_QUANTITY, updateQuantity);
            context.getContentResolver().update(uri, values, null, null);
        }
    }
}
