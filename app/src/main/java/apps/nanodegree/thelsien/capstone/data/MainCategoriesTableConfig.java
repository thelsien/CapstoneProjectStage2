package apps.nanodegree.thelsien.capstone.data;

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
}
