package apps.nanodegree.thelsien.capstone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.Vector;

import apps.nanodegree.thelsien.capstone.data.MainCategoriesTable;

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

    public static void setupMainCategories(Context context) {
        Cursor c = context.getContentResolver().query(MainCategoriesTable.CONTENT_URI, null, null, null, null);

        if (c != null && !c.moveToFirst()) {
            c.close();

            Vector<ContentValues> cVVector = new Vector<>();

            for (int i = 0; i < categoryNames.length; i++) {
                ContentValues values = new ContentValues();
                values.put("name", categoryNames[i]);
                values.put("icon_res", categoryIconIds[i]);
                cVVector.add(values);
            }

            ContentValues[] cVArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cVArray);

            context.getContentResolver().bulkInsert(MainCategoriesTable.CONTENT_URI, cVArray);
        }
    }
}
