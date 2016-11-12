package apps.nanodegree.thelsien.capstone;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
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
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!mIsIncome) {
            if (mUri != null) {
                //When editing a category's entry
                mTitleTextView.setText("Edit spending entry");
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

                    mValueEditText.setText(String.valueOf(c.getInt(c.getColumnIndex(SpendingsTable.FIELD_VALUE))));
                    mNoteEditText.setText(c.getString(c.getColumnIndex(SpendingsTable.FIELD_NOTE)));
                    mChooseCategoryButton.setText("Change category");
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


            } else if (mCategoryId != -1) {
                //when adding a new entry from a category screen.
                Cursor c = getContext().getContentResolver().query(MainCategoriesTableConfig.getUriCategoryWithId(mCategoryId), null, null, null, null);
                c.moveToFirst();
                mChooseCategoryButton.setText("Save to " + c.getString(c.getColumnIndex(MainCategoriesTable.FIELD_NAME)) + " category");
                c.close();

                mChooseCategoryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), String.valueOf(mCategoryId), Toast.LENGTH_SHORT).show();

                        Calendar cal = Calendar.getInstance();
                        SpendingsTableConfig config = new SpendingsTableConfig();

                        config.categoryId = mCategoryId;
                        config.value = Float.valueOf(mValueEditText.getText().toString().trim());
                        config.note = mNoteEditText.getText().toString().trim();
                        config.date = cal.getTimeInMillis() / 1000;

                        Uri uri = getContext().getContentResolver().insert(SpendingsTable.CONTENT_URI, SpendingsTable.getContentValues(config, false));
                        if (uri != null) {
                            Log.d(TAG, "Success");

                            getActivity().finish();
                        } else {
                            Log.d(TAG, "Error, uri is null after insert");
                        }
                    }
                });
            } else { //when adding a new element from the main screen
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
        } else {
            mValueTextInputLayout.setHint("Income's value");

            if (mUri != null) {
                //when editing an income
                mTitleTextView.setText("Edit income");
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
            } else {
                //when adding new income
                mTitleTextView.setText("Add an income");
                mChooseCategoryButton.setText("Add to incomes");
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

                            getActivity().finish();
                        } else {
                            Log.d(TAG, "Error, uri is null after insert");
                        }
                    }
                });
            }
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (mUri != null) {
            menu.add(Menu.NONE, 1, 100, "Delete")
                    .setIcon(R.drawable.ic_close_dark)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            menu.add(Menu.NONE, 2, 200, "Save")
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
                Cursor c = getContext().getContentResolver().query(
                        mUri,
                        null,
                        null,
                        null,
                        null
                );

                c.moveToFirst();

                int rowsUpdated;
                if (!mIsIncome) {
                    SpendingsTableConfig config = new SpendingsTableConfig();
                    config.id = Integer.valueOf(mUri.getLastPathSegment());
                    config.value = Float.valueOf(mValueEditText.getText().toString());
                    config.note = mNoteEditText.getText().toString();
                    config.date = c.getLong(c.getColumnIndex(SpendingsTable.FIELD_DATE));
                    config.categoryId = c.getInt(c.getColumnIndex(SpendingsTable.FIELD_CATEGORY_ID));

                    rowsUpdated = getContext().getContentResolver().update(
                            SpendingsTable.CONTENT_URI,
                            SpendingsTable.getContentValues(config, true),
                            SpendingsTable.FIELD_ID + " = ?",
                            new String[]{mUri.getLastPathSegment()}
                    );
                } else {
                    IncomesTableConfig config = new IncomesTableConfig();
                    config.id = Integer.valueOf(mUri.getLastPathSegment());
                    config.value = Float.valueOf(mValueEditText.getText().toString());
                    config.note = mNoteEditText.getText().toString();
                    config.date = c.getLong(c.getColumnIndex(SpendingsTable.FIELD_DATE));

                    rowsUpdated = getContext().getContentResolver().update(
                            IncomesTable.CONTENT_URI,
                            IncomesTable.getContentValues(config, true),
                            IncomesTable.FIELD_ID + " = ?",
                            new String[]{mUri.getLastPathSegment()}
                    );
                }

                c.close();

                if (rowsUpdated == 1) {
                    Toast.makeText(getContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Zero or more than 1 row was affected", Toast.LENGTH_SHORT).show();
                }

                getActivity().finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteEntryFromDB(Uri contentUri, String columnId) {
        int deletedRows = getContext().getContentResolver().delete(
                contentUri,
                columnId + " = ?",
                new String[]{mUri.getLastPathSegment()}
        );

        if (deletedRows == 1) {
            Toast.makeText(getContext(), "Deleted entry", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Zero or more than 1 row was affected.", Toast.LENGTH_SHORT).show();
        }

        getActivity().finish();
    }
}
