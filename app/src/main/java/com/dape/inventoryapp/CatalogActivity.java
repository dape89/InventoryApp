package com.dape.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
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

import com.dape.inventoryapp.adapter.ProductCursorAdapter;
import com.dape.inventoryapp.data.ProductContract.ProductEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ProductCursorAdapter adapter;
    private static final int DATA_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_products:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projecton = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_QUANTITY};

        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                projecton,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    public void deleteAllProducts() {
        getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
    }

    public void init() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CatalogActivity.this, EditorActivity.class));
            }
        });
        ListView lsvProducts = findViewById(R.id.lsvProducts);
        View emptyView = findViewById(R.id.rlEmptyView);
        lsvProducts.setEmptyView(emptyView);
        adapter = new ProductCursorAdapter(this, null);
        lsvProducts.setAdapter(adapter);
        getLoaderManager().initLoader(DATA_LOADER, null, this);
        lsvProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intentProduct = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri contentUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intentProduct.setData(contentUri);
                startActivity(intentProduct);
            }
        });
    }
}
