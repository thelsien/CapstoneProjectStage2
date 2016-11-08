package apps.nanodegree.thelsien.capstone.data;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

/**
 * Created by frodo on 2016. 11. 08..
 */

@SimpleSQLTable(table = "SpendingsTable", provider = "CategoriesProvider")
public class SpendingsTableConfig {

    @SimpleSQLColumn(value = "id", primary = true, autoincrement = true)
    public int id;

    @SimpleSQLColumn(value = "category_id")
    public int categoryId;

    @SimpleSQLColumn(value = "value")
    public float value;

    @SimpleSQLColumn(value = "note")
    public String note;
}
