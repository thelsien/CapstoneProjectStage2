package apps.nanodegree.thelsien.capstone.data;

import android.net.Uri;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by frodo on 2016. 11. 07..
 */
@SimpleSQLTable(table = "MainCategories", provider = "CategoriesProvider")
public class MainCategoriesTableConfig {

    @SimpleSQLColumn(value = "_id", primary = true, autoincrement = true)
    public int _id;

    @SimpleSQLColumn("name")
    public String name;

    @SimpleSQLColumn("icon_res")
    public int iconRes;

    public static Uri getUriCategoryWithId(int categoryId) {
        return MainCategoriesTable.CONTENT_URI.buildUpon()
                .appendPath(Integer.toString(categoryId))
                .build();
    }
}
