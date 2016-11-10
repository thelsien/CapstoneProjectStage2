package apps.nanodegree.thelsien.capstone;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by frodo on 2016. 11. 10..
 */

public class AddEditEntryActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_IS_ADD_ENTRY = "is_add_entry";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_edit_entry);

        boolean isAddEntry = getIntent().getBooleanExtra(INTENT_EXTRA_IS_ADD_ENTRY, true);

        if (isAddEntry) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.category_add_edit_container, new AddEntryFragment())
                    .commit();
        }
    }
}
