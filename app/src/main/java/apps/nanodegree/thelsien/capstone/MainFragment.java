package apps.nanodegree.thelsien.capstone;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.text.NumberFormat;

import apps.nanodegree.thelsien.capstone.adapters.CategoriesAdapter;
import apps.nanodegree.thelsien.capstone.data.MainCategoriesTable;

/**
 * Created by frodo on 2016. 11. 07..
 */

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CategoriesAdapter.OnCategoryClickListener, View.OnClickListener {

    private static final String[] CATEGORY_COLUMNS = {
            MainCategoriesTable.FIELD__ID,
            MainCategoriesTable.FIELD_NAME_RES,
            MainCategoriesTable.FIELD_ICON_RES
    };
    private static final int CATEGORIES_LOADER = 0;

    private RecyclerView mRecyclerView;
    private CategoriesAdapter mCategoryAdapter;

    private boolean mIsFabOpen = false;
    private FloatingActionButton mMainFab;
    private FloatingActionButton mSpendingFab;
    private FloatingActionButton mIncomeFab;

    private Animation mRotateBackward;
    private Animation mRotateForward;
    private Animation mFabOpen;
    private Animation mFabClose;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        Utility.trackScreen(getContext(), "main_categories");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("is_fab_open", mIsFabOpen);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mFabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        mFabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        mRotateForward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_forward);
        mRotateBackward = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_backward);

        mMainFab = (FloatingActionButton) rootView.findViewById(R.id.fab_open_close);
        mSpendingFab = (FloatingActionButton) rootView.findViewById(R.id.fab_add_spending);
        mIncomeFab = (FloatingActionButton) rootView.findViewById(R.id.fab_add_income);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.lv_list);
        mCategoryAdapter = new CategoriesAdapter(getContext(), null, this, true);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), getContext().getResources().getInteger(R.integer.main_grid_columns)));
        mRecyclerView.setAdapter(mCategoryAdapter);

        mMainFab.setContentDescription(getString(R.string.fab_open));
        mSpendingFab.setContentDescription(getString(R.string.fab_spending));
        mIncomeFab.setContentDescription(getString(R.string.fab_income));

        mMainFab.setOnClickListener(this);
        mSpendingFab.setOnClickListener(this);
        mIncomeFab.setOnClickListener(this);

        return rootView;
    }

    public void animateFABOpeningClosing() {
        if (mIsFabOpen) {
            mMainFab.setContentDescription(getString(R.string.fab_open));
            mMainFab.startAnimation(mRotateBackward);
            mSpendingFab.startAnimation(mFabClose);
            mIncomeFab.startAnimation(mFabClose);
            mSpendingFab.setClickable(false);
            mIncomeFab.setClickable(false);
            mIsFabOpen = false;
        } else {
            mMainFab.setContentDescription(getString(R.string.fab_close));
            mMainFab.startAnimation(mRotateForward);
            mSpendingFab.startAnimation(mFabOpen);
            mIncomeFab.startAnimation(mFabOpen);
            mSpendingFab.setClickable(true);
            mIncomeFab.setClickable(true);
            mIsFabOpen = true;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_incomes_list:
                intent = new Intent(getContext(), CategoryDetailsActivity.class);
                intent.putExtra(CategoryDetailsActivity.INTENT_EXTRA_IS_INCOME, true);

                startActivity(intent);
                break;
            case R.id.menu_settings:
                intent = new Intent(getContext(), SettingsActivity.class);

                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(CATEGORIES_LOADER, null, this);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("is_fab_open")) {
                mIsFabOpen = !savedInstanceState.getBoolean("is_fab_open");
                animateFABOpeningClosing();
            }
        }

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
        refreshValuesInToolbar();
    }

    private void refreshValuesInToolbar() {
        long startDate = Utility.getStartTimeForQuery(getContext());
        long endDate = Utility.getEndTimeForQuery(getContext());

        float incomesSum = Utility.getIncomesSum(getContext());
        float spendingsSum = Utility.getSpendingsSum(getContext());

        TextView incomesTextView = (TextView) getView().findViewById(R.id.tv_incomes);
//        TextView incomesCurrencyView = (TextView) getView().findViewById(R.id.tv_incomes_currency);
        TextView spendingsTextView = (TextView) getView().findViewById(R.id.tv_spendings);
//        TextView spendingsCurrencyView = (TextView) getView().findViewById(R.id.tv_spendings_currency);
        TextView balanceTextView = (TextView) getView().findViewById(R.id.tv_balance);
        //TextView balanceCurrencyView = (TextView) getView().findViewById(R.id.tv_balance_currency);

        NumberFormat format = Utility.getValueFormatWithCurrency(getContext());
        incomesTextView.setText(format.format(incomesSum));
        spendingsTextView.setText(format.format(spendingsSum));
        balanceTextView.setText(format.format(incomesSum - spendingsSum));
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

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.fab_open_close:
                animateFABOpeningClosing();
                break;
            case R.id.fab_add_spending:
                Utility.trackEvent(getContext(), "button", "click", "add spending");
                animateFABOpeningClosing();
                intent = new Intent(getContext(), AddEditEntryActivity.class);
                startActivity(intent);
                break;
            case R.id.fab_add_income:
                Utility.trackEvent(getContext(), "button", "click", "add income");
                animateFABOpeningClosing();
                intent = new Intent(getContext(), AddEditEntryActivity.class);
                intent.putExtra(AddEditEntryFragment.ARGUMENT_IS_INCOME, true);
                startActivity(intent);
                break;
        }
    }
}
