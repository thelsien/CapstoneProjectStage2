package apps.nanodegree.thelsien.capstone;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Vector;

import apps.nanodegree.thelsien.capstone.data.IncomesTable;
import apps.nanodegree.thelsien.capstone.data.MainCategoriesTable;
import apps.nanodegree.thelsien.capstone.data.MainCategoriesTableConfig;
import apps.nanodegree.thelsien.capstone.data.SpendingsTable;
import apps.nanodegree.thelsien.capstone.widget.MoneyTrackRWidgetProvider;

/**
 * Created by frodo on 2016. 11. 08..
 */

public class Utility {

    private static int[] categoryNames = new int[]{
            R.string.category_bills, R.string.category_car, R.string.category_clothes, R.string.category_comms,
            R.string.category_eating_out, R.string.category_entertainment, R.string.category_food, R.string.category_gifts,
            R.string.category_health, R.string.category_house, R.string.category_office, R.string.category_pets,
            R.string.category_sports, R.string.category_taxi, R.string.category_toiletry, R.string.category_transport
    };

    private static int[] categoryIconIds = new int[]{
            R.drawable.banknotes, R.drawable.sedan,
            R.drawable.shirt, R.drawable.phone,
            R.drawable.restaurant, R.drawable.controller,
            R.drawable.vegetarian_food, R.drawable.gift,
            R.drawable.thermometer, R.drawable.home,
            R.drawable.apartment, R.drawable.cat,
            R.drawable.sport, R.drawable.taxi,
            R.drawable.toothpaste, R.drawable.train
    };

    public static boolean isNetworkAvailable(Context context) {
        boolean isNetworkAvailable = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnected()) {
            isNetworkAvailable = true;
        }

