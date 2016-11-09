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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import apps.nanodegree.thelsien.capstone.adapters.SpendingsAdapter;
import apps.nanodegree.thelsien.capstone.data.SpendingsTable;

/**
 * Created by frodo on 2016. 11. 08..
 */

public class CategoryDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = CategoryDetailsFragment.class.getSimpleName();

    private static final int SPENDINGS_LOADER = 1;

    private int mCategoryId;
    private RecyclerView mRecyclerView;
    private SpendingsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        mCategoryId = arguments.getInt(CategoryDetailsActivity.INTENT_EXTRA_CATEGORY_ID);

        View rootView = inflater.inflate(R.layout.fragment_category_details, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.lv_list);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        getActivity().setTitle(String.valueOf(mCategoryId));
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAdapter = new SpendingsAdapter(getContext(), null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        rootView.findViewById(R.id.fab_add_to_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO navigate to add screen, this logic will be there with some other magic
                ContentValues values = new ContentValues();

                values.put("category_id", mCategoryId);
                values.put("value", 990);
                values.put("note", "Almafa123");

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

                    getLoaderManager().restartLoader(SPENDINGS_LOADER, null, CategoryDetailsFragment.this);
                } else {
                    Log.d(TAG, "Error, uri is null after instert");
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(SPENDINGS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getContext(),
                SpendingsTable.CONTENT_URI,
                null,
                SpendingsTable.FIELD_CATEGORY_ID + " = ?",
                new String[]{String.valueOf(mCategoryId)},
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
