package apps.nanodegree.thelsien.capstone;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;

import java.util.Calendar;

import apps.nanodegree.thelsien.capstone.asynctasks.CurrencyChangeAsyncTask;
import apps.nanodegree.thelsien.capstone.asynctasks.ExportDataToCSVAsyncTask;
import apps.nanodegree.thelsien.capstone.asynctasks.ImportDataFromCSVAsyncTask;
import apps.nanodegree.thelsien.capstone.data.MainCategoriesTable;

/**
 * Created by frodo on 2016. 11. 13..
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener, ActivityCompat.OnRequestPermissionsResultCallback, DatePickerDialog.OnDateSetListener, CurrencyChangeAsyncTask.OnCurrenyChangeListener {

    public static final int FILE_CHOOSER_REQUEST_CODE = 1000;

    public static final int WRITE_STORAGE_REQUEST_CODE = 100;
    public static final int READ_STORAGE_REQUEST_CODE = 200;

    private static final String TAG = SettingsFragment.class.getSimpleName();

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(R.string.loading_dialog_title);
        mProgressDialog.setMessage(getString(R.string.loading_dialog_message));
        mProgressDialog.setCancelable(false);

        addPreferencesFromResource(R.xml.main_preferences);
        findPreference(getString(R.string.prefs_export_data_to_csv)).setOnPreferenceClickListener(this);
        findPreference(getString(R.string.prefs_import_data_from_csv)).setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.prefs_current_currency_key))) {
            new CurrencyChangeAsyncTask(getActivity(), this).execute(
                    sharedPreferences.getString(getString(R.string.prefs_source_currency_key), getString(R.string.default_currency)),
                    sharedPreferences.getString(getString(R.string.prefs_current_currency_key), getString(R.string.default_currency))
            );
            mProgressDialog.show();
        } else if (key.equals(getString(R.string.prefs_time_interval))) {
            String newValue = sharedPreferences.getString(getString(R.string.prefs_time_interval), getString(R.string.default_time_interval_month));
            if (newValue.equals(getString(R.string.time_interval_custom))) {
                Calendar cal = Calendar.getInstance();
                DatePickerDialog.newInstance(
                        this,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                ).show(getFragmentManager(), "date_interval_picker");
            } else {
                getActivity().getContentResolver().notifyChange(MainCategoriesTable.CONTENT_URI, null);
            }
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.prefs_export_data_to_csv))) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE_REQUEST_CODE);

            return true;
        } else if (preference.getKey().equals(getString(R.string.prefs_import_data_from_csv))) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_STORAGE_REQUEST_CODE);

            return true;
        }

        return false;
    }

    private void requestPermission(final String permission, final int requestCode) {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), permission);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case WRITE_STORAGE_REQUEST_CODE:
                    new ExportDataToCSVAsyncTask(getActivity()).execute();
                    mProgressDialog.show();
                    break;
                case READ_STORAGE_REQUEST_CODE:
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("file/*");
                    startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE);
                    break;
            }
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {

                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.permission_explanation_dialog_title)
                        .setMessage(R.string.permission_explanation_message)
                        .setPositiveButton(R.string.permission_explanation_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{permission},
                                        requestCode);
                            }
                        })
                        .create();
                dialog.show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{permission},
                        requestCode);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            new ImportDataFromCSVAsyncTask(getActivity()).execute(data.getData());
            mProgressDialog.show();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        Log.d(TAG, String.valueOf(year) + " " + String.valueOf(monthOfYear + 1) + " " + String.valueOf(dayOfMonth) + " " + String.valueOf(yearEnd) + " " + String.valueOf(monthOfYearEnd + 1) + " " + String.valueOf(dayOfMonthEnd));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit()
                .putInt(getString(R.string.custom_interval_start_year), year)
                .putInt(getString(R.string.custom_interval_start_month), monthOfYear)
                .putInt(getString(R.string.custom_interval_start_day), dayOfMonth)
                .putInt(getString(R.string.custom_interval_end_year), yearEnd)
                .putInt(getString(R.string.custom_interval_end_month), monthOfYearEnd)
                .putInt(getString(R.string.custom_interval_end_day), dayOfMonthEnd)
                .commit();

        getActivity().getContentResolver().notifyChange(MainCategoriesTable.CONTENT_URI, null);
    }

    @Override
    public void onCurrencyChanged(boolean isSuccessful, String newCurrency) {
        mProgressDialog.dismiss();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (isSuccessful) {
            prefs.edit()
                    .putString(getString(R.string.prefs_source_currency_key), newCurrency)
                    .commit();
        } else {
            String sourceCurrency = prefs.getString(getString(R.string.prefs_source_currency_key), getString(R.string.default_currency));
            prefs.edit()
                    .putString(getString(R.string.prefs_current_currency_key), sourceCurrency)
                    .commit();
        }

        int messageResId = isSuccessful ? R.string.message_currency_changed_successfuly : R.string.message_currency_change_error;

        Toast.makeText(getActivity(), messageResId, Toast.LENGTH_SHORT).show();
    }
}