        return isNetworkAvailable;
    }

    public static NumberFormat getValueFormatWithCurrency(Context context) {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        format.setCurrency(Currency.getInstance(
                PreferenceManager.getDefaultSharedPreferences(context).getString(
                        context.getString(R.string.prefs_current_currency_key),
                        context.getString(R.string.default_currency))
                )
        );
        return format;
    }

    public static void setupFirstRunDatas(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isFirstRunDone = prefs.getBoolean(context.getResources().getString(R.string.prefs_is_first_run_done), false);

        if (!isFirstRunDone) {
            setupMainCategories(context);
            setupDefaultCurrency(context, prefs);
            prefs.edit()
                    .putBoolean(context.getResources().getString(R.string.prefs_is_first_run_done), true)
                    .commit();
        }
    }

    private static void setupMainCategories(Context context) {
        Cursor c = context.getContentResolver().query(MainCategoriesTable.CONTENT_URI, null, null, null, null);

        if (c != null) {
            c.close();

            Vector<ContentValues> cVVector = new Vector<>();

            for (int i = 0; i < categoryNames.length; i++) {
                MainCategoriesTableConfig config = new MainCategoriesTableConfig();
                config.nameRes = categoryNames[i];
                config.iconRes = categoryIconIds[i];
                cVVector.add(MainCategoriesTable.getContentValues(config, false));
            }

            ContentValues[] cVArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cVArray);

            context.getContentResolver().bulkInsert(MainCategoriesTable.CONTENT_URI, cVArray);
        }
    }

    private static void setupDefaultCurrency(Context context, SharedPreferences prefs) {
        prefs.edit()
                .putString(context.getString(R.string.prefs_current_currency_key), context.getString(R.string.default_currency))
                .putString(context.getString(R.string.prefs_source_currency_key), context.getString(R.string.default_currency))
                .apply();
    }

    public static long getStartTimeForQuery(Context context) {
        Calendar cal = Calendar.getInstance();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String timeIntervalKey = prefs.getString(context.getString(R.string.prefs_time_interval), context.getString(R.string.default_time_interval_month));

        if (timeIntervalKey.equals(context.getString(R.string.time_interval_day))) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        } else if (timeIntervalKey.equals(context.getString(R.string.default_time_interval_month))) {
            cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        } else if (timeIntervalKey.equals(context.getString(R.string.time_interval_year))) {
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        } else if (timeIntervalKey.equals(context.getString(R.string.time_interval_custom))) {
            int customYearStart = prefs.getInt(context.getString(R.string.custom_interval_start_year), 0);
            int customMonthStart = prefs.getInt(context.getString(R.string.custom_interval_start_month), 0);
            int customDayStart = prefs.getInt(context.getString(R.string.custom_interval_start_day), 0);

            cal.set(Calendar.YEAR, customYearStart);
            cal.set(Calendar.MONTH, customMonthStart);
            cal.set(Calendar.DAY_OF_MONTH, customDayStart);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        }

        return cal.getTimeInMillis() / 1000;
    }

    public static long getEndTimeForQuery(Context context) {
        Calendar cal = Calendar.getInstance();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String timeIntervalKey = prefs.getString(context.getString(R.string.prefs_time_interval), context.getString(R.string.default_time_interval_month));

        if (timeIntervalKey.equals(context.getString(R.string.time_interval_day))) {
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
        } else if (timeIntervalKey.equals(context.getString(R.string.default_time_interval_month))) {
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
        } else if (timeIntervalKey.equals(context.getString(R.string.time_interval_year))) {
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
        } else if (timeIntervalKey.equals(context.getString(R.string.time_interval_custom))) {
            int customYearEnd = prefs.getInt(context.getString(R.string.custom_interval_end_year), 0);
            int customMonthEnd = prefs.getInt(context.getString(R.string.custom_interval_end_month), 0);
            int customDayEnd = prefs.getInt(context.getString(R.string.custom_interval_end_day), 0);

            cal.set(Calendar.YEAR, customYearEnd);
            cal.set(Calendar.MONTH, customMonthEnd);
            cal.set(Calendar.DAY_OF_MONTH, customDayEnd);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
        }

        return cal.getTimeInMillis() / 1000;
    }

    public static void deleteSpendingsAndIncomes(Context context) {
        context.getContentResolver().delete(SpendingsTable.CONTENT_URI, null, null);
        context.getContentResolver().delete(IncomesTable.CONTENT_URI, null, null);
    }

    public static float getCategoryValue(Context context, int categoryId) {
        long startDate = Utility.getStartTimeForQuery(context);
        long endDate = Utility.getEndTimeForQuery(context);
        float sum = 0;
        Cursor c = context.getContentResolver().query(
                SpendingsTable.CONTENT_URI,
                new String[]{SpendingsTable.FIELD_VALUE, SpendingsTable.FIELD_DATE},
                SpendingsTable.FIELD_CATEGORY_ID + " = ? AND " + SpendingsTable.FIELD_DATE + " < ? AND " + SpendingsTable.FIELD_DATE + " >= ?",
                new String[]{String.valueOf(categoryId), String.valueOf(endDate), String.valueOf(startDate)},
                null
        );

        if (c != null) {
            c.moveToFirst();

            while (!c.isAfterLast()) {
                sum += c.getFloat(c.getColumnIndex(SpendingsTable.FIELD_VALUE));
                c.moveToNext();
            }

            c.close();
        }

        return sum;
    }

    public static void notifyThroughContentResolver(Context context) {
        context.getContentResolver().notifyChange(MainCategoriesTable.CONTENT_URI, null);
        context.getContentResolver().notifyChange(SpendingsTable.CONTENT_URI, null);
        context.getContentResolver().notifyChange(IncomesTable.CONTENT_URI, null);
    }

    public static String getCurrentCurrency(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(context.getString(R.string.prefs_current_currency_key), context.getString(R.string.default_currency));
    }

    public static float getIncomesSum(Context context) {
        long startDate = getStartTimeForQuery(context);
        long endDate = getEndTimeForQuery(context);
        float incomesSum = 0;

        Cursor c = context.getContentResolver().query(
                IncomesTable.CONTENT_URI,
                new String[]{IncomesTable.FIELD_VALUE},
                IncomesTable.FIELD_DATE + " < ? AND " + IncomesTable.FIELD_DATE + " >= ?",
                new String[]{String.valueOf(endDate), String.valueOf(startDate)},
                null
        );

        if (c != null) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                incomesSum += c.getFloat(c.getColumnIndex(IncomesTable.FIELD_VALUE));
                c.moveToNext();
            }
            c.close();
        }
        return incomesSum;
    }

    public static float getSpendingsSum(Context context) {
        long startDate = getStartTimeForQuery(context);
        long endDate = getEndTimeForQuery(context);
        float spendingsSum = 0;

        Cursor c2 = context.getContentResolver().query(
                SpendingsTable.CONTENT_URI,
                new String[]{IncomesTable.FIELD_VALUE},
                SpendingsTable.FIELD_DATE + " < ? AND " + SpendingsTable.FIELD_DATE + " >= ?",
                new String[]{String.valueOf(endDate), String.valueOf(startDate)},
                null
        );

        if (c2 != null) {
            c2.moveToFirst();
            while (!c2.isAfterLast()) {
                spendingsSum += c2.getFloat(c2.getColumnIndex(SpendingsTable.FIELD_VALUE));
                c2.moveToNext();
            }
            c2.close();
        }
        return spendingsSum;
    }

    public static void updateWidgets(Context context) {
        Intent dataUpdatedIntent = new Intent(MoneyTrackRWidgetProvider.ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    public static void trackScreen(Context context, String screenName) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, screenName);

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
    }

    public static void trackEvent(Context context, String category, String action, String label) {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        Bundle params = new Bundle();
        params.putString("category", category);
        params.putString("action", action);
        params.putString("label", label);

        firebaseAnalytics.logEvent("ga_event", params);
    }
}
