package apps.nanodegree.thelsien.capstone.asynctasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import apps.nanodegree.thelsien.capstone.Utility;
import apps.nanodegree.thelsien.capstone.data.IncomesTable;
import apps.nanodegree.thelsien.capstone.data.IncomesTableConfig;
import apps.nanodegree.thelsien.capstone.data.SpendingsTable;
import apps.nanodegree.thelsien.capstone.data.SpendingsTableConfig;

/**
 * Created by frodo on 2016. 11. 14..
 */

public class ImportDataFromCSVAsyncTask extends AsyncTask<Uri, Void, Boolean> {

    private static final String TAG = ImportDataFromCSVAsyncTask.class.getSimpleName();

    private Context mContext;

    public ImportDataFromCSVAsyncTask(Context context) {
        super();

        mContext = context;
    }

    @Override
    protected Boolean doInBackground(Uri... uris) {
        Utility.deleteSpendingsAndIncomes(mContext);

        String filePath = getFilePath(uris[0]);
        File file = new File(filePath);

        Log.d(TAG, uris[0].toString());
        Log.d(TAG, "filePath: " + filePath);

        if (!file.exists()) {
            Log.d(TAG, "File does not exist.");
            return false;
        }

        try {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine(); //first line are the headers

            while ((line = reader.readLine()) != null) {
                Log.d(TAG, line);
                RowFromCSV rowData = new RowFromCSV(line);

                if (rowData.categoryId != -1) {
                    SpendingsTableConfig config = new SpendingsTableConfig();
                    config.categoryId = rowData.categoryId;
                    config.value = rowData.value;
                    config.note = rowData.note;
                    config.date = rowData.date;

                    mContext.getContentResolver().insert(SpendingsTable.CONTENT_URI, SpendingsTable.getContentValues(config, false));
                } else {
                    IncomesTableConfig config = new IncomesTableConfig();
                    config.value = rowData.value;
                    config.note = rowData.note;
                    config.date = rowData.date;

                    mContext.getContentResolver().insert(IncomesTable.CONTENT_URI, IncomesTable.getContentValues(config, false));
                }
            }

            reader.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private String getFilePath(Uri uri) {
        Cursor filePathCursor = mContext.getContentResolver().query(uri, null, null, null, null);
        String filePath;

        if (filePathCursor == null) {
            filePath = uri.getPath();
        } else {
            filePathCursor.moveToFirst();

            int idx = filePathCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            filePath = filePathCursor.getString(idx);

            filePathCursor.close();
        }
        return filePath;
    }

    private class RowFromCSV {

        public RowFromCSV(String csvRow) {
            String[] parts = csvRow.split(";");

            id = Integer.parseInt(parts[0]);
            categoryId = Integer.parseInt(parts[1]);
            value = Float.parseFloat(parts[2]);
            note = parts[3].substring(1, parts[3].length() - 1);
            date = Long.parseLong(parts[4]);
        }

        public int id;
        public int categoryId;
        public float value;
        public String note;
        public long date;
    }
}
