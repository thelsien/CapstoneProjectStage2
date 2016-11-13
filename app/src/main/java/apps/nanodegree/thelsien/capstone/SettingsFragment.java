package apps.nanodegree.thelsien.capstone;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by frodo on 2016. 11. 13..
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.main_preferences);
    }
}
