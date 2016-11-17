package apps.nanodegree.thelsien.capstone;

import android.content.Intent;
import android.database.Cursor;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import apps.nanodegree.thelsien.capstone.adapters.CategoryEntryAdapter;
import apps.nanodegree.thelsien.capstone.data.IncomesTable;
import apps.nanodegree.thelsien.capstone.data.IncomesTableConfig;
import apps.nanodegree.thelsien.capstone.data.MainCategoriesTable;
import apps.nanodegree.thelsien.capstone.data.SpendingsTable;
import apps.nanodegree.thelsien.capstone.data.SpendingsTableConfig;

/**
 * Created by frodo on 2016. 11. 08..
 */

public class CategoryDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CategoryEntryAdapter.OnEntryClickedListener {

    public static final String TAG = CategoryDetailsFragment.class.getSimpleName();

    private static final int SPENDINGS_LOADER = 1;

    private boolean mIsShouldShowIncome;
    private int mCategoryId;
    private RecyclerView mRecyclerView;
    private CategoryEntryAdapter mAdapter;
    private TextView mEmptyView;
    private ImageView mCategoryIconView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        mCategoryId = arguments.getInt(CategoryDetailsActivity.INTENT_EXTRA_CATEGORY_ID);
        mIsShouldShowIncome = arguments.getBoolean(CategoryDetailsActivity.INTENT_EXTRA_IS_INCOME, false);

        View rootView = inflater.inflate(R.layout.fragment_category_details, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.lv_list);
        mEmptyView = (TextView) rootView.findViewById(R.id.tv_empty_list);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new CategoryEntryAdapter(getContext(), null, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        rootView.findViewById(R.id.fab_add_to_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddEditEntryActivity.class);
                if (!mIsShouldShowIncome) {
                    intent.putExtra(AddEditEntryFragment.ARGUMENT_CATEGORY_ID, mCategoryId);
                }
                intent.putExtra(AddEditEntryFragment.ARGUMENT_IS_INCOME, mIsShouldShowIncome);

                getActivity().startActivity(intent);
            }
        });

        mCategoryIconView = (ImageView) rootView.findViewById(R.id.iv_icon);

        if (!mIsShouldShowIncome) {
            getIconAndNameForSpendingCategory();
        } else {
            mCategoryIconView.setImageResource(R.drawable.ic_golf_course_black_48dp);
            mCategoryIconView.setContentDescription(String.format(getString(R.string.content_description_category_icon), getString(R.string.incomes_title)));
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.incomes_title));
        }

        return rootView;
    }

    private void getIconAndNameForSpendingCategory() {
        Cursor c = getContext().getContentResolver().query(
                MainCategoriesTable.CONTENT_URI,
                new String[]{MainCategoriesTable.FIELD_ICON_RES, MainCategoriesTable.FIELD_NAME_RES},
                MainCategoriesTable.FIELD__ID + " = ?",
                new String[]{String.valueOf(mCategoryId)},
                null
        );
        if (c != null) {
            c.moveToFirst();

            mCategoryIconView.setImageResource(c.getInt(c.getColumnIndex(MainCategoriesTable.FIELD_ICON_RES)));
            mCategoryIconView.setContentDescription(String.format(getString(R.string.content_description_category_icon), getString(c.getInt(c.getColumnIndex(MainCategoriesTable.FIELD_NAME_RES)))));
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(c.getInt(c.getColumnIndex(MainCategoriesTable.FIELD_NAME_RES))));

            c.close();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(SPENDINGS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String whereClause;
        String[] whereParams;
        long startTime = Utility.getStartTimeForQuery(getContext());
        long endTime = Utility.getEndTimeForQuery(getContext());

        if (mIsShouldShowIncome) {
            whereClause = IncomesTable.FIELD_DATE + " < ? AND " +
                    IncomesTable.FIELD_DATE + " >= ?";
            whereParams = new String[]{
                    String.valueOf(endTime),
                    String.valueOf(startTime)};

            return new CursorLoader(
                    getContext(),
                    IncomesTable.CONTENT_URI,
                    null,
                    whereClause,
                    whereParams,
                    IncomesTable.FIELD_DATE + " ASC"
            );
        }

        whereClause = SpendingsTable.FIELD_CATEGORY_ID + " = ? AND " +
                SpendingsTable.FIELD_DATE + " < ? AND " +
                SpendingsTable.FIELD_DATE + " >= ?";
        whereParams = new String[]{
                String.valueOf(mCategoryId),
                String.valueOf(endTime),
                String.valueOf(startTime)};

        return new CursorLoader(
                getContext(),
                SpendingsTable.CONTENT_URI,
                null,
                whereClause,
                whereParams,
                SpendingsTable.FIELD_DATE + " ASC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if (data.getCount() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
        mRecyclerView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEntryClicked(int entryId) {
        Intent intent = new Intent(getContext(), AddEditEntryActivity.class);
        intent.setData(mIsShouldShowIncome ? IncomesTableConfig.getUriForSingleIncome(entryId) : SpendingsTableConfig.getUriForSingleEntry(entryId));
        intent.putExtra(AddEditEntryFragment.ARGUMENT_IS_INCOME, mIsShouldShowIncome);

        startActivity(intent);
    }
}
