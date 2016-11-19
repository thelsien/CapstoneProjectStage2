package apps.nanodegree.thelsien.capstone;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Currency;

import apps.nanodegree.thelsien.capstone.data.IncomesTable;
import apps.nanodegree.thelsien.capstone.data.IncomesTableConfig;
import apps.nanodegree.thelsien.capstone.data.MainCategoriesTable;
import apps.nanodegree.thelsien.capstone.data.MainCategoriesTableConfig;
import apps.nanodegree.thelsien.capstone.data.SpendingsTable;
import apps.nanodegree.thelsien.capstone.data.SpendingsTableConfig;

/**
 * Created by frodo on 2016. 11. 10..
 */
public class AddEditEntryFragment extends Fragment {

    public static final String ARGUMENT_ENTRY_URI = "entry_uri";
    public static final String ARGUMENT_CATEGORY_ID = "category_id";
    public static final String ARGUMENT_IS_INCOME = "is_income";

    private static final String TAG = AddEditEntryFragment.class.getSimpleName();

    private Uri mUri;
    private int mCategoryId;
    private int mEntryId = -1;
    private boolean mIsIncome;
    private EditText mValueEditText;
    private EditText mNoteEditText;
    private TextInputLayout mValueTextInputLayout;
    private Button mChooseCategoryButton;
    private TextView mTitleTextView;
    private TextView mCurrencyTextView;

