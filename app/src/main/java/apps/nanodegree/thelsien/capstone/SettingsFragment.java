package apps.nanodegree.thelsien.capstone;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import apps.nanodegree.thelsien.capstone.asynctasks.CurrencyChangeAsyncTask;

/**
 * Created by frodo on 2016. 11. 13..
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int WRITE_STORAGE_REQUEST_CODE = 100;
    private static final int READ_STORAGE_REQUEST_CODE = 200;
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
                //TODO export to csv
                Log.d(TAG, "Permission is granted for CSV export");
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    //TODO show an explanation then request again
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_STORAGE_REQUEST_CODE);
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
                //TODO import from csv
                Log.d(TAG, "Permission is granted for CSV import");
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    //TODO show an explanation then request again
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            READ_STORAGE_REQUEST_CODE);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case READ_STORAGE_REQUEST_CODE:
            case WRITE_STORAGE_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    boolean isPermissionsGranted = true;
                    for (int permission : grantResults) {
                        isPermissionsGranted = isPermissionsGranted && (permission != 0);
                    }

                    if (isPermissionsGranted) {
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        if (requestCode == WRITE_STORAGE_REQUEST_CODE) {
                            //TODO csv export
                            Log.d(TAG, "Permissions are granted for CSV export");
                        } else {
                            //TODO csv export
                            Log.d(TAG, "Permissions are granted for CSV import");
                        }
                    }
                } else {
                    //TODO notify the user that he can't use this unless permission is granted
                    Log.e(TAG, "Permissions are denied for export or import CSV");
                }
                break;
            }
        }
    }
}
