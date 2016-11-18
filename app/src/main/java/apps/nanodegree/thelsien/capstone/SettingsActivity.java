package apps.nanodegree.thelsien.capstone;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import static apps.nanodegree.thelsien.capstone.SettingsFragment.READ_STORAGE_REQUEST_CODE;
import static apps.nanodegree.thelsien.capstone.SettingsFragment.WRITE_STORAGE_REQUEST_CODE;

/**
 * Created by frodo on 2016. 11. 13..
 */

public class SettingsActivity extends Activity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private SettingsFragment mSettingsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mSettingsFragment = new SettingsFragment();
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, mSettingsFragment, "settings_fragment")
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getFragmentManager().putFragment(outState, "settings_fragment", mSettingsFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSettingsFragment = (SettingsFragment) getFragmentManager().getFragment(savedInstanceState, "settings_fragment");
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
                        isPermissionsGranted = isPermissionsGranted && (permission != -1);
                    }

                    if (isPermissionsGranted) {
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        if (requestCode == WRITE_STORAGE_REQUEST_CODE) {
                            mSettingsFragment.startExportData();
                        } else {
                            mSettingsFragment.startFileChooserForImport();
                        }
                    }
                } else {
                    Toast.makeText(this, R.string.permission_needed, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Permissions are denied for export or import CSV");
                }
                break;
            }
        }
    }
}
