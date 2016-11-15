package apps.nanodegree.thelsien.capstone.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import apps.nanodegree.thelsien.capstone.R;
import apps.nanodegree.thelsien.capstone.data.IncomesTable;
import apps.nanodegree.thelsien.capstone.data.IncomesTableConfig;
import apps.nanodegree.thelsien.capstone.data.MainCategoriesTable;
import apps.nanodegree.thelsien.capstone.data.SpendingsTable;
import apps.nanodegree.thelsien.capstone.data.SpendingsTableConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by frodo on 2016. 11. 13..
 */

public class CurrencyChangeAsyncTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = CurrencyChangeAsyncTask.class.getSimpleName();
    private static final String CURRENCY_CHANGE_RATE_QUERY_URL_BASE = "http://api.fixer.io/latest?";

    private Context mContext;

    public CurrencyChangeAsyncTask(Context context) {
        super();
        mContext = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String sourceCurrency = params[0];
        String targetCurrency = params[1];
        String url = CURRENCY_CHANGE_RATE_QUERY_URL_BASE + "base=" + sourceCurrency + "&symbols=" + targetCurrency;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();

            String jsonString = response.body().string();
            JSONObject responseData = new JSONObject(jsonString);
            Log.d(TAG, jsonString);

            double changeRate = responseData.optJSONObject("rates").optDouble(targetCurrency);

            Cursor spendingsCursor = mContext.getContentResolver().query(
                    SpendingsTable.CONTENT_URI,
                    null, null, null, null
            );

            Cursor incomesCursor = mContext.getContentResolver().query(
                    IncomesTable.CONTENT_URI,
                    null, null, null, null
            );

            if (spendingsCursor != null) {
                spendingsCursor.moveToFirst();

                while (!spendingsCursor.isAfterLast()) {
                    SpendingsTableConfig config = new SpendingsTableConfig();
                    config.id = spendingsCursor.getInt(spendingsCursor.getColumnIndex(SpendingsTable.FIELD_ID));
                    config.categoryId = spendingsCursor.getInt(spendingsCursor.getColumnIndex(SpendingsTable.FIELD_CATEGORY_ID));
                    config.note = spendingsCursor.getString(spendingsCursor.getColumnIndex(SpendingsTable.FIELD_NOTE));
                    config.date = spendingsCursor.getLong(spendingsCursor.getColumnIndex(SpendingsTable.FIELD_DATE));
                    config.value = (float) (spendingsCursor.getFloat(spendingsCursor.getColumnIndex(SpendingsTable.FIELD_VALUE)) * changeRate);

                    mContext.getContentResolver().update(
                            SpendingsTable.CONTENT_URI,
                            SpendingsTable.getContentValues(config, true),
                            SpendingsTable.FIELD_ID + " = ?",
                            new String[]{String.valueOf(config.id)}
                    );

                    spendingsCursor.moveToNext();
                }

                spendingsCursor.close();
            }

            if (incomesCursor != null) {
                incomesCursor.moveToFirst();

                while (!incomesCursor.isAfterLast()) {
                    IncomesTableConfig config = new IncomesTableConfig();
                    config.id = incomesCursor.getInt(incomesCursor.getColumnIndex(IncomesTable.FIELD_ID));
                    config.note = incomesCursor.getString(incomesCursor.getColumnIndex(IncomesTable.FIELD_NOTE));
                    config.date = incomesCursor.getLong(incomesCursor.getColumnIndex(IncomesTable.FIELD_DATE));
                    config.value = (float) (incomesCursor.getFloat(incomesCursor.getColumnIndex(IncomesTable.FIELD_VALUE)) * changeRate);

                    mContext.getContentResolver().update(
                            IncomesTable.CONTENT_URI,
                            IncomesTable.getContentValues(config, true),
                            IncomesTable.FIELD_ID + " = ?",
                            new String[]{String.valueOf(config.id)}
                    );

                    incomesCursor.moveToNext();
                }

                incomesCursor.close();
            }


            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

            prefs.edit()
                    .putString(mContext.getResources().getString(R.string.prefs_source_currency_key), targetCurrency)
                    .commit();

            mContext.getContentResolver().notifyChange(MainCategoriesTable.CONTENT_URI, null);

            return true;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
