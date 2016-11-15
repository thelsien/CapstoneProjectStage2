package apps.nanodegree.thelsien.capstone;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.Vector;

import apps.nanodegree.thelsien.capstone.data.MainCategoriesTable;
import apps.nanodegree.thelsien.capstone.data.MainCategoriesTableConfig;

/**
 * Created by frodo on 2016. 11. 08..
 */

public class Utility {

    private static String[] categoryNames = new String[]{
            "Bills", "Car", "Clothes", "Communications",
            "Eating out", "Entertainment", "Food", "Gifts",
            "Health", "House", "Office", "Pets",
            "Sports", "Taxi", "Toiletry", "Transport"
    };

    private static int[] categoryIconIds = new int[]{
            R.drawable.ic_kitchen_black_48dp, R.drawable.ic_clear_black_48dp,
            R.drawable.ic_free_breakfast_black_48dp, R.drawable.ic_golf_course_black_48dp,
            R.drawable.ic_golf_course_black_48dp, R.drawable.ic_remove_circle_black_48dp,
            R.drawable.ic_kitchen_black_48dp, R.drawable.ic_kitchen_black_48dp,
            R.drawable.ic_kitchen_black_48dp, R.drawable.ic_kitchen_black_48dp,
            R.drawable.ic_kitchen_black_48dp, R.drawable.ic_kitchen_black_48dp,
            R.drawable.ic_kitchen_black_48dp, R.drawable.ic_kitchen_black_48dp,
            R.drawable.ic_kitchen_black_48dp, R.drawable.ic_kitchen_black_48dp
    };

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
                config.name = categoryNames[i];
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

        Log.d("StartTimeCalculation", "before: " + String.valueOf(cal.getTimeInMillis() / 1000));

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

        Log.d("StartTimeCalculation", "after: " + String.valueOf(cal.getTimeInMillis() / 1000));

        return cal.getTimeInMillis() / 1000;
    }

    public static long getEndTimeForQuery(Context context) {
        Calendar cal = Calendar.getInstance();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String timeIntervalKey = prefs.getString(context.getString(R.string.prefs_time_interval), context.getString(R.string.default_time_interval_month));

        Log.d("StartTimeCalculation", "before: " + String.valueOf(cal.getTimeInMillis() / 1000));

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

        Log.d("StartTimeCalculation", "after: " + String.valueOf(cal.getTimeInMillis() / 1000));

        return cal.getTimeInMillis() / 1000;
    }
}
