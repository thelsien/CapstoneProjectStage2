package apps.nanodegree.thelsien.capstone;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import apps.nanodegree.thelsien.capstone.adapters.CategoriesAdapter;
import apps.nanodegree.thelsien.capstone.data.MainCategoriesTable;

/**
 * Created by frodo on 2016. 11. 07..
 */

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CategoriesAdapter.OnCategoryClickListener {

    private static final String[] CATEGORY_COLUMNS = {
            MainCategoriesTable.FIELD__ID,
            MainCategoriesTable.FIELD_NAME,
            MainCategoriesTable.FIELD_ICON_RES
    };
    private static final int CATEGORIES_LOADER = 0;

    private RecyclerView mRecyclerView;
    private Uri mUri;
    private CategoriesAdapter mCategoryAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.lv_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mCategoryAdapter = new CategoriesAdapter(null, this, false);
        mRecyclerView.setAdapter(mCategoryAdapter);

        rootView.findViewById(R.id.fab_open_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddEditEntryActivity.class);

                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(CATEGORIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getContext(),
                MainCategoriesTable.CONTENT_URI,
                CATEGORY_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCategoryAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCategoryAdapter.swapCursor(null);
    }

    @Override
    public void onCategoryClicked(int categoryId) {
        Intent intent = new Intent(getContext(), CategoryDetailsActivity.class);
        intent.putExtra(CategoryDetailsActivity.INTENT_EXTRA_CATEGORY_ID, categoryId);

        startActivity(intent);
    }
}
