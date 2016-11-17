package apps.nanodegree.thelsien.capstone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import apps.nanodegree.thelsien.capstone.asynctasks.ExportDataToCSVAsyncTask;
import apps.nanodegree.thelsien.capstone.asynctasks.ImportDataFromCSVAsyncTask;

import static apps.nanodegree.thelsien.capstone.SettingsFragment.FILE_CHOOSER_REQUEST_CODE;
import static apps.nanodegree.thelsien.capstone.SettingsFragment.READ_STORAGE_REQUEST_CODE;
import static apps.nanodegree.thelsien.capstone.SettingsFragment.WRITE_STORAGE_REQUEST_CODE;

/**
 * Created by frodo on 2016. 11. 13..
 */

public class SettingsActivity extends Activity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
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
                            new ExportDataToCSVAsyncTask(this).execute();
                        } else {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("file/*");
                            startActivityForResult(intent, FILE_CHOOSER_REQUEST_CODE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            new ImportDataFromCSVAsyncTask(this).execute(data.getData());
        }
    }
}
