package apps.nanodegree.thelsien.capstone;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

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
}
