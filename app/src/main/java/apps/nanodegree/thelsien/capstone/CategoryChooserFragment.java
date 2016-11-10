package apps.nanodegree.thelsien.capstone;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Calendar;

import apps.nanodegree.thelsien.capstone.adapters.CategoriesAdapter;
import apps.nanodegree.thelsien.capstone.data.MainCategoriesTable;
import apps.nanodegree.thelsien.capstone.data.SpendingsTable;

/**
 * Created by frodo on 2016. 11. 10..
 */

public class CategoryChooserFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CategoriesAdapter.OnCategoryClickListener {

    private static final String TAG = CategoryChooserFragment.class.getSimpleName();
    private static final int CATEGORY_LOADER = 2;
    private CategoriesAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category_chooser, container, false);

        mAdapter = new CategoriesAdapter(null, this, true);
        RecyclerView listView = (RecyclerView) rootView.findViewById(R.id.lv_list);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView.setLayoutManager(new GridLayoutManager(getContext(), 3)); //TODO replace magic number
        listView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onCategoryClicked(int categoryId) {
        Toast.makeText(getContext(), String.valueOf(categoryId), Toast.LENGTH_SHORT).show();

        ContentValues values = new ContentValues();

        values.put("category_id", 1);
        values.put("value", 1100);
        values.put("note", "NewEntry");

        Calendar cal = Calendar.getInstance();
        values.put("date", cal.getTimeInMillis() / 1000);

        Uri uri = getContext().getContentResolver().insert(SpendingsTable.CONTENT_URI, values);
        if (uri != null) {
            Log.d(TAG, "Success");

            Cursor c = getContext().getContentResolver().query(
                    SpendingsTable.CONTENT_URI,
                    null, null, null, null
            );

            if (c != null) {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    Log.d(TAG, "cat_id: " + c.getInt(c.getColumnIndex(SpendingsTable.FIELD_CATEGORY_ID)));
                    Log.d(TAG, "value: " + c.getFloat(c.getColumnIndex(SpendingsTable.FIELD_VALUE)));
                    Log.d(TAG, "note: " + c.getString(c.getColumnIndex(SpendingsTable.FIELD_NOTE)));
                    Log.d(TAG, "date: " + c.getLong(c.getColumnIndex(SpendingsTable.FIELD_DATE)));
                    c.moveToNext();
                }
                c.close();
            }

//                    getLoaderManager().restartLoader(SPENDINGS_LOADER, null, CategoryDetailsFragment.this);
            getActivity().finish();
        } else {
            Log.d(TAG, "Error, uri is null after instert");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(CATEGORY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getContext(),
                MainCategoriesTable.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
