package apps.nanodegree.thelsien.capstone.data;

import ckm.simple.sql_provider.UpgradeScript;
import ckm.simple.sql_provider.annotation.ProviderConfig;
import ckm.simple.sql_provider.annotation.SimpleSQLConfig;

/**
 * Created by frodo on 2016. 11. 07..
 */
@SimpleSQLConfig(
        name = "CategoriesProvider",
        authority = "apps.nanodegree.thelsien.capstone.authority",
        database = "categories.db",
        version = 1
)
public class CategoriesProviderConfig implements ProviderConfig {
    @Override
    public UpgradeScript[] getUpdateScripts() {
        return new UpgradeScript[0];
    }
}