    public static AddEditEntryFragment getInstance(Uri uri, int categoryId, boolean isIncome) {
        AddEditEntryFragment f = new AddEditEntryFragment();

        Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_ENTRY_URI, uri);
        args.putInt(ARGUMENT_CATEGORY_ID, categoryId);
        args.putBoolean(ARGUMENT_IS_INCOME, isIncome);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_edit_entry, container, false);

        Bundle args = getArguments();
        mUri = args.getParcelable(ARGUMENT_ENTRY_URI);
        mCategoryId = args.getInt(ARGUMENT_CATEGORY_ID, -1);
        mIsIncome = args.getBoolean(ARGUMENT_IS_INCOME, false);
        mValueEditText = (EditText) rootView.findViewById(R.id.et_value);
        mNoteEditText = (EditText) rootView.findViewById(R.id.et_note);
        mChooseCategoryButton = (Button) rootView.findViewById(R.id.btn_choose_category);
        mValueTextInputLayout = ((TextInputLayout) rootView.findViewById(R.id.til_value));
        mTitleTextView = ((TextView) rootView.findViewById(R.id.tv_title));
        mCurrencyTextView = ((TextView) rootView.findViewById(R.id.tv_currency));
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getContext(), android.R.color.white));

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurrencyTextView.setText(Currency.getInstance(
                PreferenceManager.getDefaultSharedPreferences(getContext())
                        .getString(getString(R.string.prefs_current_currency_key),
                                getString(R.string.default_currency))).getSymbol()
        );

        if (!mIsIncome) {
            if (mUri != null) {
                //When editing a category's entry
                setupEditCategoryEntry();
            } else if (mCategoryId != -1) {
                //when adding a new entry from a category screen.
                setupAddNewEntryToSpecificCategory();
            } else { //when adding a new element from the main screen
                setupAddNewEntry();
            }
        } else {
            mValueTextInputLayout.setHint(getString(R.string.add_edit_incomes_edit_text_hint));

            if (mUri != null) {
                //when editing an income
                setupEditingIncomeEntry();
            } else {
                //when adding new income
                setupAddNewIncomeEntry();
            }
        }

        return rootView;
    }

    private void setupAddNewIncomeEntry() {
        mTitleTextView.setText(R.string.add_income_title);
        mChooseCategoryButton.setText(R.string.add_edit_button_add_income);
        mChooseCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                IncomesTableConfig config = new IncomesTableConfig();

                config.value = Float.valueOf(mValueEditText.getText().toString().trim());
                config.note = mNoteEditText.getText().toString().trim();
                config.date = cal.getTimeInMillis() / 1000;

                Uri uri = getContext().getContentResolver().insert(IncomesTable.CONTENT_URI, IncomesTable.getContentValues(config, false));
                if (uri != null) {
                    Log.d(TAG, "Success");

                    Utility.notifyThroughContentResolver(getContext());
                    Utility.updateWidgets(getContext());

                    getActivity().finish();
                } else {
                    Log.d(TAG, "Error, uri is null after insert");
                }
            }
        });
    }

    private void setupEditingIncomeEntry() {
        mTitleTextView.setText(R.string.edit_incomes_title);
        Cursor c = getContext().getContentResolver().query(
                mUri,
                null,
                null,
                null,
                null
        );

        if (c != null) {
            c.moveToFirst();

            mValueEditText.setText(String.valueOf(c.getFloat(c.getColumnIndex(IncomesTable.FIELD_VALUE))));
            mNoteEditText.setText(c.getString(c.getColumnIndex(IncomesTable.FIELD_NOTE)));

            c.close();
        }

        mChooseCategoryButton.setVisibility(View.GONE);
    }

    private void setupAddNewEntry() {
        mChooseCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                        .addToBackStack("other_fragment")
                        .replace(R.id.category_add_edit_container, CategoryChooserFragment.getInstance(-1, mEntryId, Float.valueOf(mValueEditText.getText().toString().trim()), mNoteEditText.getText().toString().trim()))
                        .commit();
            }
        });
    }

    private void setupAddNewEntryToSpecificCategory() {
        Cursor c = getContext().getContentResolver().query(MainCategoriesTableConfig.getUriCategoryWithId(mCategoryId), null, null, null, null);
        if (c != null) {
            c.moveToFirst();
            mChooseCategoryButton.setText(String.format(getString(R.string.add_edit_button_save_to_category), getString(c.getInt(c.getColumnIndex(MainCategoriesTable.FIELD_NAME_RES)))));
            c.close();
        }

        mChooseCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                SpendingsTableConfig config = new SpendingsTableConfig();

                config.categoryId = mCategoryId;
                config.value = Float.valueOf(mValueEditText.getText().toString().trim());
                config.note = mNoteEditText.getText().toString().trim();
                config.date = cal.getTimeInMillis() / 1000;

                Uri uri = getContext().getContentResolver().insert(SpendingsTable.CONTENT_URI, SpendingsTable.getContentValues(config, false));
                if (uri != null) {
                    Log.d(TAG, "Success");

                    Utility.notifyThroughContentResolver(getContext());
                    Utility.updateWidgets(getContext());

                    getActivity().finish();
                } else {
                    Log.d(TAG, "Error, uri is null after insert");
                }
            }
        });
    }

    private void setupEditCategoryEntry() {
        mTitleTextView.setText(R.string.edit_spendings_title);
        mEntryId = Integer.parseInt(mUri.getLastPathSegment());

        Cursor c = getContext().getContentResolver().query(
                mUri,
                null,
                null,
                null,
                null
        );

        if (c != null) {
            c.moveToFirst();

            mValueEditText.setText(String.valueOf(c.getFloat(c.getColumnIndex(SpendingsTable.FIELD_VALUE))));
            mNoteEditText.setText(c.getString(c.getColumnIndex(SpendingsTable.FIELD_NOTE)));
            mChooseCategoryButton.setText(R.string.add_edit_button_change_category);
            mCategoryId = c.getInt(c.getColumnIndex(SpendingsTable.FIELD_CATEGORY_ID));

            mChooseCategoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                            .addToBackStack("other_fragment")
                            .replace(R.id.category_add_edit_container, CategoryChooserFragment.getInstance(
                                    mCategoryId,
                                    mEntryId,
                                    Float.valueOf(mValueEditText.getText().toString().trim()),
                                    mNoteEditText.getText().toString().trim())
                            )
                            .commit();
                }
            });

            c.close();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (mUri != null) {
            menu.add(Menu.NONE, 1, 100, R.string.menu_delete)
                    .setIcon(R.drawable.ic_clear_black_48dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            menu.add(Menu.NONE, 2, 200, R.string.menu_save)
                    .setIcon(R.drawable.ic_golf_course_black_48dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                deleteEntryFromDB(
                        mIsIncome ? IncomesTable.CONTENT_URI : SpendingsTable.CONTENT_URI,
                        mIsIncome ? IncomesTable.FIELD_ID : SpendingsTable.FIELD_ID
                );
                break;
            case 2:
                saveEntryToDB(
                        mIsIncome ? IncomesTable.CONTENT_URI : SpendingsTable.CONTENT_URI,
                        mIsIncome ? IncomesTable.FIELD_ID : SpendingsTable.FIELD_ID
                );
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveEntryToDB(Uri uri, String idColumn) {
        Cursor c = getContext().getContentResolver().query(
                mUri,
                null,
                null,
                null,
                null
        );

        int rowsUpdated;
        SpendingsTableConfig spendingsConfig = new SpendingsTableConfig();
        IncomesTableConfig incomesConfig = new IncomesTableConfig();

        if (c != null) {
            c.moveToFirst();

            if (!mIsIncome) {
                spendingsConfig.id = Integer.valueOf(mUri.getLastPathSegment());
                spendingsConfig.value = Float.valueOf(mValueEditText.getText().toString());
                spendingsConfig.note = mNoteEditText.getText().toString();
                spendingsConfig.date = c.getLong(c.getColumnIndex(SpendingsTable.FIELD_DATE));
                spendingsConfig.categoryId = c.getInt(c.getColumnIndex(SpendingsTable.FIELD_CATEGORY_ID));
            } else {
                incomesConfig.id = Integer.valueOf(mUri.getLastPathSegment());
                incomesConfig.value = Float.valueOf(mValueEditText.getText().toString());
                incomesConfig.note = mNoteEditText.getText().toString();
                incomesConfig.date = c.getLong(c.getColumnIndex(SpendingsTable.FIELD_DATE));
            }

            c.close();
        }

        ContentValues contentValues;

        if (mIsIncome) {
            contentValues = IncomesTable.getContentValues(incomesConfig, true);
        } else {
            contentValues = SpendingsTable.getContentValues(spendingsConfig, true);
        }

        rowsUpdated = getContext().getContentResolver().update(
                uri,
                contentValues,
                idColumn + " = ?",
                new String[]{mUri.getLastPathSegment()}
        );

        if (rowsUpdated == 1) {
            Toast.makeText(getContext(), R.string.add_edit_save_successful, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.add_edit_save_delete_error, Toast.LENGTH_SHORT).show();
        }

        Utility.notifyThroughContentResolver(getContext());
        Utility.updateWidgets(getContext());

        getActivity().finish();
    }

    private void deleteEntryFromDB(Uri contentUri, String columnId) {
        int deletedRows = getContext().getContentResolver().delete(
                contentUri,
                columnId + " = ?",
                new String[]{mUri.getLastPathSegment()}
        );

        if (deletedRows == 1) {
            Toast.makeText(getContext(), R.string.add_edit_delete_successful, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.add_edit_save_delete_error, Toast.LENGTH_SHORT).show();
        }

        Utility.notifyThroughContentResolver(getContext());
        Utility.updateWidgets(getContext());

        getActivity().finish();
    }
}
