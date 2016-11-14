package apps.nanodegree.thelsien.capstone.asynctasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import apps.nanodegree.thelsien.capstone.data.IncomesTable;
import apps.nanodegree.thelsien.capstone.data.SpendingsTable;

/**
 * Created by frodo on 2016. 11. 14..
 */

public class ExportDataToCSVAsyncTask extends AsyncTask<Void, Void, Void> {

    private static final String TAG = ExportDataToCSVAsyncTask.class.getSimpleName();

    private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd_HH-mm-ss", Locale.getDefault());
    private Context mContext;

    public ExportDataToCSVAsyncTask(Context context) {
        super();

        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Calendar cal = Calendar.getInstance();

        String fileName = sdf.format(cal.getTime()) + ".csv";
        File folder = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
        );

        try {
            boolean isFileCreated = false;
            if (!folder.exists()) {
                isFileCreated = folder.createNewFile();
            }

            if (!isFileCreated) {
                return null;
            }

            String fullPathToFile = folder.toString();
            FileWriter fw = new FileWriter(fullPathToFile);
            Cursor spendingsCursor = mContext.getContentResolver().query(SpendingsTable.CONTENT_URI, null, null, null, null);

            fw.append("\"");
            fw.append(SpendingsTable.FIELD_ID);
            fw.append("\"");
            fw.append(";");

            fw.append("\"");
            fw.append(SpendingsTable.FIELD_CATEGORY_ID);
            fw.append("\"");
            fw.append(";");

            fw.append("\"");
            fw.append(SpendingsTable.FIELD_VALUE);
            fw.append("\"");
            fw.append(";");

            fw.append("\"");
            fw.append(SpendingsTable.FIELD_NOTE);
            fw.append("\"");
            fw.append(";");

            fw.append("\"");
            fw.append(SpendingsTable.FIELD_DATE);
            fw.append("\"");
            fw.append("\n");

            if (spendingsCursor != null) {
                spendingsCursor.moveToFirst();

                while (!spendingsCursor.isAfterLast()) {
                    fw.append(String.valueOf(spendingsCursor.getInt(spendingsCursor.getColumnIndex(SpendingsTable.FIELD_ID))));
                    fw.append(";");

                    fw.append(String.valueOf(spendingsCursor.getInt(spendingsCursor.getColumnIndex(SpendingsTable.FIELD_CATEGORY_ID))));
                    fw.append(";");

                    fw.append(String.valueOf(spendingsCursor.getFloat(spendingsCursor.getColumnIndex(SpendingsTable.FIELD_VALUE))));
                    fw.append(";");

                    fw.append("\"");
                    fw.append(spendingsCursor.getString(spendingsCursor.getColumnIndex(SpendingsTable.FIELD_NOTE)));
                    fw.append("\"");
                    fw.append(";");

                    fw.append(String.valueOf(spendingsCursor.getLong(spendingsCursor.getColumnIndex(SpendingsTable.FIELD_DATE))));

                    fw.append("\n");

                    spendingsCursor.moveToNext();
                }

                spendingsCursor.close();

            }

            Cursor incomesCursor = mContext.getContentResolver().query(IncomesTable.CONTENT_URI, null, null, null, null);

            if (incomesCursor != null) {
                incomesCursor.moveToFirst();

                while (!incomesCursor.isAfterLast()) {
                    fw.append(String.valueOf(incomesCursor.getInt(incomesCursor.getColumnIndex(IncomesTable.FIELD_ID))));
                    fw.append(";");

                    fw.append(String.valueOf(-1));
                    fw.append(";");

                    fw.append(String.valueOf(incomesCursor.getFloat(incomesCursor.getColumnIndex(IncomesTable.FIELD_VALUE))));
                    fw.append(";");

                    fw.append("\"");
                    fw.append(incomesCursor.getString(incomesCursor.getColumnIndex(IncomesTable.FIELD_NOTE)));
                    fw.append("\"");
                    fw.append(";");

                    fw.append(String.valueOf(incomesCursor.getLong(incomesCursor.getColumnIndex(IncomesTable.FIELD_DATE))));

                    fw.append("\n");

                    incomesCursor.moveToNext();

                    incomesCursor.moveToNext();
                }

                incomesCursor.close();
            }

            fw.flush();
            fw.close();
            Log.d(TAG, "File created, saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
