package apps.nanodegree.thelsien.capstone;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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

import apps.nanodegree.thelsien.capstone.asynctasks.CurrencyChangeAsyncTask;
import apps.nanodegree.thelsien.capstone.asynctasks.ExportDataToCSVAsyncTask;
import apps.nanodegree.thelsien.capstone.asynctasks.ImportDataFromCSVAsyncTask;

/**
 * Created by frodo on 2016. 11. 13..
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    public static final int FILE_CHOOSER_REQUEST_CODE = 1000;

    public static final int WRITE_STORAGE_REQUEST_CODE = 100;
    public static final int READ_STORAGE_REQUEST_CODE = 200;

    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            (new CurrencyChangeAsyncTask(getActivity())).execute(sharedPreferences.getString(getString(R.string.prefs_source_currency_key), "USD"), sharedPreferences.getString(getString(R.string.prefs_current_currency_key), "USD"));
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.prefs_export_data_to_csv))) {
            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                new ExportDataToCSVAsyncTask(getActivity()).execute();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.permission_explanation_dialog_title)
                            .setMessage(R.string.permission_explanation_message)
                            .setPositiveButton(R.string.permission_explanation_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                            WRITE_STORAGE_REQUEST_CODE);
                                }
                            })
                            .create();
                    dialog.show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_STORAGE_REQUEST_CODE);
                }
            }
            return true;
        } else if (preference.getKey().equals(getString(R.string.prefs_import_data_from_csv))) {
            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.permission_explanation_dialog_title)
                            .setMessage(R.string.permission_explanation_message)
                            .setPositiveButton(R.string.permission_explanation_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            READ_STORAGE_REQUEST_CODE);
                                }
                            })
                            .create();
                    dialog.show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_STORAGE_REQUEST_CODE);
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            new ImportDataFromCSVAsyncTask(getActivity()).execute(data.getData());
        }
    }
}
