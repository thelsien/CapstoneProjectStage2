package apps.nanodegree.thelsien.capstone;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by frodo on 2016. 11. 13..
 */

public class SettingsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
