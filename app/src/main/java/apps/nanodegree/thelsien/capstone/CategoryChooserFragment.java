package apps.nanodegree.thelsien.capstone;

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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Calendar;

import apps.nanodegree.thelsien.capstone.adapters.CategoriesAdapter;
import apps.nanodegree.thelsien.capstone.data.MainCategoriesTable;
import apps.nanodegree.thelsien.capstone.data.SpendingsTable;
import apps.nanodegree.thelsien.capstone.data.SpendingsTableConfig;

/**
 * Created by frodo on 2016. 11. 10..
 */

public class CategoryChooserFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, CategoriesAdapter.OnCategoryClickListener {

    private static final String TAG = CategoryChooserFragment.class.getSimpleName();
    private static final int CATEGORY_LOADER = 2;
    private static final String ARGUMENTS_VALUE = "chooser_value";
    private static final String ARGUMENTS_NOTE = "chooser_note";
    private static final String ARGUMENTS_CATEGORY_ID = "chooser_category_id";
    private static final String ARGUMENTS_ENTRY_ID = "chooser_entry_id";

    private CategoriesAdapter mAdapter;

    private InterstitialAd mInterstitialAd;

    public static CategoryChooserFragment getInstance(int categoryId, int entryId, float value, String note) {
        CategoryChooserFragment f = new CategoryChooserFragment();

        Bundle args = new Bundle();
        args.putInt(ARGUMENTS_CATEGORY_ID, categoryId);
        args.putFloat(ARGUMENTS_VALUE, value);
        args.putString(ARGUMENTS_NOTE, note);
        args.putInt(ARGUMENTS_ENTRY_ID, entryId);

        f.setArguments(args);

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category_chooser, container, false);

        Utility.trackScreen(getContext(), "category_chooser_fragment");

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                getActivity().finish();
            }
        });

        requestNewInterstitial();

        mAdapter = new CategoriesAdapter(getContext(), null, this, false);
        RecyclerView listView = (RecyclerView) rootView.findViewById(R.id.lv_list);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.main_grid_columns)));
        listView.setAdapter(mAdapter);

        return rootView;
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("DF4CD421E5ECF2C5B912A0051F1B7BC6")
                .addTestDevice("611bd81b4df23a35")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onCategoryClicked(int categoryId) {
        Calendar cal = Calendar.getInstance();
        SpendingsTableConfig config = new SpendingsTableConfig();
        Bundle args = getArguments();

        config.categoryId = categoryId;
        config.value = args.getFloat(ARGUMENTS_VALUE);
        config.note = args.getString(ARGUMENTS_NOTE);
        config.date = cal.getTimeInMillis() / 1000;

        if (args.getInt(ARGUMENTS_CATEGORY_ID) == -1) {
            Uri uri = getContext().getContentResolver().insert(SpendingsTable.CONTENT_URI, SpendingsTable.getContentValues(config, false));
            if (uri != null) {
                Log.d(TAG, "Success");
            } else {
                Log.d(TAG, "Error, uri is null after insert");
            }
        } else {
            int rowsUpdated = getContext().getContentResolver().update(SpendingsTable.CONTENT_URI, SpendingsTable.getContentValues(config, false), SpendingsTable.FIELD_ID + " = ?", new String[]{String.valueOf(args.getInt(ARGUMENTS_ENTRY_ID))});

            if (rowsUpdated == 1) {
                Toast.makeText(getContext(), R.string.category_chooser_entry_updated, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.add_edit_save_delete_error, Toast.LENGTH_SHORT).show();
            }
        }

        Utility.notifyThroughContentResolver(getContext());
        Utility.updateWidgets(getContext());
        Utility.trackEvent(getContext(), "button", "click", "category_chosen_for_entry");

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            getActivity().finish();
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
